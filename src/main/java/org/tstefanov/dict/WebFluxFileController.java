package org.tstefanov.dict;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webflux-files")
public class WebFluxFileController {

    private final StorageService storageService;

    public WebFluxFileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> handleFileUpload(@RequestPart("files") Flux<FilePart> fileParts) {
        return fileParts
                .collectList() // Collect all FilePart objects into a list
                .flatMap(list -> {
                    if (list.size() > 3) {
                        // If more than 3 files, return a 400 Bad Request response
                        return Mono.just(ResponseEntity.badRequest().body("Cannot upload more than 3 files at a time."));
                    }
                    // If validation passes, store the files and then return a 200 OK response
                    return storageService.store(Flux.fromIterable(list))
                            .then(Mono.just(ResponseEntity.ok().body("All files uploaded successfully.")));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().body("Error uploading files: " + e.getMessage())));
    }

    @GetMapping("/download/{filename:.+}")
    public Mono<ResponseEntity<Resource>> serveFile(@PathVariable String filename) {
        return storageService.loadAsResource(filename)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
