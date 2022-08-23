package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.Buffer;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.BufferFunction;
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
public class EventStream implements OutputLogLineListener, BufferFunction<Event> {

    //output format for the time
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final String RANK_KEY = "rank";
    private static final String TREE_INDEX_KEY = "treeIndex";
    private static final String TIME_KEY = "time";
    private static final String JOB_ID_KEY = "jobID";
    private static final String LOAD_KEY = "load";

    private final ResponseBodyEmitter emitter;
    private final JobDao jobDao;
    private final Buffer<Event> bufferedEvents;

    /**
     * constructor of the class
     * @param emitter the ResponseBodyEmitter iver which the data is continuously given back to the user
     * @param jobDao a jobDao that is necessary to convert the mallob id into a regular jobId
     */
    public EventStream(ResponseBodyEmitter emitter, JobDao jobDao) {
        this.emitter = emitter;
        this.jobDao = jobDao;
        this.bufferedEvents = new Buffer<>(this);
    }

    /**
     * processes a new log line from mallob
     * the method gets called every time mallob writes a new log line
     * @param line the log line that should be processed
     */
    @Override
    public void processLine(String line) {
        if (Event.isEvent(line)) {
            Event event = new Event(line);

            this.bufferedEvents.tryToExecuteBufferFunciton(event);
        }

        //retry to send the buffered events
        this.bufferedEvents.retryBufferedFunction();
    }

    private void sendEvent(Event event, int jobId) {
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
    }

    @Override
    public boolean bufferFunction(Event outputUpdate) {
        int mallobId = outputUpdate.getMallobJobID();
        int jobId = 0;
        try {
            jobId = this.jobDao.getJobIdByMallobId(mallobId);
        } catch (FallobException e) {
            System.out.println("An sql error occurred while accessing the database");
        }

        if (jobId > 0) {
            this.sendEvent(outputUpdate, jobId);
            return true;
        }
        return false;
    }
}

