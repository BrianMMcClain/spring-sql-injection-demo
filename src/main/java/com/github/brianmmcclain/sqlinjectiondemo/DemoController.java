package com.github.brianmmcclain.sqlinjectiondemo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;


@Controller
public class DemoController {

    @Autowired
    ProductRepository productRepo;

    private Connection connection;

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

    @RequestMapping(value = "/inject/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String inject(@PathVariable("id") String id) {
        try {
            // Connect to MySQL database and execute query
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "mypass");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from product where id = " + id);
            
            // Iterate through results
            StringBuilder ret = new StringBuilder();
            while (rs.next()) {

                // Build result string
                int rID = rs.getInt(1);
                String rName = rs.getString(2);
                float rPrice = rs.getFloat(3);
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