package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class FileService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<String> readAllLines(Path path) {
        try {
            if (!Files.exists(path)) {
                System.err.println("Input file not found: " + path.toAbsolutePath());
                return List.of();
            }
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to read input file: " + e.getMessage());
            return List.of();
        }
    }

    public void writeJson(List<Operation> ops, Path path) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), ops);
        } catch (IOException e) {
            System.err.println("Failed to write JSON: " + e.getMessage());
        }
    }

    public List<Operation> readOperations(Path path) {
        try {
            if (!Files.exists(path)) {
                System.err.println("Output file not found to parse: " + path.toAbsolutePath());
                return List.of();
            }
            return mapper.readValue(path.toFile(), new TypeReference<List<Operation>>() {});
        } catch (IOException e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return List.of();
        }
    }
}
