package cod.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobTrigger {

    private final JobLauncher jobLauncher;
    private final Job importCryptoTransactionJob;

    public ScheduledJobTrigger(JobLauncher jobLauncher, Job importCryptoTransactionJob) {
        this.jobLauncher = jobLauncher;
        this.importCryptoTransactionJob = importCryptoTransactionJob;
    }

    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    public void triggerJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // Adding a unique parameter
                .toJobParameters();

        jobLauncher.run(importCryptoTransactionJob, jobParameters);
    }
}