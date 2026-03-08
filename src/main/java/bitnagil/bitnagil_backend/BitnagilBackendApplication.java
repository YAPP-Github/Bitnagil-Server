package bitnagil.bitnagil_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
public class BitnagilBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitnagilBackendApplication.class, args);
	}

}
