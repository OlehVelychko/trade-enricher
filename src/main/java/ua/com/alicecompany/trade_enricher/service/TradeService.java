package ua.com.alicecompany.trade_enricher.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradeService {
    private final StringRedisTemplate redisTemplate;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public TradeService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<String> processTrades(BufferedReader reader) throws java.io.IOException {
        List<String> enrichedTrades = new ArrayList<>();
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        for (CSVRecord record : parser) {
            String date = record.get("date");
            String productId = record.get("productId");
            String currency = record.get("currency");
            String price = record.get("price");

            if (!isValidDate(date)) {
                System.err.println("Invalid date format: " + date);
                continue;
            }

            String productName = redisTemplate.opsForValue().get("product:" + productId);
            if (productName == null) {
                System.err.println("Missing mapping for productId: " + productId);
                productName = "Missing Product Name";
            }

            enrichedTrades.add(String.join(",", date, productName, currency, price));
        }
        return enrichedTrades;
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
