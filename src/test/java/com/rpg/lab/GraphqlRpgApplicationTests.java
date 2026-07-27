package com.rpg.lab;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class GraphqlRpgApplicationTests {

	@Test
	void run() {
		try(MockedStatic<SpringApplication> mock = Mockito.mockStatic(SpringApplication.class)) {
			GraphqlRpgApplication.main(new String[0]);

			mock.verify(() -> SpringApplication.run(GraphqlRpgApplication.class, new String[0]));
		}
	}

}
