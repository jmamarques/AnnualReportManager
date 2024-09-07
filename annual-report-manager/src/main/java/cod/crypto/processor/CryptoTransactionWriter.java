package cod.crypto.processor;

import cod.crypto.model.CryptoTransaction;
import cod.crypto.repository.CryptoTransactionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Log4j2
public class CryptoTransactionWriter implements ItemWriter<CryptoTransaction> {


    private final CryptoTransactionRepository cryptoTransactionRepository;

    public CryptoTransactionWriter(CryptoTransactionRepository cryptoTransactionRepository) {
        this.cryptoTransactionRepository = cryptoTransactionRepository;
    }

    @Override
    public void write(Chunk<? extends CryptoTransaction> chunk) {
        log.info("Writing cryptoTransactions");

        cryptoTransactionRepository.batchInsert(chunk.getItems().stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList()));

        log.info("Completed writing cryptoTransactions");

    }
}
