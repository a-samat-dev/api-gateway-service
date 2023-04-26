package kz.samat.apigatewayservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "service-mapping")
@Getter
@Setter
public class ServiceMapping {

    private String refDataService;

    private String userService;

    private String patientService;

    private String scheduleService;
}
