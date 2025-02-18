package ua.com.alicecompany.trade_enricher.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.com.alicecompany.trade_enricher.model.Trade;
import ua.com.alicecompany.trade_enricher.parser.DataParser;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TradeService {
    private final StringRedisTemplate redisTemplate;
    private final DataParser dataParser; // Dependency Injection for DataParser

    // Constructor explicitly specifying csvDataParser with @Qualifier
    public TradeService(StringRedisTemplate redisTemplate, @Qualifier("csvDataParser") DataParser dataParser) {
        this.redisTemplate = redisTemplate;
        this.dataParser = dataParser;
    }

    @Async
    public CompletableFuture<Void> processTradesAsync(InputStream inputStream, PrintWriter writer) {
        return CompletableFuture.runAsync(() -> {
            AtomicInteger processedLines = new AtomicInteger(0);
            Set<String> missingProducts = new HashSet<>();
            Set<String> uniqueTrades = new HashSet<>();

            try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                List<Trade> trades = dataParser.parseData(inputStream); // Using DataParser to process data

                for (Trade trade : trades) {
                    try {
                        String date = formatDate(trade.getDate());
                        String productId = String.valueOf(trade.getProductId());
                        String currency = trade.getCurrency();
                        String price = trade.getPrice().toString();

                        String productName = redisTemplate.opsForValue().get("product:" + productId);
                        if (productName == null) {
                            if (!missingProducts.contains(productId)) {
                                System.err.println("Missing mapping for productId: " + productId);
                                missingProducts.add(productId);
                            }
                            productName = "Missing Product Name";
                        }

                        String tradeEntry = String.join(",", date, productName, currency, price);
                        if (!uniqueTrades.contains(tradeEntry)) {
                            uniqueTrades.add(tradeEntry);
                            bufferedWriter.write(tradeEntry);
                            bufferedWriter.newLine();
                        }

                        if (processedLines.incrementAndGet() % 10000 == 0) {
                            bufferedWriter.flush();
                            System.out.println("Processed lines: " + processedLines.get());
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing trade: " + trade);
                    }
                }

                bufferedWriter.flush(); // Final flush
                System.out.println("Total processed lines: " + processedLines.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Method for formatting date into a string
    private String formatDate(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }
}