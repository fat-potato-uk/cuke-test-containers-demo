package demo.cucumber;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;

import static java.lang.String.format;

@Service
class Actions {

    @Value("${test.container.name}")
    private String testContainerName;

    @Autowired
    private RestTemplate restTemplate;

    private GenericContainer container;

    private ResponseEntity<ArrayNode> result;

    void executeGetJsonArray(String url) {
        var fullAddress = format("http://%s:%s/%s", container.getContainerIpAddress(), container.getMappedPort(8080), url);
        result = restTemplate.getForEntity(fullAddress, ArrayNode.class);
    }

    void startContainer() {
        container = new GenericContainer(testContainerName).withExposedPorts(8080);
        container.start();
    }

    int getHttpResponse() {
        return result.getStatusCodeValue();
    }

    ArrayNode getHttpResponseMessage() {
        return result.getBody();
    }
}