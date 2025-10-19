package org.tstefanov.dict;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    Path store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void processFiles(String jobId, List<Path> filePaths);

    JobStatus getJobStatus(String jobId);

}
