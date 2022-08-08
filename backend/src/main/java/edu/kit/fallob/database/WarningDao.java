package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;

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
     * @throws FallobException if an error occurs while accessing the database
     */
    public void save(Warning warning) throws FallobException;

    /**
     * removes all warnings that were stored before the give point in time
     * @param time the point in time that defines which warnings get removed
     * @throws FallobException if an error occurs while accessing the database
     */
    public void removeAllWarningsBeforeTime(LocalDateTime time) throws FallobException;

    /**
     * returns all warnings that are currently stored
     * @return a list with all warnings
     * @throws FallobException if an error occurs while accessing the database
     */
    public List<Warning> getAllWarnings() throws FallobException;
}
