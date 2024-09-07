package cod.crypto.config;

import cod.configuration.CodProperties;
import cod.crypto.listener.JobCompletionNotificationListener;
import cod.crypto.model.CryptoTransaction;
import cod.crypto.processor.CryptoTransactionFieldSetMapper;
import cod.crypto.processor.CryptoTransactionItemProcessor;
import cod.crypto.processor.CryptoTransactionLineAggregator;
import cod.crypto.repository.CryptoTransactionRepository;
import cod.crypto.repository.CryptoTransactionRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class CryptoTransactionBatchConfig {

    private final CodProperties codProperties;

    public CryptoTransactionBatchConfig(CodProperties codProperties) {
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
                .itemPreparedStatementSetter((cryptoTransaction, ps) -> CryptoTransactionRepository.CryptoTransactionPrepareStatement(ps, cryptoTransaction))
                .itemSqlParameterSourceProvider(BeanPropertySqlParameterSource::new)
                .beanMapped()
                .build();
    }

    @Bean
    public JdbcCursorItemReader<CryptoTransaction> readerDb(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<CryptoTransaction>()
                .name("readerDb")
                .dataSource(dataSource)
                .sql("select distinct date_utc, pair, side, price, executed_amount, executed_currency, amount_amount, amount_currency, fee_amount, fee_currency from crypto_transaction order by date_utc")
                .rowMapper(new CryptoTransactionRowMapper())
                .build();
    }

    @Bean
    public FlatFileItemWriter<CryptoTransaction> writerFlat() {
        return new FlatFileItemWriterBuilder<CryptoTransaction>()
                .name("cryptoTransactionItemWriter") // Name of the writer
                .resource(new FileSystemResource(codProperties.getReport() + "/processed-transactions.csv")) // Output file location
                .delimited() // Set the format as delimited (CSV)
                .names("dateUtc", "pair", "side", "price", "executed", "amount", "fee") // Column names
                .shouldDeleteIfExists(true)
                .lineAggregator(new CryptoTransactionLineAggregator()) // Aggregates each line with delimited values
                .build(); // Build the writer
    }

    @Bean
    public Job importCryptoTransactionJob(JobRepository jobRepository, Step step1, Step step2, JobCompletionNotificationListener listener) {
        return new JobBuilder("importCryptoTransactionJob", jobRepository)
                .listener(listener)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<CryptoTransaction> reader,
                      CryptoTransactionItemProcessor processor,
                      JdbcBatchItemWriter<CryptoTransaction> writerDb) {
        return new StepBuilder("step1", jobRepository)
                .<CryptoTransaction, CryptoTransaction>chunk(codProperties.getBatchSize(), transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writerDb)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      JdbcCursorItemReader<CryptoTransaction> readerDb,
                      CryptoTransactionItemProcessor processor,
                      FlatFileItemWriter<CryptoTransaction> flatFileItemWriter) {
        return new StepBuilder("step2", jobRepository)
                .<CryptoTransaction, CryptoTransaction>chunk(codProperties.getBatchSize(), transactionManager)
                .reader(readerDb)
                .processor(processor)
                .writer(flatFileItemWriter)
                .build();
    }

}
