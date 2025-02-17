package ua.com.alicecompany.trade_enricher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.com.alicecompany.trade_enricher.service.TradeService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/enrich")
    public ResponseEntity<List<String>> enrichTrades(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String> enrichedTrades = tradeService.processTrades(reader);
            return ResponseEntity.ok(enrichedTrades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of("Error processing file: " + e.getMessage()));
        }
    }
}