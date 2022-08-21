package edu.kit.fallob.dataobjects;

public class JobConfigurationIntFilter {
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return true;
        }

        int value = (Integer) other;
        if (value == (- Integer.MAX_VALUE)) {
            return true;
        }
        return false;
    }
}
