package cod.crypto.repository;

import cod.crypto.model.CryptoTransaction;
import cod.crypto.model.CurrencyAmount;
import cod.crypto.model.TransactionSide;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CryptoTransactionRowMapper implements RowMapper<CryptoTransaction> {

    @Override
    public CryptoTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        CryptoTransaction cryptoTransaction = new CryptoTransaction();

        Timestamp timestamp = rs.getTimestamp("date_utc");
        LocalDateTime dateUtc = timestamp != null ? timestamp.toLocalDateTime() : null;
        cryptoTransaction.setDateUtc(dateUtc);
        cryptoTransaction.setPair(rs.getString("pair"));
        cryptoTransaction.setSide(TransactionSide.valueOf(rs.getString("side")));
        cryptoTransaction.setPrice(rs.getBigDecimal("price"));
        cryptoTransaction.setExecuted(
                new CurrencyAmount(
                        rs.getBigDecimal("executed_amount"),
                        rs.getString("executed_currency")
                )
        );
        cryptoTransaction.setAmount(
                new CurrencyAmount(
                        rs.getBigDecimal("amount_amount"),
                        rs.getString("amount_currency")
                )
        );
        cryptoTransaction.setFee(
                new CurrencyAmount(
                        rs.getBigDecimal("fee_amount"),
                        rs.getString("fee_currency")
                )
        );

        return cryptoTransaction;
    }
}
