package edu.kit.fallob.dataobjects;

public class JobConfigurationDependencyFilter {
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return true;
        }


        Integer[] dependencies = (Integer[]) other;
        if (dependencies[0] == null) {
            return true;
        }
        return false;
    }
}
