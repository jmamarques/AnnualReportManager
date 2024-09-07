package cod.crypto.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CryptoTransaction {

    private LocalDateTime dateUtc;
    private String pair;
    private TransactionSide side;
    private BigDecimal price;
    private CurrencyAmount executed;
    private CurrencyAmount amount;
    private CurrencyAmount fee;

    public String getFormattedDateUtc() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateUtc != null ? dateUtc.format(formatter) : null;
    }
}
