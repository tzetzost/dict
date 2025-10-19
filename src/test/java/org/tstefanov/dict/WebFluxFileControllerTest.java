package org.tstefanov.dict;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class WebFluxFileControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public CommandLineRunner testCommandLineRunner() {
            return args -> {
                // Do nothing, preventing the production runner from executing
            };
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StorageService storageService;

    @Test
    void handleFileUpload_shouldReturnOk_whenFilesAreValid() {
        given(storageService.store(any(Flux.class))).willReturn(Mono.empty());

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("files", new ByteArrayResource("test content 1".getBytes())).filename("file1.txt");
        bodyBuilder.part("files", new ByteArrayResource("test content 2".getBytes())).filename("file2.txt");

        webTestClient.post().uri("/webflux-files/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("All files uploaded successfully.");
    }

    @Test
    void handleFileUpload_shouldReturnBadRequest_whenMoreThanThreeFiles() {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("files", new ByteArrayResource("1".getBytes())).filename("file1.txt");
        bodyBuilder.part("files", new ByteArrayResource("2".getBytes())).filename("file2.txt");
        bodyBuilder.part("files", new ByteArrayResource("3".getBytes())).filename("file3.txt");
        bodyBuilder.part("files", new ByteArrayResource("4".getBytes())).filename("file4.txt");

        webTestClient.post().uri("/webflux-files/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Cannot upload more than 3 files at a time.");
    }

    @Test
    void serveFile_shouldReturnFile_whenFileExists() {
        // Given
        // This is the definitive fix: Use a real ByteArrayResource that has both
        // content (for the body) and a description (for the filename).
        Resource resource = new ByteArrayResource("file content".getBytes()) {
            @Override
            public String getFilename() {
                return "test-file.txt";
            }
        };
        given(storageService.loadAsResource("test-file.txt")).willReturn(Mono.just(resource));

        // When & Then
        webTestClient.get().uri("/webflux-files/download/test-file.txt")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test-file.txt\"")
                .expectBody(String.class).isEqualTo("file content");
    }

    @Test
    void serveFile_shouldReturnNotFound_whenFileDoesNotExist() {
        // Given
        given(storageService.loadAsResource("not-found.txt")).willReturn(Mono.empty());

        // When & Then
        webTestClient.get().uri("/webflux-files/download/not-found.txt")
                .exchange()
                .expectStatus().isNotFound();
    }
}
