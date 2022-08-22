package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.sql.Date;
import java.time.LocalDateTime;

public class EventProxy {

    //event-attributes
    private final int processID;
    private final int treeIndex;
    private final int jobID;
    private final boolean load;
    private final LocalDateTime time;



    public EventProxy(Event event) {
        this.processID = event.getProcessID();
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

    public int getProcessID() {
        return processID;
    }

    public String getTime() {
        return time.toString();
    }

}
