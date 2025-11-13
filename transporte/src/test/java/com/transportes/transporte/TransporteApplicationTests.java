package com.transportes.transporte;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = {
"spring.autoconfigure.exclude=org.springframework.cloud.openfeign.FeignAutoConfiguration"
})
class TransporteApplicationTests {

	@Test
	void contextLoads() {
	}

}
