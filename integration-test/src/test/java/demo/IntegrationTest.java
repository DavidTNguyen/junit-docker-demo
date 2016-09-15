package demo;

import com.jayway.restassured.RestAssured;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.DockerPort;
import org.junit.*;

import java.util.function.Function;

import static com.jayway.restassured.RestAssured.get;
import static com.palantir.docker.compose.connection.waiting.HealthChecks.toRespondOverHttp;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

public class IntegrationTest {

    private static final int INTERNAL_PORT = 8080;

    private static final Function<DockerPort, String> TO_EXTERNAL_URI =
            (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT");

    @Rule
    public DockerComposeRule docker = DockerComposeRule.builder()
            .file("../docker-compose.yml")
            .waitingForService("greeting-service", toRespondOverHttp(INTERNAL_PORT, TO_EXTERNAL_URI))
            .waitingForService("counter-service", toRespondOverHttp(INTERNAL_PORT, TO_EXTERNAL_URI))
            .waitingForService("master-service", toRespondOverHttp(INTERNAL_PORT, TO_EXTERNAL_URI))
            .saveLogsTo("build/docker-logs")
            .build();

    @Before
    public void setUp() throws Exception {
        Container masterServiceContainer = docker.containers().container("master-service");
        RestAssured.baseURI = TO_EXTERNAL_URI.apply(masterServiceContainer.port(INTERNAL_PORT));
    }

    @Test
    public void shouldReturnData() throws Exception {
        get("/info")
            .then().assertThat()
            .body("counter", isA(Number.class))
            .body("greeting", is("Hello World"));
    }

    @Test
    public void shouldReturnDefaultGreetingWhenGreetingServiceDown() throws Exception {
        docker.dockerCompose().container("greeting-service").stop();

        get("/info")
            .then().assertThat()
            .body("greeting", is("Hola!"));
    }

    @Test
    public void shouldReturnDefaultCountWhenCounterServiceDown() throws Exception {
        docker.dockerCompose().container("counter-service").stop();

        get("/info")
            .then().assertThat()
            .body("counter", is(42));
    }
}
