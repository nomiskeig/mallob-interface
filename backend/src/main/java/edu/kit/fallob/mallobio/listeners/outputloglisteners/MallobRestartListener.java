package edu.kit.fallob.mallobio.listeners.outputloglisteners;


import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * listener that gets triggered if mallob is restarted. If it's triggered it sets all running jobs to the cancelled status
 * and adds a load 0 event to the database
 * @author Valentin Schenk
 * @version 1.0
 */
public class MallobRestartListener implements OutputLogLineListener{

    // the line that prints the program options is printed when mallob starts and is therefor used to detect if mallob has restarted
    private static final String RESTART_REGEX = "Program options";

    private JobDao jobDao;
    private EventDao eventDao;

    /**
     * the constructor of the class
     */
    public MallobRestartListener() {
        try {
            DaoFactory daoFactory = new DaoFactory();
            this.jobDao = daoFactory.getJobDao();
            this.eventDao = daoFactory.getEventDao();
        } catch (FallobException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processLine(String line) {
        Pattern pattern = Pattern.compile(RESTART_REGEX);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            System.out.println(line);

            try {
                //get all running jobs from the system
                List<Integer> runningJobs = this.jobDao.getAllRunningJobs();

                for (Integer jobId: runningJobs) {
                    this.jobDao.updateJobStatus(jobId, JobStatus.CANCELLED);
                }

                //get the load 1 events from the jobs that were running before the mallob restart in order to add the
                //load 0 events manually to the database because the job doesn't exist anymore inside of mallob
                List<Event> events = this.eventDao.getEventsByTime(LocalDateTime.now(ZoneOffset.UTC));

                for (Event event: events) {
                    //create the new load 0 event and save it to the database
                    Event load0Event = new Event(event.getProcessID(), event.getTreeIndex(), event.getJobID(), false, LocalDateTime.now(ZoneOffset.UTC));
                    this.eventDao.save(load0Event);
                }

            } catch (FallobException e) {
                e.printStackTrace();
            }
        }
    }
}
