package da.springframework.springbootwebfluxapirest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluxApirestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApirestApplication.class, args);
    }

}
