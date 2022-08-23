package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

/**
 * the event listener for the event stream
 * it implements the OutputLogLineListener interface and gets registered as listener for the mallob log lines
 */
public class EventStream implements OutputLogLineListener {

    //output format for the time
    private static final String TIME_FORMAT = "yyyy-mm-dd'T'HH:mm:ss.SSSX";
    private static final String RANK_KEY = "rank";
    private static final String TREE_INDEX_KEY = "treeIndex";
    private static final String TIME_KEY = "time";
    private static final String JOB_ID_KEY = "jobID";
    private static final String LOAD_KEY = "load";

    private final ResponseBodyEmitter emitter;
    private final JobDao jobDao;
    private final Queue<Event> bufferedEvents;

    /**
     * constructor of the class
     * @param emitter the ResponseBodyEmitter iver which the data is continuously given back to the user
     * @param jobDao a jobDao that is necessary to convert the mallob id into a regular jobId
     */
    public EventStream(ResponseBodyEmitter emitter, JobDao jobDao) {
        this.emitter = emitter;
        this.jobDao = jobDao;
        this.bufferedEvents = new LinkedList<>();
    }

    /**
     * processes a new log line from mallob
     * the method gets called every time mallob writes a new log line
     * @param line the log line that should be processed
     */
    @Override
    public void processLine(String line) {
        System.out.println("got new event");
        if (Event.isEvent(line)) {
            Event event = new Event(line);

            this.tryToSendEvent(event);
        }

        this.tryToSendBufferedEvents();
    }

    private void tryToSendEvent(Event event) {
        if (!sendEvent(event)) {
            this.bufferEvent(event);
        }
    }

    private boolean sendEvent(Event event) {
        //try to convert the mallob id into the job id
        int mallobId = event.getJobID();
        int jobId = 0;
        try {
            jobId = this.jobDao.getJobIdByMallobId(mallobId);
        } catch (FallobException e) {
            return false;
        }

        //convert the load boolean into an integer for the json object
        int loadInt = event.isLoad() ? 1 : 0;

        //convert the time into the right format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        ZonedDateTime timeWithZone = event.getTime().atZone(ZoneOffset.UTC);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RANK_KEY, event.getProcessID());
        jsonObject.put(TREE_INDEX_KEY, event.getTreeIndex());
        jsonObject.put(TIME_KEY, timeWithZone.format(formatter));
        jsonObject.put(JOB_ID_KEY, jobId);
        jsonObject.put(LOAD_KEY, loadInt);

        try {
            this.emitter.send(jsonObject.toString() + "\n", MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            this.emitter.complete();
            MallobOutput mallobOutput = MallobOutput.getInstance();
            mallobOutput.removeOutputLogLineListener(this);
        }

        return true;
    }

    private void bufferEvent(Event event) {
        try {
            this.bufferedEvents.add(event);
        } catch(IllegalStateException e) {
            System.out.println("Event could not be added to buffering-queue : capacity overflow.");
        }
    }

    private void tryToSendBufferedEvents() {
        if (this.bufferedEvents.size() == 0) {
            return;
        }

        int maxTries = this.bufferedEvents.size();
        Event event = this.bufferedEvents.poll();

        while(event != null && maxTries > 0) {
            this.tryToSendEvent(event);
            maxTries--;
            event = this.bufferedEvents.poll();
        }
    }
}

