package ua.com.alicecompany.trade_enricher.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TradeService {
    private final StringRedisTemplate redisTemplate;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public TradeService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void processTrades(BufferedReader reader, PrintWriter writer) throws IOException {
        AtomicInteger processedLines = new AtomicInteger(0);
        Set<String> missingProducts = new HashSet<>();
        Set<String> uniqueTrades = new HashSet<>();

        try (CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim());
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            for (CSVRecord record : parser) {
                try {
                    String date = record.get("date");
                    String productId = record.get("productId");
                    String currency = record.get("currency");
                    String price = record.get("price");

                    if (!isValidDate(date)) {
                        System.err.println("❌ Invalid date format: " + date);
                        continue;
                    }

                    String productName = redisTemplate.opsForValue().get("product:" + productId);
                    if (productName == null) {
                        if (!missingProducts.contains(productId)) {
                            System.err.println("⚠️ Missing mapping for productId: " + productId);
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
                        System.out.println("✅ Processed lines: " + processedLines.get());
                    }
                } catch (Exception e) {
                    System.err.println("🚨 Error processing record: " + record);
                }
            }

            bufferedWriter.flush(); // Финальный flush
            System.out.println("🏁 Total processed lines: " + processedLines.get());
        }
    }

    private boolean isValidDate(String date) {
        try {
            DATE_FORMAT.setLenient(false);
            DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}