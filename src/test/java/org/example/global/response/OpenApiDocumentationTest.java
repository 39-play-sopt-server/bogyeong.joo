package org.example.global.response;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:openapi-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=false",
        "spring.jpa.open-in-view=false"
})
class OpenApiDocumentationTest {
    @Autowired
    private Environment environment;

    @Test
    void generatedDocumentIncludesStatusesParametersAndRequestExamples() throws Exception {
        String url = "http://localhost:" + environment.getProperty("local.server.port") + "/v3/api-docs";
        try (HttpClient client = HttpClient.newHttpClient()) {
            var response = client.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            var document = JsonPath.parse(response.body());
            Map<String, Object> statuses = document.read("$.paths['/api/v1/posts'].post.responses");
            assertTrue(statuses.keySet().containsAll(List.of("201", "400", "404", "500")));
            Map<String, Object> notFound = document.read("$.paths['/api/v1/posts'].post.responses['404'].content['application/json']");
            assertTrue(notFound.toString().contains("MEMBER_NOT_FOUND"));
            List<Map<String, Object>> parameters = document.read("$.paths['/api/v1/posts'].get.parameters");
            assertEquals(3, parameters.size());
            assertTrue(parameters.stream().anyMatch(p -> "size".equals(p.get("name"))
                    && p.get("schema").toString().contains("maximum=100")));
            assertEquals("보경", document.read("$.components.schemas.CreateMemberRequest.properties.name.example"));
            List<String> required = document.read("$.components.schemas.CreatePostRequest.required");
            assertTrue(required.containsAll(List.of("title", "content", "category", "memberId")));
            String successSchema = document.read("$.paths['/api/v1/posts'].post.responses['201'].content['application/json'].schema['$ref']");
            assertTrue(successSchema.contains("Post"));
        }
    }
}
