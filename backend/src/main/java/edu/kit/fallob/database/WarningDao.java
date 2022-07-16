package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Warning;

import java.time.LocalDateTime;
import java.util.List;

public interface WarningDao {

    public void save(Warning warning);

    public void removeAllWarningsBeforeTime(LocalDateTime time);

    public List<Warning> getAllWarnings();
}
