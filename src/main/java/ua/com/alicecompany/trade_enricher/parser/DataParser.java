package ua.com.alicecompany.trade_enricher.parser;

import ua.com.alicecompany.trade_enricher.model.Trade;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DataParser {
    List<Trade> parseData(InputStream inputStream) throws IOException;
}