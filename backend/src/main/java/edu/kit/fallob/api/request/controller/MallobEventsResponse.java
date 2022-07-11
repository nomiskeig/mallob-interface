package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.util.List;

public class MallobEventsResponse {

    private List<Event> events;

    public MallobEventsResponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
