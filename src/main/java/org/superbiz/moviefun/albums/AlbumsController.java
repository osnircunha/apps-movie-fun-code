package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore blobStore;

    @Autowired
    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {

        Blob blob = new Blob(String.format("covers/%d", albumId),uploadedFile.getInputStream(), uploadedFile.getContentType());
        blobStore.put(blob);

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException {
        String name = String.format("covers/%d", albumId);
        Optional<Blob> optionalBlob = blobStore.get(name);
        HttpHeaders headers;
        byte[] imageBytes;

        if(optionalBlob.isPresent()) {
            Blob blob = optionalBlob.get();
            imageBytes = new byte[blob.inputStream.available()];
            blob.inputStream.read(imageBytes);
            headers = createImageHttpHeaders(imageBytes, blob.contentType);
        } else {
            Path coverFilePath = Paths.get(new ClassPathResource("default-cover.jpg").getFile().toURI());
            imageBytes = Files.readAllBytes(coverFilePath);
            headers = createImageHttpHeaders(imageBytes, new Tika().detect(imageBytes));
        }

        return new HttpEntity<>(imageBytes, headers);
    }

    private HttpHeaders createImageHttpHeaders(byte[] imageBytes , String contentType) throws IOException {
       HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

}
