package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventDaoImpl implements EventDao{
    @Override
    public void save(Event event) {

    }

    @Override
    public void removeEventsBeforeTime(LocalDateTime time) {

    }

    @Override
    public List<Event> getEventsByTime(LocalDateTime time) {
        return null;
    }

    @Override
    public List<Event> getEventsBetweenTime(LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }

    @Override
    public LocalDateTime getTimeOfFirstEvent() {
        return null;
    }
}
