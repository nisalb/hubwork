package dev.nisalb.hubwork.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.config")
public class HubWorkProperties {
    private final Pattern patterns = new Pattern();

    @Data
    public static class Pattern {
        private String date = "dd-MM-yyyy";
        private String decimal = "#,###.00";
    }

}
