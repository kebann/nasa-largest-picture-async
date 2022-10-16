## Usage

- Build a docker image for the NASA picture service `mvn spring-boot:build-image`
- Start docker containers with Redis and the URL shortener by running `docker-compose up -d`
- Make a request to find the largest NASA picture for a given combination of sol and camera parameters

```curl 
curl --location --request POST 'localhost:8080/mars/pictures/largest' \
--header 'Content-Type: application/json' \
--data-raw '{
    "sol": 15,
    "camera": "FHAZ"
}'
```

- Copy a URL value from `Location` header in the response and use it to obtain the content of the largest NASA picture
  if found
- To stop the app run `docker-compose down`