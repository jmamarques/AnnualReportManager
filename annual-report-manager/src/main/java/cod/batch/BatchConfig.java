package cod.batch;

import cod.batch.model.CryptoTransaction;
import cod.batch.processor.CryptoTransactionLineAggregator;
import cod.batch.processor.CryptoTransactionFieldSetMapper;
import cod.batch.processor.CryptoTransactionItemProcessor;
import cod.configuration.CodProperties;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final CodProperties codProperties;

    public BatchConfig(CodProperties codProperties) {
        this.codProperties = codProperties;
    }

    @Bean
    public FlatFileItemReader<CryptoTransaction> reader() {
        return new FlatFileItemReaderBuilder<CryptoTransaction>()
                .name("cryptoTransactionItemReader")
                .resource(new FileSystemResource(codProperties.getDirectory() + "/transactions.csv"))
                .delimited()
                .delimiter(",")
                .quoteCharacter('\"')
                .names("Date(UTC)", "Pair", "Side", "Price", "Executed", "Amount", "Fee")
                .targetType(CryptoTransaction.class)
                .lineMapper(createLineMapper())
                .linesToSkip(1)
                .build();
    }
    private LineMapper<CryptoTransaction> createLineMapper() {
        DefaultLineMapper<CryptoTransaction> lineMapper = new DefaultLineMapper<>();

        // Create and set the FieldSetMapper
        CryptoTransactionFieldSetMapper fieldSetMapper = new CryptoTransactionFieldSetMapper();
        lineMapper.setFieldSetMapper(fieldSetMapper);

        // Create and set the LineTokenizer
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setQuoteCharacter('\"');
        lineTokenizer.setNames("Date(UTC)", "Pair", "Side", "Price", "Executed", "Amount", "Fee");
        lineMapper.setLineTokenizer(lineTokenizer);

        return lineMapper;
    }

    @Bean
    public CryptoTransactionItemProcessor processor() {
        return new CryptoTransactionItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<CryptoTransaction> writerDb(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CryptoTransaction>()
                .dataSource(dataSource)
                .sql("INSERT INTO crypto_transaction (date_utc, pair, side, price, executed_amount, executed_currency, " +
                        "amount_amount, amount_currency, fee_amount, fee_currency) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .itemPreparedStatementSetter((cryptoTransaction, ps) -> {
                    ps.setString(1, cryptoTransaction.getDateUtc());
                    ps.setString(2, cryptoTransaction.getPair());
                    ps.setString(3, cryptoTransaction.getSide().name());
                    ps.setBigDecimal(4, cryptoTransaction.getPrice());
                    ps.setBigDecimal(5, cryptoTransaction.getExecuted().getAmount());
                    ps.setString(6, cryptoTransaction.getExecuted().getCurrency());
                    ps.setBigDecimal(7, cryptoTransaction.getAmount().getAmount());
                    ps.setString(8, cryptoTransaction.getAmount().getCurrency());
                    ps.setBigDecimal(9, cryptoTransaction.getFee().getAmount());
                    ps.setString(10, cryptoTransaction.getFee().getCurrency());
                })
                .beanMapped()
                .build();
    }

    @Bean
    public FlatFileItemWriter<CryptoTransaction> writerFlat() {
        return new FlatFileItemWriterBuilder<CryptoTransaction>()
                .name("cryptoTransactionItemWriter") // Name of the writer
                .resource(new FileSystemResource(codProperties.getReport() + "/processed-transactions.csv")) // Output file location
                .delimited() // Set the format as delimited (CSV)
                .names("dateUtc", "pair", "side", "price", "executed", "amount", "fee") // Column names
                .lineAggregator(new CryptoTransactionLineAggregator()) // Aggregates each line with delimited values
                .build(); // Build the writer
    }

    @Bean
    public Job importCryptoTransactionJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importCryptoTransactionJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<CryptoTransaction> reader,
                      CryptoTransactionItemProcessor processor,
                      JdbcBatchItemWriter<CryptoTransaction> writerDb,
                      FlatFileItemWriter<CryptoTransaction> writerFlat) {
        return new StepBuilder("step1", jobRepository)
                .<CryptoTransaction, CryptoTransaction>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writerDb)
                .writer(writerFlat)
                .build();
    }

}
