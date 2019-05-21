# Testing Spring controllers
- Integration testing
- Using old style @ContextConfiguration to load the context (using MockMVC)
- Using @WebMvcTest

Integration testing
@SpringBootTest does not use slicing at all which means it'll start your full application context and not customize
component scanning at all.

Using @WebMvcTest
Is very convenient way to test Spring MVC controllers in isolation. Adding this annotation injects MockMvc instance which
is initialized with all project level MVC Configurations (like ContrtollerAdvice etc..)

Old Style MockMvc usage
In this test case, MockMvc is created and initialized inside setUp. In this case all MVC related configuration needs
to be manually setup against the MockMvc object


# This project reflects error handling and some spring MVC default configuration overriding.
- It set up global exception to handle Spring framework generated errors
- Deals mostly with client error 4xx. API send a decent client error. Give client as much useful information.
- Deals with 5xx error

# Different types of error scenarios handled -
- Strictness in  data structure. If any extra data passed, request is rejected. @see PaymentsControllerTest.createPaymentFail_ShouldExtraUnwantedParametersPassed
- Strictness in content. Missing data will result in error. @see PaymentsControllerTest.paymentCreationFail_ShouldFromIsMissing
- Content validation. Negative amount results in BadRequest(400). @see PaymentsControllerTest.paymentCreationFail_ShouldAmountIsNegative


# Annotations information:

| Annotation | Information |
| ------ | ------ |
|@RunWith(SpringJUnit4ClassRunner.class)|tells JUnit to invoke the Spring test wrapper which allows the Web App Context to be loaded. By default Spring will load the context into a Static variable so it only gets initialized once per test run saving a lot of time. This his helpful if you create a base test class with the context info and reuse it across your project.|
|@WebAppConfiguration|Lets the Spring wrapper know that we want a WebApplicationContext loaded for the project. This is needed for the Mock MVC setup.|
|@RunWith(SpringRunner.class)|Alias for @RunWith(SpringJUnit4ClassRunner.class) |
|@WebMvcTest(controllers = {PaymentsController.class})|@WebMvcTest auto-configures the Spring MVC infrastructure and limits scanned beans to @Controller, @ControllerAdvice, @JsonComponent, Converter, GenericConverter, Filter, WebMvcConfigurer, and HandlerMethodArgumentResolver. Regular @Component beans are not scanned when using this annotation.|
|@ContextConfiguration(classes = {AppConfiguration.class, PaymentsController.class})|It defines which Spring components we want to load for the test Context.|



