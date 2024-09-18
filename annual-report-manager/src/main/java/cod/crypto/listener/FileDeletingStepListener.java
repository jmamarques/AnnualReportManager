package cod.crypto.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class FileDeletingStepListener implements JobExecutionListener {

    public FileDeletingStepListener() {
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            String filePath = jobExecution.getJobParameters().getString("filePath");
            try {
                Path file = Paths.get(filePath);
                if (Files.exists(file)) {
                    Files.delete(file);
                    log.info("File deleted: {}", filePath);
                } else {
                    log.info("File not found: {}", filePath);
                }
            } catch (Exception e) {
                log.error("Error deleting file: {}", e.getMessage());
            }
        }
    }
}
