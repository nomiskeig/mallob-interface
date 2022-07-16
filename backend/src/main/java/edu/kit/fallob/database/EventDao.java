package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventDao {
    public void save(Event event);

    public void removeEventsBeforeTime(LocalDateTime time);

    public List<Event> getEventsByTime(LocalDateTime time);

    public List<Event> getEventsBetweenTime(LocalDateTime startTime, LocalDateTime endTime);

    public LocalDateTime getTimeOfFirstEvent();
}
