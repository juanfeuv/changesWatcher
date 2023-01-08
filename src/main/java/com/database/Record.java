/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.database;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author pipel
 */
@Document("records")
public class Record {

    private String col1 = null;
    private String col2;
    private Integer col3 = null;
    private Double col4;
    private String filename;
    private String directory;

    public Record(String col1, String col2, Integer col3, Double col4, String filename, String directory) {
        super();
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.filename = filename;
        this.directory = directory;
    }

    @Override
    public String toString() {
        return String.format("{\"col1\": %s, \"col2\": %s, \"col3\": %s, \"col4\": %s}", this.col1.isBlank() ? null : "\"" + this.col1 + "\"", this.col2.isBlank() ? null : "\"" + this.col2 + "\"", this.col3, this.col4);
    }

}
