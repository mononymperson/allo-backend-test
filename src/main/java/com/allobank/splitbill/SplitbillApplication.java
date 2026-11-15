package com.allobank.splitbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SplitbillApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitbillApplication.class, args);
	}

}
