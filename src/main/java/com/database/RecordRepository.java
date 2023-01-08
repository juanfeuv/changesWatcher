/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.database;

import java.util.List;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 *
 * @author pipel
 */
public interface RecordRepository extends MongoRepository<Record, String> {

    @Aggregation(pipeline = {
        "{ '$match': { 'filename' : ?2 } }",
        "{ '$skip' : ?0 }",
        "{ '$limit' : ?1 }"
    })
    List<Record> findAll(int skip, int limit, final String filename);

    @Aggregation(pipeline = {
        "{ '$match': {} }",
        "{ '$skip' : ?0 }",
        "{ '$limit' : ?1 }"
    })
    List<Record> findAll(int skip, int limit);

    @Query(value = "{filename:'?0'}")
    public long count(String filename);

}
