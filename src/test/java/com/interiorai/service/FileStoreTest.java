package com.interiorai.service;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;
class FileStoreTest {
 @Test void rejectsSpoofedImageType() throws Exception {var dir=Files.createTempDirectory("interiorai-test-");FileStore store=new FileStore(dir.toString());var fake=new MockMultipartFile("image","fake.png","image/png","not a png".getBytes());assertThrows(RuntimeException.class,()->store.put(fake));assertThrows(RuntimeException.class,()->store.read("../../etc/passwd"));}
}
