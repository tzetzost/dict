package org.tstefanov.dict;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public interface StorageService {

    /**
     * Stores a stream of uploaded files.
     * @param fileParts A Flux of FilePart objects to be stored.
     * @return A Mono that completes when all files are stored.
     */
    Mono<Void> store(Flux<FilePart> fileParts);

    /**
     * Loads all stored files.
     * @return A Flux of Path objects.
     */
    Flux<Path> loadAll();

    /**
     * Loads a single file as a Spring Resource.
     * @param filename The name of the file to load.
     * @return A Mono containing the Resource, or an empty Mono if not found.
     */
    Mono<Resource> loadAsResource(String filename);

    /**
     * Deletes all stored files.
     */
    Mono<Void> deleteAll();
}
