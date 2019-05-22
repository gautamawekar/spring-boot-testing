package com.gawekar.springboot.mockbeans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
public class SpringContextCreationUsingJunit4Test {
    @Autowired
    private ApplicationContext applicationContext;
    @MockBean
    private SomeClass someClass;

    @Test
    public void checkIfContextIsCreated() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    public void checkIfSomeClassBeanIsInContext() {
        SomeClass bean = applicationContext.getBean(SomeClass.class);

        //Below will ensure that SomeClass bean is created and added to the context
        assertThat(bean).isNotNull();

    }

    private static class SomeClass {

    }
}
