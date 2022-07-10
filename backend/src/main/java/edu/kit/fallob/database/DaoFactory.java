package edu.kit.fallob.database;

public class DaoFactory {

    private final UserDao userDao;
    private final JobDao jobDao;
    private final EventDao eventDao;
    private final WarningDao warningDao;

    public DaoFactory() {
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
