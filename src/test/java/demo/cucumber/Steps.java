package demo.cucumber;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.List;

public class Steps extends SetupAndStepHelper {
    @When("^the client calls /(.*)$")
    public void theClientCallsEmployees(String url) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^response code received is (\\d+)$")
    public void responseCodeReceivedIs(int statusCode) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the client receives employees:$")
    public void theClientReceivesEmployees(List<String> employeeNames) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
