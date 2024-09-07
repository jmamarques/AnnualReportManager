package cod.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "cod")
public class CodProperties {

    private String directory;
    private String report;
    private String fileFormat;
    private int batchSize;
}
