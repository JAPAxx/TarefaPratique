package com.pratique.events.model;

public enum EventCategory {
    FESTA("Festa"),
    ESPORTIVO("Esportivo"),
    SHOW("Show"),
    CULTURAL("Cultural"),
    GASTRONOMICO("Gastronomico"),
    EDUCATIVO("Educativo");

    private final String label;

    EventCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
