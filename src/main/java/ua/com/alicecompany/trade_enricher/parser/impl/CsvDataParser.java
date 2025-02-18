package ua.com.alicecompany.trade_enricher.parser.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
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
    @Override
    public List<Trade> parseData(InputStream inputStream) throws IOException {
        List<Trade> trades = new ArrayList<>();
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
        String[] line;

        // Skip the header row
        try {
            reader.readNext();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e); // Error reading header
        }

        while (true) {
            try {
                line = reader.readNext();
                if (line == null) break;
            } catch (CsvValidationException e) {
                throw new RuntimeException(e); // Error reading data
            }
            if (line.length < 4) continue;  // Skip rows with insufficient data

            Trade trade = new Trade();

            // Convert and populate fields
            trade.setDate(parseDate(line[0]));  // Convert date
            trade.setProductId(Integer.parseInt(line[1]));  // Convert to Integer
            trade.setCurrency(line[2]);
            trade.setPrice(new BigDecimal(line[3]));  // Convert price to BigDecimal

            trades.add(trade);
        }
        return trades;
    }

    // Method to convert date from yyyyMMdd format to Date object
    private static java.util.Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            // Log error and return null to allow the program to continue
            System.err.println("Invalid date format: " + dateStr);
            return null;
        }
    }
}