package ua.com.alicecompany.trade_enricher.parser;

public interface ParserFactory {
    DataParser getParser(String contentType);
}