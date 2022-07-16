package edu.kit.fallob.database;

public class DatabaseGarbageCollector implements Runnable{
    private final int timeIntervall;

    public DatabaseGarbageCollector(int timeIntervall) {
        this.timeIntervall = timeIntervall;
    }

    @Override
    public void run() {

    }

    private void removeOldJobConfigurations() {

    }

    private void removeOldJobDescriptions() {

    }

    private void removeOldEvents() {

    }

    private void removeOldWarnings() {

    }
}
