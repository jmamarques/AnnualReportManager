package cod.batch.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CryptoTransaction {

    private String dateUtc;
    private String pair;
    private TransactionSide side;
    private BigDecimal price;
    private CurrencyAmount executed;
    private CurrencyAmount amount;
    private CurrencyAmount fee;
}
