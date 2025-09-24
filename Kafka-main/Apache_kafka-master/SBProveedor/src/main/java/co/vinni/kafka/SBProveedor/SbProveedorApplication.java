package co.vinni.kafka.SBProveedor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Scanner;

@SpringBootApplication
public class SbProveedorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbProveedorApplication.class, args);
	}
	@Bean
	CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate){
		return args -> {

			Scanner sc = new Scanner (System.in);

			String mensaje = sc.nextLine();


			kafkaTemplate.send("EquipoKafka-topic", mensaje);
		};
	}
}
