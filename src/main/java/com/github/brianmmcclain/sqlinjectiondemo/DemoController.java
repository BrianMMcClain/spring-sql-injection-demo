package com.github.brianmmcclain.sqlinjectiondemo;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
public class DemoController {

    @Autowired
    ProductRepository productRepo;

    private Connection connection;

    Logger logger = LoggerFactory.getLogger(DemoController.class);

    /**
     * The /safe endpoint takes in an ID and looks up a product using Spring Data JPA.
     * 
     * @param id Product ID to look up
     * @return Raw string containing product name and price
     */
    @RequestMapping(value = "/safe/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String safe(@PathVariable("id") int id) {
        Optional<Product> p = productRepo.findById(id);
        if (p.isPresent()) {
            return p.get().name + ": $" + p.get().price;
        } else {
            return "No product found!";
        }
    }

    /**
     * The /unsafe endpoint takes in an ID and looks up a product by concatenating
     * the provided product ID to a manual SQL query. This present as very easy way
     * to perform a SQL injection attack and manipulate the query sent to our database.
     * 
     * @param id Product ID to look up
     * @return Raw string containing product name and price (or more for a crafty user)
     */
    @RequestMapping(value = "/unsafe/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String unsafe(@PathVariable("id") String id) {
        try {
            // Connect to MySQL database and execute query
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "mypass");
            Statement statement = connection.createStatement();
            String sqlQuery = "select * from product where id = " + id;
            logger.info("Executing: " + sqlQuery);
            ResultSet rs = statement.executeQuery(sqlQuery);
            
            // Iterate through results
            StringBuilder ret = new StringBuilder();
            while (rs.next()) {

                // Build result string
                int rID = rs.getInt(1); // ID
                String rName = rs.getString(2); // Name
                float rPrice = rs.getFloat(3); // Price
                if (ret.length() > 0) {
                    ret.append("<br />");
                }
                ret.append(rName + ": $" + rPrice);
            }
            return ret.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error connecting to database!";
        }
    }
}