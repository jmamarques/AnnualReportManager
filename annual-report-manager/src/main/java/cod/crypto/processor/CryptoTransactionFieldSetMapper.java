package cod.crypto.processor;

import cod.crypto.model.CryptoTransaction;
import cod.crypto.model.CurrencyAmount;
import cod.crypto.model.TransactionSide;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cod.util.GeneralConst.DATE_TIME_FORMATTER;

public class CryptoTransactionFieldSetMapper implements FieldSetMapper<CryptoTransaction> {

    @NotNull
    @Override
    public CryptoTransaction mapFieldSet(FieldSet fieldSet) {
        CryptoTransaction transaction = new CryptoTransaction();
        transaction.setDateUtc(LocalDateTime.parse(fieldSet.readString("Date(UTC)"), DATE_TIME_FORMATTER));
        transaction.setPair(fieldSet.readString("Pair"));
        transaction.setSide(TransactionSide.valueOf(fieldSet.readString("Side")));
        transaction.setPrice(new BigDecimal(fieldSet.readString("Price")));
        transaction.setExecuted(parseCurrencyAmount(fieldSet.readString("Executed")));
        transaction.setAmount(parseCurrencyAmount(fieldSet.readString("Amount")));
        transaction.setFee(parseCurrencyAmount(fieldSet.readString("Fee")));
        return transaction;
    }

    private CurrencyAmount parseCurrencyAmount(String currencyStr) {
        String amountStr = currencyStr.replaceAll("[^0-9.]", "");
        String currency = currencyStr.replaceAll("[0-9.]", "");
        return new CurrencyAmount(new BigDecimal(amountStr), currency);
    }
}