package demo;

import com.jayway.restassured.RestAssured;
import com.palantir.docker.compose.DockerComposeRule;
import org.junit.*;

import static com.jayway.restassured.RestAssured.get;
import static com.palantir.docker.compose.connection.waiting.HealthChecks.toRespondOverHttp;
import static org.hamcrest.Matchers.is;

public class IntegrationTest {

    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("../docker-compose.yml")
            .waitingForService("greeting-service", toRespondOverHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT")))
            .waitingForService("counter-service", toRespondOverHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT")))
            .waitingForService("master-service", toRespondOverHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT")))
            .saveLogsTo("build/dockerLogs/dockerComposeRuleTest")
            .build();

    @BeforeClass
    public static void setUp() throws Exception {
        docker.dockerCompose().up();
        RestAssured.port = 8083;
    }

    @After
    public void tearDown() throws Exception {
        docker.dockerCompose().down();
    }

    @Test
    public void shouldReturnGreeting() throws Exception {
        get("/greeting")
            .then().assertThat()
            .body("greeting", is("Hello World"));
    }

    @Test
    @Ignore
    public void shouldReturnDefaultGreetingWhenGreetingServiceDown() throws Exception {
        docker.dockerCompose().container("greeting-service").stop();

        get("/greeting")
            .then().assertThat()
            .body("greeting", is("Default Hello"));
    }

    @Test
    @Ignore
    public void shouldReturnDefaultCountWhenCounterServiceDown() throws Exception {
        docker.dockerCompose().container("counter-service").stop();

        get("/greeting")
            .then().assertThat()
            .body("counter", is(42));
    }
}
