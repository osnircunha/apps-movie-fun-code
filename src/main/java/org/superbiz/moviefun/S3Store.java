package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class S3Store implements BlobStore {


    private AmazonS3Client s3Client;
    private String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {

        this.photoStorageBucket = photoStorageBucket;
        this.s3Client = s3Client;
    }

    @Override
    public void put(Blob blob) throws IOException {
        s3Client.putObject(photoStorageBucket, blob.name, blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) {
        try {
            S3Object s3Object = s3Client.getObject(photoStorageBucket, name);
            byte[] data = IOUtils.toByteArray(s3Object.getObjectContent());
            InputStream targetStream = new ByteArrayInputStream(data);

            Blob blob = new Blob(name, targetStream, new Tika().detect(data));
            return Optional.of(blob);
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    @Override
    public void deleteAll() {

    }
}
