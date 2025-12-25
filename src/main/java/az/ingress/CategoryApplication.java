package az.ingress;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import static org.springframework.boot.SpringApplication.run;

@EnableCaching
@SpringBootApplication
public class CategoryApplication {

    public static void main(String[] args) {
        run(CategoryApplication.class, args);
    }
}