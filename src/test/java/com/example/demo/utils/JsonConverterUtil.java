package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@Slf4j
public class JsonConverterUtil {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public  <T> T readJsonFile(String filePath, TypeReference<T> valueTypeRef) {
        T data = null;
        try {
            String content = readContentFile(filePath);
            data = objectMapper.readValue(content, valueTypeRef);
        }
        catch (JsonProcessingException errorJson) {
            log.error(errorJson.getMessage(),errorJson);
        }
        catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return data;
    }
    public  String readContentFile(String filePath) {
        String content = null;
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            File file = resource.getFile();
            content = new String(Files.readAllBytes(file.toPath()));
        }
        catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return content;
    }
}
