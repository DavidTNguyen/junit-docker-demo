package demo;

import com.jayway.restassured.RestAssured;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.function.Function;

import static com.jayway.restassured.RestAssured.get;
import static com.palantir.docker.compose.connection.waiting.HealthChecks.toRespondOverHttp;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

public class IntegrationTest {

    private static final Function<DockerPort, String> TO_EXTERNAL_URI =
            (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT");

    @Rule
    public DockerComposeRule docker = DockerComposeRule.builder()
            .file("../docker-compose.yml")
            .waitingForService("greeting-service", toRespondOverHttp(8080, TO_EXTERNAL_URI))
            .waitingForService("counter-service", toRespondOverHttp(8080, TO_EXTERNAL_URI))
            .waitingForService("master-service", toRespondOverHttp(8080, TO_EXTERNAL_URI))
            .saveLogsTo("build/docker-logs")
            .build();

    @Before
    public void setUp() throws Exception {
        docker.dockerCompose().up();
        RestAssured.port = 8083;
    }

    @After
    public void tearDown() throws Exception {
        docker.dockerCompose().down();
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
