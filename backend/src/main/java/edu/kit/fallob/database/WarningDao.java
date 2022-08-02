package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Warning;

import java.time.LocalDateTime;
import java.util.List;

/**
 * interface that represent a data access object for the Warning class
 * it is responsible for saving the warnings from Mallob persistently
 * @author Valentin Schenk
 * @version 1.0
 */
public interface WarningDao {

    /**
     * saves the given warning persistently
     * @param warning the warning object that contains the data that should be saved
     */
    public void save(Warning warning);

    /**
     * removes all warnings that were stored before the give point in time
     * @param time the point in time that defines which warnings get removed
     */
    public void removeAllWarningsBeforeTime(LocalDateTime time);

    /**
     * returns all warnings that are currently stored
     * @return a list with all warnings
     */
    public List<Warning> getAllWarnings();
}
