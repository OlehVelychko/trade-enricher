package ua.com.alicecompany.trade_enricher.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private Date date;
    private int productId;
    private String currency;
    private BigDecimal price;
}