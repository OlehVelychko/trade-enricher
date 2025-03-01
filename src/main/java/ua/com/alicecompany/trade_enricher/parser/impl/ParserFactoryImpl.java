package ua.com.alicecompany.trade_enricher.parser.impl;

import org.springframework.stereotype.Component;
import ua.com.alicecompany.trade_enricher.parser.DataParser;
import ua.com.alicecompany.trade_enricher.parser.ParserFactory;

import java.util.Map;

@Component
public class ParserFactoryImpl implements ParserFactory {
    private final Map<String, DataParser> parserMap;

    public ParserFactoryImpl(CsvDataParser csvDataParser, JsonDataParser jsonDataParser, XmlDataParser xmlDataParser) {
        this.parserMap = Map.of(
                "text/csv", csvDataParser,
                "application/json", jsonDataParser,
                "application/xml", xmlDataParser
        );
    }

    @Override
    public DataParser getParser(String contentType) {
        return parserMap.getOrDefault(contentType, parserMap.get("text/csv"));
    }
}