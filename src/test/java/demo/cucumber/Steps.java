package demo.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import demo.models.Employee;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;

@Slf4j
public class Steps extends SetupAndStepHelper {
    @And("^the client calls (.*)$")
    public void theClientCallsEmployees(String url) throws Throwable {
        this.executeGet(url);
    }

    @And("^response code received is (\\d+)$")
    public void responseCodeReceivedIs(int statusCode) throws Throwable {
        assertEquals(statusCode, this.getHttpResponse());
    }

    @Then("^the client receives employees:$")
    public void theClientReceivesEmployees(List<String> employeeNames) throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();
        var employees = asList(objectMapper.readValue(getEmployeeHttpResponseMessage(), Employee[].class));
        assertEquals(employeeNames, employees.stream().map(Employee::getName).collect(Collectors.toList()));
    }

    @When("^the database has been seeded$")
    public void theDatabaseHasBeenSeeded() throws Throwable {
        log.info("Preloading " + employeeRepository.save(new Employee("Bilbo Baggins", "burglar")));
        log.info("Preloading " + employeeRepository.save(new Employee("Frodo Baggins", "thief")));
    }
}
