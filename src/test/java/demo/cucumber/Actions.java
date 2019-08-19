package demo.cucumber;

import demo.controllers.EmployeeController;
import demo.controllers.advice.EmployeeControllerAdvice;
import demo.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Service
public class Actions {

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeControllerAdvice employeeControllerAdvice;

    // Result actions to be passed between steps.
    private static ResultActions resultActions = null;

    private MockMvc mockMvc;

    @PostConstruct
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).setControllerAdvice(employeeControllerAdvice).build();
    }

    void executeGet(String url) throws Exception {
        resultActions = mockMvc.perform(get(url));
    }

    int getHttpResponse() {
        return resultActions.andReturn().getResponse().getStatus();
    }

    String getEmployeeHttpResponseMessage() throws IOException {
        return resultActions.andReturn().getResponse().getContentAsString();
    }
}