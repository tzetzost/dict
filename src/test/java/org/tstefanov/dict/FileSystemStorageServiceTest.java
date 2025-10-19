package org.tstefanov.dict;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileSystemStorageServiceTest {

    @TempDir
    Path tempDir;

    private StorageProperties properties;
    private FileSystemStorageService storageService;

    @BeforeEach
    void setUp() {
        properties = new StorageProperties();
        properties.setLocation(tempDir.toString());
        storageService = new FileSystemStorageService(properties);
    }

    @Test
    void store_shouldSaveFile_whenFilePartIsValid() {
        // Given
        FilePart filePart = mock(FilePart.class);
        Path destinationPath = tempDir.resolve("test.txt");
        when(filePart.filename()).thenReturn("test.txt");
        when(filePart.transferTo(destinationPath)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = storageService.store(Flux.just(filePart));

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void store_shouldThrowException_whenPathIsOutsideDirectory() {
        // Given
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("../../evil.txt");

        // When
        Mono<Void> result = storageService.store(Flux.just(filePart));

        // Then
        StepVerifier.create(result)
                .expectError(StorageException.class)
                .verify();
    }

    @Test
    void loadAsResource_shouldReturnResource_whenFileExists() throws IOException {
        // Given
        Path testFile = tempDir.resolve("existing-file.txt");
        Files.write(testFile, "Hello, World!".getBytes());

        // When
        Mono<Resource> result = storageService.loadAsResource("existing-file.txt");

        // Then
        StepVerifier.create(result)
                .assertNext(resource -> {
                    assertThat(resource.exists()).isTrue();
                    assertThat(resource.isReadable()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void loadAsResource_shouldThrowException_whenFileDoesNotExist() {
        // When
        Mono<Resource> result = storageService.loadAsResource("not-found.txt");

        // Then
        StepVerifier.create(result)
                .expectError(StorageFileNotFoundException.class)
                .verify();
    }
}
