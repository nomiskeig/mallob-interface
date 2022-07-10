package edu.kit.fallob.database;

import java.time.LocalDateTime;

public class JobDaoImpl implements JobDao{
    @Override
    public int saveJobConfiguration(JobConfiguration configuration, String username, int mallobId) {
        return 0;
    }

    @Override
    public int saveJobDescription(JobDescription description, String username, SubmitType type) {
        return 0;
    }

    @Override
    public void removeAllJobsBeforeTime(LocalDateTime time) {

    }

    @Override
    public int[] getAllJobIds(String username) {
        return new int[0];
    }

    @Override
    public JobDescription getJobDescription(int descriptionId) {
        return null;
    }

    @Override
    public JobConfiguration getJobConfiguration(int jobId) {
        return null;
    }

    @Override
    public JobStatus getJobStatus(int jobId) {
        return null;
    }

    @Override
    public JobInformation getJobInformation(int jobId) {
        return null;
    }

    @Override
    public JobResult getJobResult(int jobId) {
        return null;
    }

    @Override
    public void updateJobStatus(int jobId, JobStatus status) {

    }

    @Override
    public void addJobResult(int jobId, JobResult result, ResultMetaData resultMetaData) {

    }

    @Override
    public int getJobIdByMallobId(int mallobId) {
        return 0;
    }

    @Override
    public SubmitType getSubmitType(int descriptionId) {
        return null;
    }

    @Override
    public long getSizeOfAllJobDescriptions() {
        return 0;
    }
}
