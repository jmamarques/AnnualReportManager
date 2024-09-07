package cod.crypto.repository;

import cod.crypto.model.CryptoTransaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class CryptoTransactionRepository {
    public static final String INSERT_SQL = "INSERT INTO crypto_transaction (date_utc, pair, side, price, executed_amount, executed_currency, " +
            "amount_amount, amount_currency, fee_amount, fee_currency) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final JdbcTemplate jdbcTemplate;

    public CryptoTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(List<CryptoTransaction> items) {
        jdbcTemplate.batchUpdate(INSERT_SQL, items, items.size(), CryptoTransactionRepository::CryptoTransactionPrepareStatement);
    }

    public static void CryptoTransactionPrepareStatement(PreparedStatement ps, CryptoTransaction cryptoTransaction) throws SQLException {
        ps.setTimestamp(1, Timestamp.valueOf(cryptoTransaction.getDateUtc()));
        ps.setString(2, cryptoTransaction.getPair());
        ps.setString(3, cryptoTransaction.getSide().name());
        ps.setBigDecimal(4, cryptoTransaction.getPrice());
        ps.setBigDecimal(5, cryptoTransaction.getExecuted().getAmount());
        ps.setString(6, cryptoTransaction.getExecuted().getCurrency());
        ps.setBigDecimal(7, cryptoTransaction.getAmount().getAmount());
        ps.setString(8, cryptoTransaction.getAmount().getCurrency());
        ps.setBigDecimal(9, cryptoTransaction.getFee().getAmount());
        ps.setString(10, cryptoTransaction.getFee().getCurrency());
    }

    // Insert a new transaction
    public int save(CryptoTransaction cryptoTransaction) {
        return jdbcTemplate.update(INSERT_SQL,
                cryptoTransaction.getDateUtc(),
                cryptoTransaction.getPair(),
                cryptoTransaction.getSide().name(),
                cryptoTransaction.getPrice(),
                cryptoTransaction.getExecuted().getAmount(),
                cryptoTransaction.getExecuted().getCurrency(),
                cryptoTransaction.getAmount().getAmount(),
                cryptoTransaction.getAmount().getCurrency(),
                cryptoTransaction.getFee().getAmount(),
                cryptoTransaction.getFee().getCurrency()
        );
    }

    // Retrieve all transactions
    public List<CryptoTransaction> findAll() {
        String sql = "SELECT * FROM crypto_transaction";
        return jdbcTemplate.query(sql, new CryptoTransactionRowMapper());
    }

    // Find transaction by ID (assuming you have an ID field)
    public CryptoTransaction findById(int id) {
        String sql = "SELECT * FROM crypto_transaction WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new CryptoTransactionRowMapper(), id);
    }

    // Update an existing transaction
    public int update(CryptoTransaction cryptoTransaction, int id) {
        String sql = "UPDATE crypto_transaction SET date_utc = ?, pair = ?, side = ?, price = ?, executed_amount = ?, " +
                "executed_currency = ?, amount_amount = ?, amount_currency = ?, fee_amount = ?, fee_currency = ? " +
                "WHERE id = ?";

        return jdbcTemplate.update(sql,
                cryptoTransaction.getDateUtc(),
                cryptoTransaction.getPair(),
                cryptoTransaction.getSide().name(),
                cryptoTransaction.getPrice(),
                cryptoTransaction.getExecuted().getAmount(),
                cryptoTransaction.getExecuted().getCurrency(),
                cryptoTransaction.getAmount().getAmount(),
                cryptoTransaction.getAmount().getCurrency(),
                cryptoTransaction.getFee().getAmount(),
                cryptoTransaction.getFee().getCurrency(),
                id
        );
    }

    // Delete a transaction
    public int deleteById(int id) {
        String sql = "DELETE FROM crypto_transaction WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
