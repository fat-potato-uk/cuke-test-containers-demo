package demo.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.Application;
import demo.controllers.EmployeeController;
import demo.controllers.advice.EmployeeControllerAdvice;
import demo.models.Employee;
import demo.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ContextConfiguration(classes = Application.class)
public class SetupAndStepHelper {

    @Autowired
    EmployeeController employeeController;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeControllerAdvice employeeControllerAdvice;

    // Result actions to be passed between steps.
    private static ResultActions resultActions = null;

    MockMvc mockMvc;

    @PostConstruct
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).setControllerAdvice(employeeControllerAdvice).build();
    }

    public void executeGet(String url) throws Exception {
        resultActions = mockMvc.perform(get(url));
        resultActions.andDo(print());
    }

    public int getHttpResponse() {
        MockHttpServletResponse result = resultActions.andReturn().getResponse();
        return result.getStatus();
    }

    public String getEmployeeHttpResponseMessage() throws IOException {
        return resultActions.andReturn().getResponse().getContentAsString();
    }
}