package ua.com.alicecompany.trade_enricher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableCaching // Включаем кэш Redis
@EnableAsync   // Включаем асинхронную обработку
public class TradeEnricherApplication {
	public static void main(String[] args) {
		SpringApplication.run(TradeEnricherApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("AsyncTradeProcessor-");
		executor.initialize();
		return executor;
	}
}
