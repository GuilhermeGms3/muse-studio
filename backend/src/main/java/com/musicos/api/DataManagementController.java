package com.musicos.api;

import static com.musicos.api.ApiModels.*;

import com.musicos.service.DataManagementService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/data")
public class DataManagementController {
    private final DataManagementService service;

    public DataManagementController(DataManagementService service) {
        this.service = service;
    }

    @GetMapping("/backup")
    public BackupSnapshot backup() {
        return service.backup();
    }

    @PostMapping("/restore")
    public RestoreResult restore(@RequestBody BackupSnapshot snapshot) {
        return service.restore(snapshot);
    }

    @GetMapping("/journal.csv")
    public ResponseEntity<byte[]> journalCsv() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=music-os-journal.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=utf-8"))
                .body(service.journalCsv());
    }

    @PostMapping(value = "/imports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportedFileView importFile(@RequestPart MultipartFile file) {
        return service.importFile(file);
    }

    @GetMapping("/status")
    public DataStatusView status() {
        return service.status();
    }
}
