package com.gora.backend.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.gora.backend.exception.BadRequestException;

@Component
public class CommonUtils {
    public InputStream loadResourceAsStream(String resourceLocation) throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceLocation);
        return resourceAsStream;
    }

    public void saveToFile(InputStream is, File file) throws IOException {
        try (OutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    public Resource convertFileToResource(String resourceLocation, String tempFilePath){
        try (InputStream inputStream = loadResourceAsStream(resourceLocation)) {
            Resource resource;
            
            File outputFile = new File(tempFilePath);
            saveToFile(inputStream, outputFile);
            resource = new FileSystemResource(outputFile);

            return resource;
        } catch (IOException e) {
            return null;
        }
    }
}
