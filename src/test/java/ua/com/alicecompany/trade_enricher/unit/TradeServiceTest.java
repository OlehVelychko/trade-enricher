package ua.com.alicecompany.trade_enricher.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import ua.com.alicecompany.trade_enricher.model.Trade;
import ua.com.alicecompany.trade_enricher.parser.DataParser;
import ua.com.alicecompany.trade_enricher.parser.ParserFactory;
import ua.com.alicecompany.trade_enricher.service.TradeService;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private DataParser dataParser;

    @Mock
    private ParserFactory parserFactory;

    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        tradeService = new TradeService(redisTemplate, parserFactory);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(parserFactory.getParser(anyString())).thenReturn(dataParser);
    }

    @Test
    void shouldProcessValidTradesCorrectly() throws Exception {
        Trade trade = new Trade(new Date(), 1, "USD", BigDecimal.valueOf(100.25));
        when(dataParser.parseData(any(InputStream.class))).thenReturn(Collections.singletonList(trade));
        when(valueOperations.get("product:1")).thenReturn("Treasury Bills Domestic");

        InputStream inputStream = new ByteArrayInputStream("date,productId,currency,price\n20230101,1,USD,100.25".getBytes(StandardCharsets.UTF_8));
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        CompletableFuture<Void> future = tradeService.processTradesAsync(inputStream, writer, "text/csv");

        await().atMost(5, TimeUnit.SECONDS).until(future::isDone);

        verify(valueOperations, times(1)).get("product:1");
    }

    @Test
    void shouldHandleMissingProductName() throws Exception {
        Trade trade = new Trade(new Date(), 99, "EUR", BigDecimal.valueOf(200.45));
        when(dataParser.parseData(any(InputStream.class))).thenReturn(Collections.singletonList(trade));
        when(valueOperations.get("product:99")).thenReturn(null);

        InputStream inputStream = new ByteArrayInputStream("date,productId,currency,price\n20230101,99,EUR,200.45".getBytes(StandardCharsets.UTF_8));
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        CompletableFuture<Void> future = tradeService.processTradesAsync(inputStream, writer, "text/csv");
        future.join();

        verify(valueOperations, times(1)).get("product:99");
    }
}