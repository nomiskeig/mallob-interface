package edu.kit.fallob.database;

/**
 * class that is responsible for creating new DAO objects and returning them
 * @author Valentin Schenk
 * @version 1.0
 */
import edu.kit.fallob.springConfig.FallobException;

public class DaoFactory {

    private final UserDao userDao;
    private final JobDao jobDao;
    private final EventDao eventDao;
    private final WarningDao warningDao;

    /**
     * constructor for the class
     * creates the dao objects
     */
    public DaoFactory() throws FallobException {
        this.userDao = new UserDaoImpl();
        this.jobDao = new JobDaoImpl();
        this.eventDao = new EventDaoImpl();
        this.warningDao = new WarningDaoImpl();
    }

    /**
     * getter for the UserDao class
     * @return the new UserDao instance
     */
    public UserDao getUserDao() {
        return this.userDao;
    }

    /**
     * getter for the JobDao class
     * @return the new JobDao instance
     */
    public JobDao getJobDao() {
        return this.jobDao;
    }

    /**
     * getter for the EventDao class
     * @return the new EventDao instance
     */
    public EventDao getEventDao() {
        return this.eventDao;
    }

    /**
     * getter for the WarningDao class
     * @return the new WarningDao instance
     */
    public WarningDao getWarningDao() {
        return this.warningDao;
    }
}
