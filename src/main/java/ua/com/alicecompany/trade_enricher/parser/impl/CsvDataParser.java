package ua.com.alicecompany.trade_enricher.parser.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.com.alicecompany.trade_enricher.model.Trade;
import ua.com.alicecompany.trade_enricher.parser.DataParser;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

@Component
public class CsvDataParser implements DataParser {
    private static final Logger logger = LoggerFactory.getLogger(CsvDataParser.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    public List<Trade> parseData(InputStream inputStream) throws IOException {
        List<Trade> trades = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] line;

            // Skip headers
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                try {
                    if (line.length < 4) {
                        logger.warn("Skipping invalid row (not enough columns): {}", (Object) line);
                        continue;
                    }

                    Trade trade = new Trade();
                    trade.setDate(parseDateSafely(line[0]));
                    trade.setProductId(parseInteger(line[1]));
                    trade.setCurrency(line[2]);
                    trade.setPrice(parseBigDecimal(line[3]));

                    if (trade.getDate() != null) {
                        trades.add(trade);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Skipping invalid trade record: {}", (Object) line, e);
                }
            }
        } catch (CsvValidationException e) {
            logger.error("Error reading CSV file", e);
            throw new IOException("Failed to parse CSV file", e);
        }

        return trades;
    }

    private java.util.Date parseDateSafely(String dateStr) {
        try {
            DATE_FORMAT.setLenient(false);
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            logger.warn("Invalid date format, skipping record: {}", dateStr);
            return null;
        }
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format: {}", value);
            throw new IllegalArgumentException("Invalid integer format: " + value, e);
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid decimal format: {}", value);
            throw new IllegalArgumentException("Invalid decimal format: " + value, e);
        }
    }
}