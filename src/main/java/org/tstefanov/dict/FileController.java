package org.tstefanov.dict;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        // 1. Validate the number of files before any processing.
        if (files.length > 3) {
            return ResponseEntity.badRequest().body("Cannot upload more than 3 files at a time.");
        }

        // 2. Create a list to hold the paths of the saved files.
        List<Path> savedFilePaths = new ArrayList<>();

        // 3. Synchronously save each file and collect its new, permanent path.
        for (MultipartFile file : files) {
            Path path = storageService.store(file);
            savedFilePaths.add(path);
        }

        // 4. Now, start the asynchronous processing with the list of safe paths.
        String jobId = UUID.randomUUID().toString();
        storageService.processFiles(jobId, savedFilePaths);

        // 5. Immediately return the job ID to the client.
        return ResponseEntity.accepted().body(String.format("jobId: %s", jobId));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobStatus> getStatus(@PathVariable String jobId) {
        JobStatus status = storageService.getJobStatus(jobId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
