package com.cloud.proxy;


//import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.JvmProxyConfigurer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;
import wiremock.org.apache.hc.client5.http.classic.HttpClient;
import wiremock.org.apache.hc.client5.http.classic.methods.HttpGet;
import wiremock.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import wiremock.org.apache.hc.core5.http.HttpResponse;
import wiremock.org.apache.hc.core5.http.io.entity.EntityUtils;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import com.cloud.proxy.model.User;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"backend=http://localhost:8888",
                "config.stopBubbling = true," +
                "lombok.addLombokGeneratedAnnotation = true"})
@WireMockTest(httpPort = 8888)
public class ProxyWireMockTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RouteLocator routeLocator;

    private MockWebServer mockWebServer;

    private static WireMockServer wireMockServer;

    private HttpClient httpClient;

     private final String UUIDRegex = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    @Rule
    public WireMockRule wm = new WireMockRule(options()
            .dynamicPort()
            .enableBrowserProxying(true)
    );

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().port(9090));// Mock downstream service
        wireMockServer.start();

        stubFor(post(urlEqualTo("/users/create"))  // Arbitrary URL
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Mocked Backend Response")));

        stubFor(get(urlMatching("/users/" + UUIDRegex))  // Arbitrary URL
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Mocked Backend Response")));

        httpClient = HttpClientBuilder.create()
                .useSystemProperties() // This must be enabled for auto proxy config
                .build();
    }

    @AfterEach
    void tearDown() throws IOException {
        wireMockServer.stop();
    }

    public void printRoutes() {
        routeLocator.getRoutes().subscribe(route -> {
            System.out.println("Route ID: " + route.getId());
            System.out.println("Route URI: " + route.getUri());
        });
    }

    @Test
    public void testRouting_create() throws InterruptedException {
        printRoutes();
        webTestClient.post()
                .uri("/users/create")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(
                        User.builder()
                                .id(UUID.randomUUID().toString())
                                .email("johndoe@gmail.com")
                                .firstName("John")
                                .lastName("Doe")
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Mocked Backend Response");
    }

    @Test
    public void testRouting_get() throws InterruptedException {
        String id = UUID.randomUUID().toString();
        webTestClient.get()
                .uri("/users/{id}", id) // Path to be routed by Spring Cloud Gateway
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Mocked Backend Response"); // Verifying the mocked response
    }

    /*
    @Test
    void shouldForwardCustomHeaderToDownstream() {

        // Make a request to the gateway
        webTestClient.get()
                .uri("/users/create") // Gateway route forwarding to WireMock
                .exchange()
                .expectStatus().isOk();


        // Verify header exists in the downstream request
//        verify(getRequestedFor(urlEqualTo("/users/create")));
//        wireMockServer.verify(getRequestedFor(urlEqualTo("http://localhost:8888"))
//                .withHeader("X-Trace-Id", matching(".+"))); // Ensures the header is not null or empty

        WireMock.verify(1, anyRequestedFor(urlMatching(".*")));
        List<LoggedRequest> allRequests = wireMockServer.findAll(RequestPatternBuilder.allRequests());
        var requests = wireMockServer.findAll(getRequestedFor(urlEqualTo("*./users/create")));
        assertNotNull(requests, "No requests were received by the downstream service.");
        assertNotNull(requests.get(0).getHeaders().getHeader("X-Trace-Id").firstValue(),
                "X-Trace-Id header was not sent to the downstream service.");

    }

    @Test
    public void testViaProxy() throws Exception {
        wm.stubFor(get("/things")
                .withHost(equalTo("my.first.domain"))
                .willReturn(ok("Domain 1")));

        wm.stubFor(get("/things")
                .withHost(equalTo("my.second.domain"))
                .willReturn(ok("Domain 2")));

        HttpResponse response = httpClient.execute(new HttpGet("http://my.first.domain/things"));
//        String responseBody = EntityUtils.toString(response);
        String responseBody = response.toString();
        Assertions.assertEquals("Domain 1", responseBody);

//        response = httpClient.execute(new HttpGet("http://my.second.domain/things"));
//        responseBody = EntityUtils.toString(response.getEntity());
//        Assertions.assertEquals("Domain 2", responseBody);
    }

    @Test
    void testRequestHeadersSentToMockServer() throws Exception {

        // Create a mock web server
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.start();

            // Enqueue a mock response to simulate backend
            mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("OK"));

            // Define the mock server URL (your WebTestClient should target this)
            String mockServerUrl = mockWebServer.url("/users/create").toString();

            // Use WebTestClient to make a request to the mock server
            webTestClient.get()
                    .uri(mockServerUrl)
                    .exchange()
                    .expectStatus().isOk();

            // Capture the request received by MockWebServer
            var request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);

            // Verify that the correct header was sent
            assertThat(request.getHeader("X-Trace-Id")).isNotNull();
        }
    }

    @Test
    void testRequestHeadersSentToMockServer2() throws Exception {

        // Create a mock web server
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.start();

            // Enqueue a mock response to simulate backend
            mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("OK"));

            // Define the mock server URL (Spring Cloud Gateway should route requests here)
            String mockServerUrl = mockWebServer.url("/users/create").toString();

            // Assuming your Spring Cloud Gateway is configured to forward requests
            // from /users/** to the mock server's URL
            webTestClient.get()
                    .uri("/users/create") // This should match the Spring Cloud Gateway route
                    .header("X-Trace-Id", "12345") // Add any required headers
                    .exchange()
                    .expectStatus().isOk(); // Expect a successful response

            // Capture the request received by MockWebServer
            var request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);

            // Verify that the correct header was sent
            assertThat(request.getHeader("X-Trace-Id")).isNotNull();
            assertThat(request.getHeader("X-Trace-Id")).isEqualTo("12345");
        }
    }

     */
}
