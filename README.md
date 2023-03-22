<h2> API CODING CHALLENGE </h2>
<h3> A Spring Boot REST app that exposes endpoints to </h3>

<li> Register a User with basic information including a unique username and password
<li> Upload, view and delete images after authorizing the username/password.
<li> Associate the updated list of images with the user profile
<li> View the User Basic Information and the Images

<h3> App features </h3>
<li> Uses H2 (In-memory database) and JPA to store the user information with username and password,
retrieve the username and password to authenticate the user
<li> Password encryption / decryption </li>
<li> Basic authentication </li>
<li> Authorized endpoints </li>
<li> Integrated with imgur’s API to upload, view and delete one image at a time. 
<li> 1 account created at Imgur for the app to integrate.
<li> No UI should is built. The app is to be accessed via the REST API’s.

<h3> Restrictions </h3>
<li> Only admin can create a generic user </li>
<li> Username needs to be unique </li>
<li> The user who has uploaded the image alone can view and delete it </li>

<h3> Steps to run </h3>
<li>mvnw clean install
<li>mvnw spring-boot:run
<li>Default server port http://localhost:8080 </li>

<h3> REST API endpoints </h3>
<li> see http://localhost:8080/swagger-ui/index.html#/ </li>
<li> All but swagger, admin endpoints require basic authentication </li>

<h3> Optimizations included </h3>
<li> Max image size limited to 10MB </li>
<li> Cache with time limit for user info </li>
<li> Cache with time limit for image metadata </li>

<h3> Future improvements </h3>
<li> Secure API via oAuth2 </li>
<li> Establish CI/CD Pipeline using Jenkins </li>
<li> Create a messaging event that publishes the username and the image name to a Messaging Platform </li>
<li> Limit number of requests </li>
<li> Pagination </li>
<li> Mark and schedule / queue delete image request</li>
