Spring SQL Injection Demo
===

This demo shows how Spring can help increase your protection against SQL injection attacks, which continues to top the [OWASP Top 10 Web Application Security Risks](https://www.owasp.org/images/7/72/OWASP_Top_10-2017_%28en%29.pdf.pdf) list. 

The demo has two endpoints, one that constructs SQL queries manually and another demonstrating how data should be access using [Spring Data JPA](https://spring.io/projects/spring-data-jpa). The demo also uses an embedded H2 database, so there's no requirement on setting up an external database. You can run the demo with the following command:

`./mvnw spring-boot:run`

/unsafe
---

The `/unsafe` endpoint takes the user's input to manually construct a SQL query and get the database entries. The idea is that a user would provide a product id (perhaps via a web frontend) and the app would return information about that product. It would do so by running the following SQL query:

`statement.executeQuery("select * from product where id = " + id);`

If a request if sent to [http://localhost:8080/unsafe/1](http://localhost:8080/unsafe/1), you'll see one product returned:

`Football: $19.99`

However, if we construct our request in such a way to manipulate the request, we can change the information that we get. What if we made a request that would match EVERY product in the database? It turns out that we could do that with a query of:

`select * from product where id = 1 OR 1=1`

In this case, `OR 1=1` is how we can make this query dump the entire database of products, because it will always evaluate to `true`. In turn, we can change the HTTP request to our app to trick it into running this new query: 

[http://localhost:8080/unsafe/1%20OR%201=1](http://localhost:8080/unsafe/1%20OR%201=1)

And we'll see our entire inventory is returned in our application:

```
Football: $19.99
Baseball: $14.99
Basketball: $17.99
Soccer Ball: $18.99
Helmet: $24.99
```

/safe
---

The `/safe` endpoint uses Spring Data JPA and a standard [JpaRepository](https://github.com/BrianMMcClain/spring-sql-injection-demo/blob/master/src/main/java/com/github/brianmmcclain/sqlinjectiondemo/ProductRepository.java) to manage how data is accessed in our database. If we send a similar, unmalicious request to our application, we'll see the same result of a single product:

[http://localhost:8080/safe/1](http://localhost:8080/safe/1)

`Football: $19.99`

However, if we send the malicious request to attempt the same exploit, we'll see a different result:

[http://localhost:8080/safe/1%20OR%201=1](http://localhost:8080/safe/1%20OR%201=1)

```
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Wed Nov 06 10:38:54 EST 2019
There was an unexpected error (type=Bad Request, status=400).
Failed to convert value of type 'java.lang.String' to required type 'int'; nested exception is java.lang.NumberFormatException: For input string: "1OR1=1"
```

Our application throws an error, specifically because it was expecting an integer for an ID but received a string, and had an issue parsing it. This means we can catch this exception in our code and handle it appropriately, rather than let any request an attacker can craft run against our database.