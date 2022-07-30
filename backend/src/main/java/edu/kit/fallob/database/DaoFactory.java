package edu.kit.fallob.database;

import edu.kit.fallob.springConfig.FallobException;

public class DaoFactory {

    private final UserDao userDao;
    private final JobDao jobDao;
    private final EventDao eventDao;
    private final WarningDao warningDao;

    public DaoFactory() throws FallobException {
        this.userDao = new UserDaoImpl();
        this.jobDao = new JobDaoImpl();
        this.eventDao = new EventDaoImpl();
        this.warningDao = new WarningDaoImpl();
    }

    public UserDao getUserDao() {
        return null;
    }

    public JobDao getJobDao() {
        return null;
    }

    public EventDao getEventDao() {
        return null;
    }

    public WarningDao getWarningDao() {
        return null;
    }
}
