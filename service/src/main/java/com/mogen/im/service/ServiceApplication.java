package com.mogen.im.service;

import com.mogen.im.service.config.BeanConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.mogen.im.service","com.mogen.im.common","com.mogen.im.service.config"})
@EnableJpaAuditing
@Import(value = BeanConfig.class)
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

}
