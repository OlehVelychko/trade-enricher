package ua.com.alicecompany.trade_enricher.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import ua.com.alicecompany.trade_enricher.service.ProductService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldReturnProductNameWhenExistsInRedis() {
        // Given
        when(valueOperations.get("product:1")).thenReturn("Treasury Bills Domestic");

        // When
        String productName = productService.getProductName("1");

        // Then
        assertEquals("Treasury Bills Domestic", productName);
        verify(valueOperations, times(1)).get("product:1");
    }

    @Test
    void shouldReturnNullWhenProductDoesNotExistInRedis() {
        // Given
        when(valueOperations.get("product:99")).thenReturn(null);

        // When
        String productName = productService.getProductName("99");

        // Then
        assertEquals(null, productName);
        verify(valueOperations, times(1)).get("product:99");
    }
}