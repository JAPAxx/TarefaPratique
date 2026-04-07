package com.pratique.events.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class User {
    private String name;
    private String email;
    private String city;
    private int age;
    private final Set<Integer> confirmedEventIds;

    public User(String name, String email, String city, int age) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.age = age;
        this.confirmedEventIds = new LinkedHashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public int getAge() {
        return age;
    }

    public Set<Integer> getConfirmedEventIds() {
        return Collections.unmodifiableSet(confirmedEventIds);
    }

    public void updateProfile(String name, String email, String city, int age) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.age = age;
    }

    public boolean participatesIn(int eventId) {
        return confirmedEventIds.contains(eventId);
    }

    public void confirmEvent(int eventId) {
        confirmedEventIds.add(eventId);
    }

    public void cancelEvent(int eventId) {
        confirmedEventIds.remove(eventId);
    }
}
