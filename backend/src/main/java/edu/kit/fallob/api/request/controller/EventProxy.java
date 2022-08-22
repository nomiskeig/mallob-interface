package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EventProxy {
    //the return format for the time
    private static final String TIME_FORMAT = "yyyy-mm-dd'T'HH:mm:ss.SSSX";

    //event-attributes
    private final int rank;
    private final int treeIndex;
    private final int jobID;
    private final boolean load;
    private final LocalDateTime time;



    public EventProxy(Event event) {
        this.rank = event.getProcessID();
        this.treeIndex = event.getTreeIndex();
        this.jobID = event.getJobID();
        this.load = event.isLoad();
        this.time = event.getTime();
    }


    //-----------------------------------------getter

    public boolean isLoad() {
        return load;
    }

    public int getJobID() {
        return jobID;
    }

    public int getTreeIndex() {
        return treeIndex;
    }

    public int getRank() {
        return rank;
    }

    public String getTime() {
        //format the time to the right format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        ZonedDateTime timeWithZone = this.time.atZone(ZoneOffset.UTC);
        return timeWithZone.format(formatter);
    }

}
