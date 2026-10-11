
package com.interiorai.service;

import com.interiorai.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class FileStore {

    private final Path root;

    public FileStore(@Value("${app.upload-dir}") String upload) {
        try {
            root = Paths.get(upload).toAbsolutePath().normalize();
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Could not initialize private image folder", e
            );
        }
    }

    public String put(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.bad("Please upload a room photo.");
        }

        if (file.getSize() > 8 * 1024 * 1024) {
            throw new ApiException(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Image must be smaller than 8 MB."
            );
        }

        String mime = file.getContentType();

        String original = file.getOriginalFilename() == null
            ? ""
            : file.getOriginalFilename().toLowerCase(Locale.ROOT);

        if (!original.matches("(?s).*\\.(jpg|jpeg|png|webp)")) {
            throw ApiException.bad(
                "File extension must be JPG, PNG or WebP."
            );
        }

        String ext = extFromSignature(file, mime);

        if (!original.endsWith("." + ext)
                && !(ext.equals("jpg") && original.endsWith(".jpeg"))) {
            throw ApiException.bad(
                "File extension does not match the image format."
            );
        }

        String name = UUID.randomUUID() + "." + ext;

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, resolve(name));
        } catch (IOException e) {
            throw new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to store image."
            );
        }

        return name;
    }

    private String extFromSignature(MultipartFile f, String mime) {
        if (mime == null || !List.of(
                "image/jpeg", "image/png", "image/webp"
            ).contains(mime)) {
            throw ApiException.bad("Use a JPG, PNG or WebP image.");
        }

        try (InputStream stream = f.getInputStream()) {
            byte[] b = stream.readNBytes(12);

            boolean jpg = b.length >= 3
                && (b[0] & 255) == 255
                && (b[1] & 255) == 216
                && (b[2] & 255) == 255;

            boolean png = b.length >= 8
                && (b[0] & 255) == 137
                && b[1] == 80
                && b[2] == 78
                && b[3] == 71;

            boolean webp = b.length >= 12
                && new String(
                    b, 0, 4,
                    java.nio.charset.StandardCharsets.US_ASCII
                ).equals("RIFF")
                && new String(
                    b, 8, 4,
                    java.nio.charset.StandardCharsets.US_ASCII
                ).equals("WEBP");

            if (jpg && mime.equals("image/jpeg")) return "jpg";
            if (png && mime.equals("image/png")) return "png";
            if (webp && mime.equals("image/webp")) return "webp";

            throw ApiException.bad(
                "Image content does not match its file type."
            );

        } catch (IOException e) {
            throw ApiException.bad("Unable to read image.");
        }
    }

    public String putGenerated(byte[] bytes) {
        String name = UUID.randomUUID() + ".png";

        try {
            Files.write(
                resolve(name),
                bytes,
                StandardOpenOption.CREATE_NEW
            );
        } catch (IOException e) {
            throw new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to store generated image."
            );
        }

        return name;
    }

    public Path resolve(String name) {
        if (!name.matches("[0-9a-fA-F-]{36}\\.(png|jpg|webp)")) {
            throw ApiException.bad("Invalid image path.");
        }

        Path p = root.resolve(name).normalize();

        if (!p.startsWith(root)) {
            throw ApiException.forbidden();
        }

        return p;
    }

    public byte[] read(String name) {
        try {
            return Files.readAllBytes(resolve(name));
        } catch (IOException e) {
            throw ApiException.notFound("Image not found.");
        }
    }

    public void remove(String name) {
        if (name == null) return;

        try {
            Files.deleteIfExists(resolve(name));
        } catch (IOException ignored) {
        }
    }

    public static String mime(String name) {
        return name.endsWith(".webp")
            ? "image/webp"
            : name.endsWith(".jpg")
                ? "image/jpeg"
                : "image/png";
    }
}
