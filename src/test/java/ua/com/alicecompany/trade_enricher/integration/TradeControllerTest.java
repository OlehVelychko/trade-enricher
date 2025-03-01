package ua.com.alicecompany.trade_enricher.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TradeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldEnrichTradesSuccessfully() throws Exception {
        // Given
        String csvContent = "date,productId,currency,price\n20230101,1,USD,100.25";
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", csvContent.getBytes());

        // When & Then
        mockMvc.perform(multipart("/api/v1/trades/enrich").file(file))
                .andExpect(status().isOk());
    }
}