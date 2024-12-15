package com.cloud.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

import java.util.UUID;

@SpringBootApplication
//@EnableConfigurationProperties(ProxyApplication.UriConfiguration.class)
public class ProxyApplication{

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

//	private RequestBodyValidation validationFilter;
//
//	@Bean
//	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
////        printRoutes();
//		String httpUri = "http://httpbin.org:80";
//		UUID traceID = UUID.randomUUID();
//		return builder.routes()
//				.route(p -> p
////                        .path("/users/create")
//						//For local testing
//                        .path("/post").and().method(HttpMethod.POST)
////						.path("/get").and().method(HttpMethod.GET)
//						.filters(f -> f
//										.addRequestHeader("X-Trace-Id", traceID.toString())
//                                .filters(validationFilter)
//						)
//						.uri(httpUri))
//				.build();
//	}


//	@Bean
//	public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
//
//		String httpUri = uriConfiguration.getBackend();
//		UUID traceID = UUID.randomUUID();
//		return builder.routes()
//				.route(p -> p
//						.path("/users/create")
//						//For local testing
//						/*
//						.path("/get")
//						.path("/post")
//						.path("/put")
//						.path("/delete")
//						 */
//						.filters(f -> f.addRequestHeader("X-Trace-Id", traceID.toString())
//								.filters())
//						.uri(httpUri))
//				.build();
//	}
//
//
//	@ConfigurationProperties
//    static class UriConfiguration {
//		//Comment for local testing
//		private String backend = "https://gateway-poc-axh2wfbs7q-ew.a.run.app";
//
//		//Uncomment for local testing
//		//private String backend = "http://httpbin.org:80";
//
//		public String getBackend() {
//			return backend;
//		}
//
//		public void setBackend(String backend) {
//			this.backend = backend;
//		}
//	}

}
