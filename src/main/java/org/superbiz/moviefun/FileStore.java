package org.superbiz.moviefun;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Component
@Profile("!cloud")
public class FileStore implements BlobStore {

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);

        byte[] buffer = new byte[blob.inputStream.available()];
        blob.inputStream.read(buffer);

        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File coverFile = new File(name);

        if(!coverFile.exists()) {
            return Optional.ofNullable(null);
        }

        Path coverFilePath = coverFile.toPath();

        byte[] bytes = Files.readAllBytes(coverFilePath);
        InputStream targetStream = new ByteArrayInputStream(bytes);
        String contentType = new Tika().detect(bytes);
        Blob blob = new Blob(name, targetStream, contentType);

        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }

}
