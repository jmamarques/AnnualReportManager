package cod.crypto.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrencyAmount {

    private BigDecimal amount;
    private String currency;
}
