package org.tstefanov.dict;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final Map<String, JobStatus> jobStatuses = new ConcurrentHashMap<>();

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public Path store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                    StandardCopyOption.REPLACE_EXISTING);
            }
            return destinationFile;
        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    @Async("fileUploadExecutor")
    public void processFiles(String jobId, List<Path> filePaths) {
        JobStatus status = new JobStatus();
        int totalFiles = filePaths.size();
        try {
            status.setStatus(JobStatusEnum.PROCESSING);
            status.setProgress(0);
            jobStatuses.put(jobId, status);

            // This now simulates processing the files that are already saved.
            for (int i = 0; i < totalFiles; i++) {
                // Simulate work on each file
                Thread.sleep(1000); // Placeholder for actual work
                double progress = ((double) (i + 1) / totalFiles) * 100.0;
                status.setProgress(progress);
                jobStatuses.put(jobId, status);
            }

            status.setStatus(JobStatusEnum.COMPLETED);
            status.setProgress(100.0);
            jobStatuses.put(jobId, status);

        } catch (Exception e) {
            status.setStatus(JobStatusEnum.FAILED);
            jobStatuses.put(jobId, status);
        }
    }

    @Override
    public JobStatus getJobStatus(String jobId) {
        return jobStatuses.get(jobId);
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                .filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
