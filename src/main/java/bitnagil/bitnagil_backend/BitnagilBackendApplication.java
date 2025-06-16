package bitnagil.bitnagil_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BitnagilBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitnagilBackendApplication.class, args);
	}

}
