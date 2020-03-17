package demo.cucumber;

import demo.Application;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

@Slf4j
@ContextConfiguration(classes = Application.class)
public class Steps {

    @Autowired
    private Actions actions;

    @Given("^the system has been initialised$")
    public void theSystemHasBeenInitialised() {
        actions.startContainer();
    }

    @When("^the client calls (.*)$")
    public void theClientCallsEmployees(String url) throws Throwable {
        actions.executeGetJsonArray(url);
    }

    @And("^response code received is (\\d+)$")
    public void responseCodeReceivedIs(int responseCode) {
        assertEquals(responseCode, actions.getHttpResponse());
    }

    @Then("^the client receives employees:$")
    public void theClientReceivesEmployees(List<String> employeeNames) throws Throwable {
        var employees = actions.getHttpResponseMessage();
        var retrievedEmployees = new ArrayList<String>();
        employees.forEach(jsonNode -> retrievedEmployees.add(jsonNode.get("name").textValue()));
        assertThat(employeeNames, containsInAnyOrder(retrievedEmployees.toArray()));
    }
}
