package cod.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;

@Component
@Log4j2
public class ScheduledJobTrigger {

    private final JobLauncher jobLauncher;
    private final Job importCryptoTransactionJob;
    private final CodProperties codProperties;

    public ScheduledJobTrigger(JobLauncher jobLauncher, Job importCryptoTransactionJob, CodProperties codProperties) {
        this.jobLauncher = jobLauncher;
        this.importCryptoTransactionJob = importCryptoTransactionJob;
        this.codProperties = codProperties;
    }

    @Scheduled(cron = "0 * * * * ?")
    public void triggerJob() throws Exception {
        File directory = new File(codProperties.getDirectory());
        boolean hasFiles = Objects.requireNonNull(directory.listFiles()).length > 0;

        if (hasFiles) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importCryptoTransactionJob, jobParameters);
        } else {
            log.info("No files found. Job will not be triggered.");
        }
    }
}