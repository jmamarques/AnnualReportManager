package cod.batch;

import cod.batch.model.CryptoTransaction;
import cod.batch.model.CurrencyAmount;
import cod.batch.model.TransactionSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate
                    .query("SELECT id, date_utc, pair, side, price, executed_amount, executed_currency, " +
                                    "amount_amount, amount_currency, fee_amount, fee_currency " +
                                    "FROM crypto_transaction",
                            (ResultSet rs, int rowNum) -> {
                                CryptoTransaction transaction = new CryptoTransaction();

                                transaction.setDateUtc(rs.getString("date_utc"));
                                transaction.setPair(rs.getString("pair"));
                                String side = rs.getString("side");
                                transaction.setSide(TransactionSide.valueOf(side));
                                transaction.setPrice(rs.getBigDecimal("price"));

                                transaction.setExecuted(new CurrencyAmount(
                                        rs.getBigDecimal("executed_amount"),
                                        rs.getString("executed_currency"))
                                );
                                transaction.setAmount(new CurrencyAmount(
                                        rs.getBigDecimal("amount_amount"),
                                        rs.getString("amount_currency"))
                                );
                                transaction.setFee(new CurrencyAmount(
                                        rs.getBigDecimal("fee_amount"),
                                        rs.getString("fee_currency"))
                                );

                                return transaction;
                            })
                    .forEach(transaction -> log.info("Found <{{}}> in the database.", transaction));
        }
    }
}