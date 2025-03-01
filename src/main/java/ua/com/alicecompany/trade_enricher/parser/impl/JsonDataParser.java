package ua.com.alicecompany.trade_enricher.parser.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ua.com.alicecompany.trade_enricher.model.Trade;
import ua.com.alicecompany.trade_enricher.parser.DataParser;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

@Component
public class JsonDataParser implements DataParser {
    @Override
    public List<Trade> parseData(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, Trade.class));
    }
}