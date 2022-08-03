package edu.kit.fallob.api.request.controller;

import java.util.List;

public class MallobEventsResponse {

    private final List<EventProxy> events;

    public MallobEventsResponse(List<EventProxy> events) {
        this.events = events;
    }

    public List<EventProxy> getEvents() {
        return events;
    }
}
