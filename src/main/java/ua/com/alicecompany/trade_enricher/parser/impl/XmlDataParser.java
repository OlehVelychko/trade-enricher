package ua.com.alicecompany.trade_enricher.parser.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;
import ua.com.alicecompany.trade_enricher.model.Trade;
import ua.com.alicecompany.trade_enricher.parser.DataParser;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

@Component
public class XmlDataParser implements DataParser {
    @Override
    public List<Trade> parseData(InputStream inputStream) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(inputStream, xmlMapper.getTypeFactory().constructCollectionType(List.class, Trade.class));
    }
}