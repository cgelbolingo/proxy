package com.cloud.proxy;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProxyApplicationTests {;

	@Autowired
	private RouteLocator routeLocator;

	@Test
	public void verifyRoutes() {
		routeLocator.getRoutes().subscribe(route -> {
			Assertions.assertEquals(route.getUri().getPath(), "https://gateway-poc-axh2wfbs7q-ew.a.run.app");
		});
	}

}
