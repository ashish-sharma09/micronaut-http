# micronaut-http
An attempt to reproduce potential issue with Micronaut declarative http client.

Test can be run from commamnd line using gradle in debug mode to see error _io.micronaut.http.client.exceptions.ReadTimeoutException: Read Timeout_ from Micronaut Http client. However it can be best viewed by importing the project in intellij , Eclipse or any other editor of choice and running the test *StatusIntegrationSpec*.

On my machine the test fails when CONCURRENT_REQUESTS_COUNT field defined within StatusIntegrationSpec has a value of 20 (sometimes fails with 10 or more).
It works mostly when it is less than 10.
