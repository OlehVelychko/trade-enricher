package ua.com.alicecompany.trade_enricher.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.com.alicecompany.trade_enricher.service.TradeService;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping(value = "/enrich", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> enrichTrades(@RequestParam("file") MultipartFile file) {
        StreamingResponseBody stream = outputStream -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                 PrintWriter writer = new PrintWriter(outputStream)) {
                tradeService.processTrades(reader, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"enriched_trades.csv\"")
                .body(stream);
    }
}