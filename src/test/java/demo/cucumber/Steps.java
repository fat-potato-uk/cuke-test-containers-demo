package demo.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import demo.Application;
import demo.models.Employee;
import demo.repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;

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
