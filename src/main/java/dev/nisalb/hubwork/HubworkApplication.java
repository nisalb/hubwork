package dev.nisalb.hubwork;

import dev.nisalb.hubwork.service.UserService;
import jakarta.annotation.PreDestroy;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class HubworkApplication {

    @Setter(onMethod = @__({@Autowired}))
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(HubworkApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(HubworkApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void populateData() {
        logger.atInfo().log("Populating Users and Businesses...");
        userService.initUsersAndBusinesses(20, 5);
        logger.atInfo().log("Users and Businesses are populated");
    }

}
