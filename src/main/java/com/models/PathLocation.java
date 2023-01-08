/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.models;

import com.database.RecordRepository;
import com.database.Record;
import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author pipel
 */
public class PathLocation {

    private static com.google.common.io.Files googleFiles;

    @Autowired
    private RecordRepository recordRepo;

    private String path;

    public String getPath() {
        return path;
    }

    public void validatePath() {

        ResponseStatusException empty = new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Path is empty"
        );

        ResponseStatusException exists = new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Directory does not exist"
        );

        try {
            if (this.path.isBlank()) {
                throw empty;
            }
        } catch (Exception e) {
            throw empty;
        }

        try {
            Path directory = Path.of(this.path);

            if (!Files.exists(directory)) {
                throw exists;
            }
        } catch (Exception e) {
            throw exists;
        }
    }

    public void startMonitoring(RecordRepository recordRepo) {
        this.recordRepo = recordRepo;

        Thread newThread = new Thread(() -> {
            try {

                System.out.println("Watching directory for changes: " + this.path);

                // STEP1: Create a watch service
                WatchService watchService = FileSystems.getDefault().newWatchService();

                // STEP2: Get the path of the directory which you want to monitor.
                // "G:\\projects\\fullstackdeveloperblog\\watchapi"
                Path directory = Path.of(this.path);

                // STEP3: Register the directory with the watch service
                WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

                // STEP4: Poll for events
                while (true) {
                    for (WatchEvent<?> event : watchKey.pollEvents()) {

                        // STEP5: Get file name from even context
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

                        Path fileName = pathEvent.context();

                        // STEP6: Check type of event.
                        WatchEvent.Kind<?> kind = event.kind();

                        // STEP7: Perform necessary action with the event
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {

                            System.out.println("A new item is created : " + fileName + " in " + this.path);

                            this.insertRecords(fileName.toString());
                        }

                        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {

                            System.out.println("An item has been deleted: " + fileName + " in " + this.path);
                        }
                        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

                            System.out.println("An item has been modified: " + fileName + " in " + this.path);
                        }

                    }

                    // STEP8: Reset the watch key everytime for continuing to use it for further event polling
                    boolean valid = watchKey.reset();
                    if (!valid) {
                        break;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        newThread.start();
    }

    public void insertRecords(String filename) {
        Path dirFilename = Paths.get(this.path, filename);

        if (Files.isDirectory(dirFilename) || !googleFiles.getFileExtension(dirFilename.toString()).equals("csv")) {
            System.out.println(filename + " is not a CSV");
            return;
        }

        CSVReader reader = null;

        try {
            //parsing a CSV file into CSVReader class constructor
            reader = new CSVReader(new FileReader(dirFilename.toString()));
            String[] nextLine = reader.readNext();

            //reads one line at a time
            while ((nextLine = reader.readNext()) != null) {
                Record newRecord = new Record(
                        !nextLine[0].isBlank() ? nextLine[0] : null,
                        !nextLine[1].isBlank() ? nextLine[1] : null,
                        !nextLine[2].isBlank() ? Integer.parseInt(nextLine[2].trim()) : null,
                        !nextLine[3].isBlank() ? Double.parseDouble(nextLine[3].trim()) : null,
                        filename,
                        this.path.replaceAll("/$", "")
                );

                recordRepo.save(newRecord);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            this.insertRecords(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
