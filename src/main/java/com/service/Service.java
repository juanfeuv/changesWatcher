/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.database.RecordRepository;

/**
 *
 * @author pipel
 */
@EnableMongoRepositories(basePackageClasses = RecordRepository.class)
@SpringBootApplication
public class Service {

    public static void main(String[] args) {
        SpringApplication.run(Service.class, args);
    }

}
