package dev.nisalb.hubwork;

import dev.nisalb.hubwork.service.UserService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ConfigurationPropertiesScan({"dev.nisalb.hubwork.config"})
public class HubworkApplication {

    @Setter(onMethod = @__({@Autowired}))
    private UserService userService;

    @Value(value = "${app.setup.count.user}")
    private int numUsersToPopulate = 20;

    @Value(value = "${app.setup.count.business}")
    private int numBusinessesToPopulate = 5;

    private final Logger logger = LoggerFactory.getLogger(HubworkApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(HubworkApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void populateData() {
        if (numUsersToPopulate < 1) numUsersToPopulate = 20;
        if (numBusinessesToPopulate < 1) numBusinessesToPopulate = 5;

        logger.atInfo().log("Populating Users ({}) and Businesses ({})...", numUsersToPopulate, numBusinessesToPopulate);
        userService.initUsersAndBusinesses(numUsersToPopulate, numBusinessesToPopulate);
        logger.atInfo().log("Users and Businesses are populated");
    }

}
