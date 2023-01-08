package com.service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.github.underscore.U;
import com.database.Record;
import com.database.RecordRepository;
import com.models.PathLocation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class DemoApplication {

    @Autowired
    private RecordRepository recordRepo;

    @GetMapping("/get-records")
    @Operation(description = "Get the records by filename paginated")
    public String getRecords(
            @Parameter(description = "Filename to search. In case of empty will get all the records", example = "sample_data.csv") @RequestParam(value = "filename", defaultValue = "") String filename,
            @Parameter(description = "Skip some recors") @RequestParam(value = "skip", defaultValue = "0") int skip,
            @Parameter(description = "Limit some records") @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        List<Record> res = filename.isEmpty()
                ? recordRepo.findAll(skip, limit)
                : recordRepo.findAll(skip, limit, filename);

        return U.map(res, x -> x.toString()).toString();
    }

    @PostMapping("/start-monitoring")
    @Operation(description = "Start watching a folder. Only the CSV files just created/dropped will be inserted in MongoDB. Exp path: \"C:\\Users\\user1\\Music\\test\" or \"C:/Users/user1/Music/test\"")
    public ResponseEntity startMonitoring(@RequestBody PathLocation pathLocation) {
        pathLocation.validatePath();
        pathLocation.startMonitoring(recordRepo);

        return new ResponseEntity(
                "Watching: " + pathLocation.getPath(),
                HttpStatus.OK
        );
    }

}
