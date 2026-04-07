package com.pratique.events.model;

public enum EventStatus {
    HAPPENING_NOW("Acontecendo agora"),
    UPCOMING("Proximo"),
    ALREADY_HAPPENED("Ja aconteceu");

    private final String label;

    EventStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
