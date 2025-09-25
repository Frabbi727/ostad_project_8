package org.example;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        Path inputPath = Path.of("input.txt");
        Path outputPath = Path.of("output.json");

        FileService fileService = new FileService();
        List<Operation> successfulOps = new ArrayList<>();

        List<String> lines = fileService.readAllLines(inputPath);
        for (int i = 0; i < lines.size(); i++) {
            String raw = lines.get(i);
            try {
                Operation op = parseOperation(raw);
                double result = compute(op);
                op.setResult(result);
                successfulOps.add(op);
            } catch (IllegalArgumentException ex) {
                System.err.printf("Skipping line %d (%s): %s%n", i + 1, raw, ex.getMessage());
                // continue with next line
            } catch (Exception ex) {
                System.err.printf("Unexpected error at line %d (%s): %s%n", i + 1, raw, ex.getMessage());
            }
        }

        fileService.writeJson(successfulOps, outputPath);

        List<Operation> reloaded = fileService.readOperations(outputPath);

        for (Operation op : reloaded) {
            System.out.printf("%.1f %s %.1f = %.1f%n",
                    op.getNumOne(), op.getOperator(), op.getNumTwo(), op.getResult());
        }
    }

    private static Operation parseOperation(String line) {
        if (line == null) throw new IllegalArgumentException("Line is null");
        String trimmed = line.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("Empty line");

        String[] parts = trimmed.split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid format. Expected: <num_1> <operator> <num_2>");
        }

        double numOne;
        double numTwo;
        try {
            numOne = Double.parseDouble(parts[0]);
            numTwo = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format");
        }

        String operator = parts[1];
        if (!operator.matches("[+\\-*/]")) {
            throw new IllegalArgumentException("Unsupported operator: " + operator);
        }

        return new Operation(numOne, numTwo, operator, 0.0);
    }

    private static double compute(Operation op) {
        return switch (op.getOperator()) {
            case "+" -> op.getNumOne() + op.getNumTwo();
            case "-" -> op.getNumOne() - op.getNumTwo();
            case "*" -> op.getNumOne() * op.getNumTwo();
            case "/" -> {
                if (op.getNumTwo() == 0.0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                yield op.getNumOne() / op.getNumTwo();
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + op.getOperator());
        };
    }
}