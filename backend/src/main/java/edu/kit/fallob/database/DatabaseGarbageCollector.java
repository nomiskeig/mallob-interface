package edu.kit.fallob.database;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.springConfig.FallobException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * class that is responsible for removing the old entries from the database and the filesysten
 * it runs in a separate thread and is executed periodically
 * @author Valentin Schenk
 * @version 1.0
 */
public class DatabaseGarbageCollector implements Runnable{
    //the ratio to convert minutes into milliseconds
    private static final int MINUTES_MILLISECONDS_RATIO = 60000;

    private final int timeIntervall;
    private final FallobConfiguration configuration;

    private JobDao jobDao;
    private EventDao eventDao;
    private WarningDao warningDao;

    /**
     * the constructor of the class
     * @param timeIntervall the time intervall in minutes, in which the garbage collector is executed
     */
    public DatabaseGarbageCollector(int timeIntervall) {
        this.timeIntervall = timeIntervall;
        this.configuration = FallobConfiguration.getInstance();
        try {
            DaoFactory daoFactory = new DaoFactory();
            this.jobDao = daoFactory.getJobDao();
            this.eventDao = daoFactory.getEventDao();
            this.warningDao = daoFactory.getWarningDao();

            //remove all entries from the database except the users
            LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);

            this.jobDao.removeAllJobsBeforeTime(currentTime);
            this.jobDao.removeAllJobDescriptions();
            this.eventDao.removeEventsBeforeTime(currentTime);
            this.warningDao.removeAllWarningsBeforeTime(currentTime);
        } catch (FallobException e) {
            e.printStackTrace();
            new RuntimeException(e);
        }
    }

    /**
     * the method that executes the garbage collection mechanism removes the old entries
     * after each execution the thread sleeps for the configured time and then it gets executed again
     */
    @Override
    public void run() {
        while (true) {
            //remove the old entries
            this.removeOldJobConfigurations();
            this.removeOldJobDescriptions();
            this.removeOldEvents();
            this.removeOldWarnings();

            //convert the configured time interval into milliseconds
            long milliseconds = (long) this.timeIntervall * MINUTES_MILLISECONDS_RATIO;

            //let the thread sleep for the configured amount of time
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * removes the old job-configurations and other job data from the database
     */
    private void removeOldJobConfigurations() {
        long storageTime = this.configuration.getJobStorageTime();
        LocalDateTime deletionTime = LocalDateTime.now(ZoneOffset.UTC).minusHours(storageTime);

        try {
            this.jobDao.removeAllJobsBeforeTime(deletionTime);
        } catch (FallobException e) {
            e.printStackTrace();
        }
    }

    /**
     * removes the old job-descriptions from the filesystem and the database
     */
    private void removeOldJobDescriptions() {
        long maxSize = this.configuration.getMaxDescriptionStorageSize();

        //keep deleting the oldest job description until the total size is smaller than the configured maximum
        while (jobDao.getSizeOfAllJobDescriptions() >= maxSize) {
            try {
                this.jobDao.removeOldestJobDescription();
            } catch (FallobException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * removes the old events from the database
     */
    private void removeOldEvents() {
        long storageTime = this.configuration.getEventStorageTime();
        LocalDateTime deletionTime = LocalDateTime.now(ZoneOffset.UTC).minusHours(storageTime);

        try {
            this.eventDao.removeEventsBeforeTime(deletionTime);

            //update the staring time in the configuration
            LocalDateTime timeFirstEvent = eventDao.getTimeOfFirstEvent();

            if (timeFirstEvent != null) {
                this.configuration.setStartTime(timeFirstEvent);
            }
        } catch (FallobException e) {
            e.printStackTrace();
        }
    }

    /**
     * removes the old warnings from the database
     */
    private void removeOldWarnings() {
        long storageTime = this.configuration.getWarningStorageTime();
        LocalDateTime deletionTime = LocalDateTime.now(ZoneOffset.UTC).minusHours(storageTime);

        try {
            this.warningDao.removeAllWarningsBeforeTime(deletionTime);
        } catch (FallobException e) {
            e.printStackTrace();
        }
    }
}
