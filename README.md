### Challenge 9

This tutorial is designed more of a "complete" example and therefore can be cloned 
from the start. It builds on a modified version of the code base we have been working
on during the last few challenges. It aims to provide an "ideal" example of how a Java
Spring Boot project might be organised and the rational behind it.

#### Docker

We have two approaches when it comes to containerising our applications:

* Multi-stage Docker build
* Maven Docker build

I have formed a preference for the former recently (which comes at _some_ costs) over the latter.
It's limitation is that it only really works with single `pom.xml` projects, e.g. not multi-module
Maven projects. It's because of this some may consider it an unsuitable approach (and will therefore
go down the Maven Docker build process). If it's cost can be borne, it is very effective.

For this challenge we will therefore be looking at the multi-stage Docker build process:

```dockerfile
FROM maven:3.6.1-jdk-12 AS build

# Resolve the dependencies as an independent layer first
COPY pom.xml /usr/src/app/pom.xml
WORKDIR /usr/src/app
RUN mvn dependency:go-offline

# Copy and build
COPY src /usr/src/app/src
RUN mvn clean package

# Move artifact into slim container
FROM openjdk:12-alpine
COPY --from=build /usr/src/app/target/rest-demo-2-1.0-SNAPSHOT.jar /usr/app/rest-demo-2-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/rest-demo-2-1.0-SNAPSHOT.jar"]
```

This `Dockerfile` will build our code and create a (slim) container that can be deployed. It does this
in a manner that also caches our dependencies in a layer. This means that the build process for
code-only changes should be very quick.
 
One of the main reasons I like this approach is that it makes the CI/CD process _very_ simple, and 
allows the developer to mirror what exactly happens when they push their code.

Feel free to have a go at building the code and how it copes with code changes (with regard to build
times)

#### Integration Testing

Whilst technically component testing, Spring considers many of the tools we will use part
of its _integration testing framework_. Either way, we are going to be looking at testing 
our code in an end-to-end manner.

To do this, we are going to be using `Cucumber`.

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

_Note: At this stage you may wish to install the `Cucumber` and `Gherkin` plugins
for intelliJ_

If you navigate to the `employee.feature` file, you will see the above. `Cucumber` is
a BDD framework that allows us to write high level tests (in `Gherkin`) and "glue" them
together with Java code.

This approach allows for very versatile and readable tests that (when written well), can 
give a great overview of the application (without having to struggle over hours of documentation).

The glue code run behind these steps looks like this:

```java
@Slf4j
@ContextConfiguration(classes = Application.class)
public class Steps {

    @Autowired
    private Actions actions;

    @Autowired
    private EmployeeRepository employeeRepository;

    @And("^the client calls (.*)$")
    public void theClientCallsEmployees(String url) throws Throwable {
        actions.executeGet(url);
    }

    @And("^response code received is (\\d+)$")
    public void responseCodeReceivedIs(int statusCode) throws Throwable {
        assertEquals(statusCode, actions.getHttpResponse());
    }

    @Then("^the client receives employees:$")
    public void theClientReceivesEmployees(List<String> employeeNames) throws Throwable {
        var employees = asList(new ObjectMapper().readValue(actions.getEmployeeHttpResponseMessage(), Employee[].class));
        assertEquals(employeeNames, employees.stream().map(Employee::getName).collect(Collectors.toList()));
    }

    @When("^the system has been initialised$")
    public void theDatabaseHasBeenSeeded() throws Throwable {
        log.info("Preloading " + employeeRepository.save(new Employee("Bilbo Baggins", "burglar")));
        log.info("Preloading " + employeeRepository.save(new Employee("Frodo Baggins", "thief")));
    }
}
```

Each method is annotated with a regular expression that corresponds to the `Gherkin` used above.
In addition, we are wiring in a helper class `Actions` via use of the `@ContextConfiguration(classes = Application.class)`
(which wires in the Spring context) for exercising the desired behaviour. This pattern I have found
allows for greater re-use of code between steps and less bloated step/glue files.

The final piece of the puzzle is a basic class that tells `Cucumber` where to look for everything:

```java
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
public class CucumberIntegrationTest {
}
```

_Note: Unfortunately, at the time of scribing this `JUnit5` support for `Cucumber` is still lacking_

Have a go at running these tests (clicking on the play icon next to the above class will do this).

For this challenge, try to create some new tests for some of the other end points.