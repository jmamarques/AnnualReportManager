package cod.batch.processor;

import cod.batch.model.CryptoTransaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;

public class CryptoTransactionItemProcessor implements ItemProcessor<CryptoTransaction, CryptoTransaction> {

    @Override
    public CryptoTransaction process(@NotNull CryptoTransaction transaction) {
        return transaction;
    }
}
