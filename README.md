# Async all the way down

An opinionated, asynchronous, reactive Dropwizard microservice. Uses:

 - Asynchronous JAX-RS server via RX
 - Chunked responses via RX `Flowable`s
 - Asynchronous Postgres driver
 - Asynchronous JAX-RS client (TODO)

How to try it out
---

1. Make sure you have Docker set up and working (it uses TestContainers to start a Postgres instance)
1. Open in your IDE
1. Run the `DemoTest` test to start the server over a temporary database instance
1. Once the test is up and running, call the following endpoints from your browser:
   1. `http://localhost:8081/demo/write` to write 100 records to the database, returning them to the browser immediately as they are written
   1. `http://localhost:8081/demo/read` to read the records back in the same way
   1. `http://localhost:8081/demo/count` to read the record count
   1. `http://localhost:8081/demo/reset` to delete the records

Note that responses are streamed back (deliberately slowed down to show chunked responses in progress) and don't block a server thread. The database I/O doesn't block a worker thread either - both use NIO, so you don't need a large thread pool to support high volume.

It's all magic
---
Check out [the JAX-RS Resource](https://github.com/badgerwithagun/async-all-the-way-down/blob/master/src/main/java/com/gruelbox/asyncalltheway/DemoResource.java) - note that all this is achieved largely transparently as long as you write the endpoints using reactive logic.
