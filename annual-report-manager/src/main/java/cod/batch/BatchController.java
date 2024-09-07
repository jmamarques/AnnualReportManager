package cod.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job importCryptoTransactionJob;

    public BatchController(JobLauncher jobLauncher, Job importCryptoTransactionJob) {
        this.jobLauncher = jobLauncher;
        this.importCryptoTransactionJob = importCryptoTransactionJob;
    }

    @GetMapping("/trigger")
    public ResponseEntity<String> triggerJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // Adding a unique parameter
                .toJobParameters();

        jobLauncher.run(importCryptoTransactionJob, jobParameters);
        return ResponseEntity.ok("Job triggered successfully");
    }
}
