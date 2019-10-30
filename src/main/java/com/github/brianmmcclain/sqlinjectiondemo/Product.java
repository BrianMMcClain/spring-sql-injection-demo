package com.github.brianmmcclain.sqlinjectiondemo;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {

    @Id
    public int id; 

    public String name;

    public float price;
}