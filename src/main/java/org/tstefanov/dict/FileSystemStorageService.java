package org.tstefanov.dict;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @PostConstruct
    public void init() {
        try {
            // For development: clean up previous files on restart.
            // For production, this line should be removed or commented out.
            deleteAll().block();

            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public Mono<Void> store(Flux<FilePart> fileParts) {
        return fileParts.flatMap(filePart -> {
            Path destinationFile = this.rootLocation.resolve(Paths.get(filePart.filename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                return Mono.error(new StorageException("Cannot store file outside current directory."));
            }
            return filePart.transferTo(destinationFile);
        }).then();
    }

    @Override
    public Flux<Path> loadAll() {
        try {
            return Flux.fromStream(Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize));
        } catch (IOException e) {
            return Flux.error(new StorageException("Failed to read stored files", e));
        }
    }

    @Override
    public Mono<Resource> loadAsResource(String filename) {
        return Mono.fromSupplier(() -> {
            try {
                Path file = rootLocation.resolve(filename);
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new StorageFileNotFoundException("Could not read file: " + filename);
                }
            } catch (MalformedURLException e) {
                throw new StorageFileNotFoundException("Could not read file: " + filename, e);
            }
        });
    }

    @Override
    public Mono<Void> deleteAll() {
        return Mono.fromRunnable(() -> FileSystemUtils.deleteRecursively(rootLocation.toFile()));
    }
}
