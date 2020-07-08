# micronaut-http-client
An attempt to reproduce potential issue with Micronaut declarative http client.

## Issue
The issue seems to be with Micronaut declarative http clients where they start erroring out with _io.micronaut.http.client.exceptions.ReadTimeoutException: Read Timeout_ when several concurrent requests are in play (more than 20 in test here) using the clients. This seem to only happen (wierdly) when a declarative client is used in conjunction with micronaut security, HttpClientFilter and Controller.

The application flow in the repo seen here is similar to what we have for our application and the strange part is that the errror is reproducible only when validation client, token issue client and service client all are used. If we remove any one of these the test starts to pass. Initially we thought that the issue is exactly as reported here: https://github.com/micronaut-projects/micronaut-core/issues/2905 but trying out the workaround suggested in this post does not fix the issue for us. 

## How to reproduce
_./gradlew test_ can be run from commamnd line in debug mode to see error _io.micronaut.http.client.exceptions.ReadTimeoutException: Read Timeout_ coming from Micronaut Http client. However it can be best viewed by importing the project within intellij , Eclipse or any other editor of choice and running the test *StatusIntegrationSpec* directly and reviewing console logs.

On my machine the test fails when CONCURRENT_REQUESTS_COUNT field defined within StatusIntegrationSpec has a value of 20 (sometimes fails with 10 or more).
It works mostly when it is less than 10.

