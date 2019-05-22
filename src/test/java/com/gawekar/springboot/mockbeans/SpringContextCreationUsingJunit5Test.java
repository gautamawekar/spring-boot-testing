package com.gawekar.springboot.mockbeans;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//To migrate from JUnit 4 to JUnit 5 you can replace @RunWith(SpringRunner.class) with @ExtendWith(SpringExtension.class)
@ExtendWith(SpringExtension.class)
public class SpringContextCreationUsingJunit5Test {
    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private SomeClass someClass;

    @Test
    public void checkIfContextIsCreated(){
        assertThat(applicationContext).isNotNull();
    }

    @Test
    public void checkIfSomeClassBeanIsInContext(){
        SomeClass bean = applicationContext.getBean(SomeClass.class);

        //Below will ensure that SomeClass bean is created and added to the context
        assertThat(bean).isNotNull();

    }


    private static class SomeClass{

    }

}
