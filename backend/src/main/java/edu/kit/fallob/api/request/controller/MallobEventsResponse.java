package edu.kit.fallob.api.request.controller;

import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class MallobEventsResponse {

    private final List<EventProxy> events;

    /**
     * Constructor that takes a list of event proxies
     * @param events a list of event proxies to be printed
     */
    public MallobEventsResponse(List<EventProxy> events) {
        this.events = events;
    }

    /**
     * Getter
     * @return the events (event proxies)
     */
    public List<EventProxy> getEvents() {
        return events;
    }
}
