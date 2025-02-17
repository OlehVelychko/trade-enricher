package ua.com.alicecompany.trade_enricher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Включаем кэш Redis
public class TradeEnricherApplication {
	public static void main(String[] args) {
		SpringApplication.run(TradeEnricherApplication.class, args);
	}
}