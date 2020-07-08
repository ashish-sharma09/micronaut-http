package micronaut.http

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.json.JsonOutput
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.runtime.server.EmbeddedServer
import io.restassured.RestAssured
import io.restassured.response.Response
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

class StatusIntegrationSpec extends Specification {

    private static final String CLIENT_TOKEN = "client-token"
    private static final String CLIENT_ID = "someClientID"
    private static final String ACCESS_TOKEN = "someAccessToken"

    @AutoCleanup ApplicationContext context

    WireMockServer authServer = new WireMockServer(options().dynamicPort())
    WireMockServer serviceServer = new WireMockServer(options().dynamicPort())

    def setup() {
        serviceServer.start()
        authServer.start()

        context = ApplicationContext
                .build()
                .mainClass(EmbeddedServer)
                .propertySources(PropertySource.of([
                    "service.url": serviceServer.baseUrl(),
                    "client.id": CLIENT_ID,
                    "auth.url": authServer.baseUrl(),
                ]))
                .build()
        context.start()

        def server = context.getBean(EmbeddedServer)
        server.start()

        RestAssured.port = server.port
    }

    void cleanup() {
        RestAssured.port = RestAssured.DEFAULT_PORT
        serviceServer.stop()
        authServer.stop()
        context.stop()
    }

    def "Micronaut HTTP client should not time out during multiple concurrent requests handling"() {
        given: "all external services are running"
        identityRequestValidationMock()
        identityRequestIssueMock()
        serviceRequestMock("requestId")

        when: 'requests are issued concurrently'
        def threadCount = 20
        def fixedThreadPool = Executors.newFixedThreadPool(threadCount)
        List<Future<Response>> futures = []
        threadCount.times {
            futures.add(fixedThreadPool.submit(new Callable<Response>() {
                @Override
                Response call() throws Exception {
                    RestAssured.given()
                        .header("Authorization", "Bearer $CLIENT_TOKEN")
                        .get("/status")
                }
            }))
        }

        then: "all calls are successful"
        futures.stream().allMatch{it.get().statusCode == 200}
    }

    def serviceRequestMock(String id) {
        serviceServer.stubFor(
            get(
                    urlEqualTo("/details/$id")
            )
            .withHeader("Authorization", equalTo("Bearer $ACCESS_TOKEN"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody(
                    """
                    {
                      "Status": "VALID"
                    }
                    """
                )
            )
        )
    }

    def identityRequestIssueMock() {

        def requestJson = JsonOutput.toJson([
                client_id       : "$CLIENT_ID"
        ])

        authServer.stubFor(
            post(
                urlEqualTo("/issue/token")
            )
            .withHeader("Accept", equalTo("application/json"))
            .withRequestBody(equalTo(requestJson))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("""
                    {
                        "token": "$ACCESS_TOKEN"
                    }
                    """
                )
            )
        )
    }

    def identityRequestValidationMock() {
        def requestJson = JsonOutput.toJson([token: CLIENT_TOKEN])

        authServer.stubFor(
            post(
                urlPathEqualTo("/auth/validate")
            )
            .withQueryParam("client_id", equalTo(CLIENT_ID))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(equalTo(requestJson))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody(
                    """
                    {
                      "Status": "VALID"
                    }
                    """
                )
            )
        )
    }
}

