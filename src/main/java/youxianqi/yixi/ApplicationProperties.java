package youxianqi.yixi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class ApplicationProperties {

    @Value("${application.key1}")
    private String key1;
    public String getKey1() {
        return key1;
    }
}
