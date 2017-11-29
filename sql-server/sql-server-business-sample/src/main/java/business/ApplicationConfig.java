package business;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"business", "wf.sample"})
@EnableJpaRepositories
@EnableAutoConfiguration
public class ApplicationConfig {
}
