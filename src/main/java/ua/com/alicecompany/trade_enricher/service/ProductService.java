package ua.com.alicecompany.trade_enricher.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final StringRedisTemplate redisTemplate;

    public ProductService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void loadProductsIntoRedis() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("largeSizeProduct.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            if (inputStream == null) {
                logger.error("largeSizeProduct.csv not found in resources.");
                return;
            }

            String line;
            br.readLine(); // Skip header

            Map<String, String> batchInsert = new HashMap<>();
            int batchSize = 5000;
            int totalRecords = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    batchInsert.put("product:" + parts[0], parts[1]);
                    totalRecords++;
                }

                if (batchInsert.size() >= batchSize) {
                    redisTemplate.opsForValue().multiSet(batchInsert);
                    batchInsert.clear();
                    logger.info("Inserted {} product mappings into Redis.", totalRecords);
                }
            }

            if (!batchInsert.isEmpty()) {
                redisTemplate.opsForValue().multiSet(batchInsert);
                logger.info("Final batch inserted. Total products loaded: {}", totalRecords);
            }
        } catch (Exception e) {
            logger.error("Error loading products into Redis", e);
        }
    }

    public String getProductName(String productId) {
        return redisTemplate.opsForValue().get("product:" + productId);
    }
}