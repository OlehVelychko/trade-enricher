package ua.com.alicecompany.trade_enricher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.com.alicecompany.trade_enricher.model.Trade;
import ua.com.alicecompany.trade_enricher.parser.DataParser;
import ua.com.alicecompany.trade_enricher.parser.ParserFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TradeService {
    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);
    private final StringRedisTemplate redisTemplate;
    private final ParserFactory parserFactory;

    public TradeService(StringRedisTemplate redisTemplate, ParserFactory parserFactory) {
        this.redisTemplate = redisTemplate;
        this.parserFactory = parserFactory;
    }

    @Async
    public CompletableFuture<Void> processTradesAsync(InputStream inputStream, PrintWriter writer, String contentType) {
        return CompletableFuture.runAsync(() -> {
            AtomicInteger processedLines = new AtomicInteger(0);
            Set<String> missingProducts = new HashSet<>();
            Set<String> uniqueTrades = new HashSet<>();
            boolean hasErrors = false;

            try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                DataParser dataParser = parserFactory.getParser(contentType);
                List<Trade> trades = dataParser.parseData(inputStream);

                for (Trade trade : trades) {
                    try {
                        String date = formatDate(trade.getDate());
                        String productId = String.valueOf(trade.getProductId());
                        String currency = trade.getCurrency();
                        String price = trade.getPrice().toString();

                        String productName = redisTemplate.opsForValue().get("product:" + productId);
                        if (productName == null) {
                            if (missingProducts.add(productId)) {
                                logger.warn("Missing mapping for productId: {}", productId);
                            }
                            productName = "Missing Product Name";
                        }

                        String tradeEntry = String.join(",", date, productName, currency, price);
                        if (uniqueTrades.add(tradeEntry)) {
                            bufferedWriter.write(tradeEntry);
                            bufferedWriter.newLine();
                        }

                        if (processedLines.incrementAndGet() % 10000 == 0) {
                            bufferedWriter.flush();
                            logger.info("Processed lines: {}", processedLines.get());
                        }
                    } catch (Exception e) {
                        hasErrors = true;
                        logger.error("Error processing trade: {}", trade, e);
                    }
                }

                bufferedWriter.flush();
                logger.info("Total processed lines: {}", processedLines.get());
            } catch (IOException e) {
                logger.error("Error writing trades to output", e);
            } finally {
                if (hasErrors) {
                    throw new RuntimeException("Errors occurred during trade processing");
                }
            }
        });
    }

    private String formatDate(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }
}