package ua.com.alicecompany.trade_enricher.service;

import jakarta.annotation.PostConstruct;
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
    private final StringRedisTemplate redisTemplate;

    public ProductService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void loadProductsIntoRedis() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("largeSizeProduct.csv")) {
            if (inputStream == null) {
                throw new IllegalStateException("The resource 'largeSizeProduct.csv' was not found.");
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                br.readLine(); // Skip the header

                Map<String, String> batchInsert = new HashMap<>();
                int batchSize = 5000;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        batchInsert.put("product:" + parts[0], parts[1]);
                    }

                    if (batchInsert.size() >= batchSize) {
                        redisTemplate.opsForValue().multiSet(batchInsert);
                        batchInsert.clear();
                    }
                }

                if (!batchInsert.isEmpty()) {
                    redisTemplate.opsForValue().multiSet(batchInsert);
                }

                System.out.println("Products loaded into Redis successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getProductName(String productId) {
        return redisTemplate.opsForValue().get("product:" + productId);
    }
}