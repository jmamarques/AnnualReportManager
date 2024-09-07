package cod.crypto.listener;

import cod.crypto.repository.CryptoTransactionRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
                    .query("SELECT distinct date_utc, pair, side, price, executed_amount, executed_currency, " +
                                    "amount_amount, amount_currency, fee_amount, fee_currency " +
                                    "FROM crypto_transaction",
                            (rs, rowNum) -> new CryptoTransactionRowMapper().mapRow(rs, rowNum))
                    .forEach(transaction -> log.info("Found <{{}}> in the database.", transaction));
        }
    }
}