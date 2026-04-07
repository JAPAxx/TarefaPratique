package com.pratique.events.model;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private final String name;
    private final String address;
    private final EventCategory category;
    private final LocalDateTime startTime;
    private final int durationMinutes;
    private final String description;

    public Event(
            int id,
            String name,
            String address,
            EventCategory category,
            LocalDateTime startTime,
            int durationMinutes,
            String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.category = category;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public EventCategory getCategory() {
        return category;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    public boolean isHappeningAt(LocalDateTime reference) {
        return !reference.isBefore(startTime) && reference.isBefore(getEndTime());
    }

    public boolean hasOccurred(LocalDateTime reference) {
        return !reference.isBefore(getEndTime());
    }

    public boolean isUpcoming(LocalDateTime reference) {
        return reference.isBefore(startTime);
    }

    public EventStatus getStatus(LocalDateTime reference) {
        if (isHappeningAt(reference)) {
            return EventStatus.HAPPENING_NOW;
        }

        if (hasOccurred(reference)) {
            return EventStatus.ALREADY_HAPPENED;
        }

        return EventStatus.UPCOMING;
    }
}
