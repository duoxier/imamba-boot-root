package com.imamba.boot.test;


import com.imamba.boot.service.logging.LoggingFilter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {MTestConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@WebAppConfiguration
@ActiveProfiles({"test"})
public class AbstractBootTest {

    @Autowired(
            required = false
    )
    protected WebApplicationContext wac;
    @Autowired(
            required = false
    )
    protected LoggingFilter loggingFilter;
    protected MockMvc mockMvc;

    public AbstractBootTest() {
    }

    @Before
    public void setUp() {
        this.mockMvc = ((DefaultMockMvcBuilder) MockMvcBuilders.webAppContextSetup(this.wac).addFilter(this.loggingFilter, new String[]{"/*"})).build();
    }
}
