package com.pratique.events.repository;

import com.pratique.events.model.Event;
import com.pratique.events.model.EventCategory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileEventRepository {
    private final Path filePath;

    public FileEventRepository(Path filePath) {
        this.filePath = filePath;
    }

    public List<Event> loadEvents() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<Event> events = new ArrayList<>();
            int fallbackId = 1;
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }

                Event event = parseLine(line, fallbackId);
                events.add(event);

                if (event.getId() >= fallbackId) {
                    fallbackId = event.getId() + 1;
                }
            }
            return events;
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel ler " + filePath.getFileName(), exception);
        }
    }

    public void saveEvents(List<Event> events) {
        List<String> lines = new ArrayList<>();
        for (Event event : events) {
            lines.add(serialize(event));
        }

        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel salvar " + filePath.getFileName(), exception);
        }
    }

    private Event parseLine(String line, int fallbackId) {
        if (line.contains(";")) {
            return parseSimpleFormat(line);
        }

        return parseOldFormat(line, fallbackId);
    }

    private Event parseSimpleFormat(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != 7) {
            throw new IllegalStateException("Linha invalida em " + filePath.getFileName() + ": " + line);
        }

        return new Event(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                EventCategory.valueOf(parts[3]),
                LocalDateTime.parse(parts[4]),
                Integer.parseInt(parts[5]),
                parts[6]);
    }

    private Event parseOldFormat(String line, int fallbackId) {
        String[] parts = line.split("\t", -1);
        if (parts.length != 7) {
            throw new IllegalStateException("Linha invalida em " + filePath.getFileName() + ": " + line);
        }

        return new Event(
                fallbackId,
                parts[1],
                parts[2],
                EventCategory.valueOf(parts[3]),
                LocalDateTime.parse(parts[4]),
                Integer.parseInt(parts[5]),
                parts[6]);
    }

    private String serialize(Event event) {
        return event.getId()
                + ";"
                + cleanText(event.getName())
                + ";"
                + cleanText(event.getAddress())
                + ";"
                + event.getCategory().name()
                + ";"
                + event.getStartTime()
                + ";"
                + event.getDurationMinutes()
                + ";"
                + cleanText(event.getDescription());
    }

    private String cleanText(String text) {
        return text.replace(";", ",");
    }
}
