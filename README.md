**Enivronment and Setup**
IDE: Intellij
Plugins: Lombok 
IDE Settings: Enable annotation processing
Build tool: Maven
Tools: Postman, httpbin.org

**Approach and thought process**
First and foremost, my approach may not be the cleanest since this is my first time implementing a spring cloud gateway. My experience in using microservices and calling third-party APIs did not involve the requests going through a gateway so a lot of time was spent studying here.

Firstly, I realized that there are two approaches to spring API gateways. There is a config approach that uses a properties file and the programatic approach. The latterr is the one I used.

I found a blog post detailing a guide with how to start creating a routing gateway in the form of the bean. This is used to be hardcoded in ProxyApplication.java but I moved it to its own class in src/main/java/com/cloud/proxy/config/RouteConfig.java. 
I also externalized the URL for the backend service in src/main/java/com/cloud/proxy/config/UriConfig.java in order to make it easier to test.

After adding a basic GET route to make sure I understood correctly, I started working on a basic unit test. 
The basic one of calling a the URL seemed to work fine at first but I had trouble in setting up the unit tests so that it will intercept the HTTP request to the backend server and verify the correct headers and body was passed. 
Until now, I couldn't quite figure out how to do it. 
I did however, create unit tests to at least verify the correct routing was established in src/test/java/com/cloud/proxy/ProxyApplicationTests.java

In order to test my code and verify the functionality, I used Postman to send HTTP requests and changed the API URL to httpbin.org since they have public APIs that can reflect the request the data passed to it, making it ideal for testing. 
There are also some code changes I did for testing the code. These are marked by the comment "For local testing".
Once I verified the GET endpoint was working, I went on classes that would serve as models for our objects. I also annotated them with Lombok to simplify initialization.

After which, I started on the create endpoint. I tried to add the logic to verify the data passed to it with the User.java model but for some reason, it would end up not calling the backend API. I left the code in the repository anyways.
Next came the update endpoint. This one was using POST in the backend service so to reflect REST API best practices, I made the gateway endpoint to take PUT requests and then mutate the request method to POST when calling the backend.
The same thing was done on the delete endpoint since it was using POST on the backend service as well so I made the gateway endpoint receive DELETE requests.

After which, I added the rest of the unit tests that used WebTestClient and Wiremock. At this point, I knew that the backend services for update and delete should be stubbed with a POST method but when I tested it, it didn't seem to mutate the request or it called the backend service directly.
There may be something wrong with how I set up the unit tests.

Lastly, I created the API spec in Open API format in src/main/resources/registration-gateway.yml

**Testing procedure for local run**
1. Uncomment code and replace their corresponding lines. The testing code should be marked with the comment "For local testing".
2. Run using the maven configuration with spring-boot:run
3. Send requests using postman
   Sample CURL request:
   curl --location 'http://localhost:8080/post' \
        --header 'Content-Type: application/json' \
        --data '
        {"firstName": "John",
        
        "lastName" : "Doe"
        
        }'
4. Output should be similar to the httpbin endpoint. Response should also display the trace ID added in the headers.

**If given more time**
I would continue finding a way how to intercept and verify a request sent to the backend service in order to properly test the gateway functionalities and filters.
I would also properly implement the validation logic of the request body for the create and update endpoints. 
I would also add some more error handling as well as a global exception handler.
I feel like if only I had a head start on Spring Cloud Gateways, I would be able to implement the rest of the functionalities as well as properly set up unit tests


