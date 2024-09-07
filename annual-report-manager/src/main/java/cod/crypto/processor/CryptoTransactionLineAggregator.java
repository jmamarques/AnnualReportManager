package cod.crypto.processor;

import cod.crypto.model.CryptoTransaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.file.transform.LineAggregator;

public class CryptoTransactionLineAggregator implements LineAggregator<CryptoTransaction> {

    @NotNull
    @Override
    public String aggregate(CryptoTransaction transaction) {
        // Format the fields as needed, e.g., CSV format
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                transaction.getFormattedDateUtc(),
                transaction.getPair(),
                transaction.getSide().name(),
                transaction.getPrice().toPlainString(),
                transaction.getExecuted().getAmount().toPlainString() + transaction.getExecuted().getCurrency(),
                transaction.getAmount().getAmount().toPlainString() + transaction.getAmount().getCurrency(),
                transaction.getFee().getAmount().toPlainString() + transaction.getFee().getCurrency()
        );
    }
}
