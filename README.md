# Snippet Searcher Asset Service
Service of the Snippet Searcher platform in charge of storing assets to a bucket.

### How to use this API?

Start the docker image and go to "http://localhost:8080/swagger-ui"

### How to use this Docker Image

To see configurations: see `docker-compose.yml` file.
This image is uploaded to Github Container Registry so you will have to search their docs to know how to pull it.

### How to run tests?

This service uses [test-containers](https://testcontainers.com/) to run tests as the main idea of this service is to
interact with Azurite. You need Docker up and running to run tests.