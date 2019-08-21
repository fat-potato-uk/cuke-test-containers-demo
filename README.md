### Challenge 10

This tutorial looks to highlight a means of testing our application from an system/black box
testing point of view. This means we are going to be testing the built artifact from
the last tutorial (`employee-test` Docker image) through a similar set of `Cucumber` steps.

The feature file looks exactly the same:

```gherkin
Feature: Employees can be created and queried

  Scenario: client sends a request to query existing employees
    Given the system has been initialised
    When the client calls /employees
    And response code received is 200
    Then the client receives employees:
      | Bilbo Baggins |
      | Frodo Baggins |
```

Where it differs is the actions and steps to invoke the behaviour:

```java
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
```

Here we can see functions for creating a test container and performing a `GET` request against it.

_Note: Typically we would not duplicate tests in this manner, but it helps as a comparison to keep
the same behaviour as the previous example_

The `testContainers` library provides a lot of helpful tools for exercising you application in this
way. You can run multiple containers through a `compose` file, or use some of the helper tools
like the `MySql` test container for common patterns.

The only other difference we have to consider here is that given this project does not have access
to the objects in the container under test, we can not reflectively cast returned content easily:

```java
@Then("^the client receives employees:$")
public void theClientReceivesEmployees(List<String> employeeNames) throws Throwable {
    var employees = actions.getHttpResponseMessage();
    var retrievedEmployees = new ArrayList<String>();
    employees.forEach(jsonNode -> retrievedEmployees.add(jsonNode.get("name").textValue()));
    assertThat(employeeNames, containsInAnyOrder(retrievedEmployees.toArray()));
}
```

Here we are extracting the content "by hand" for our comparison. This obviously isn't world ending
but it might help determine on what level you wish to perform this level of testing.

Have a go now at adding some additional tests for the other endpoints (perhaps copying the last
challenges steps)!