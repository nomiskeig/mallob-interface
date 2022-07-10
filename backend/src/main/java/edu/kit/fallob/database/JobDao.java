package edu.kit.fallob.database;

import java.time.LocalDateTime;

public interface JobDao {

    public int saveJobConfiguration(JobConfiguration configuration, String username, int mallobId);

    public int saveJobDescription(JobDescription description, String username, SubmitType type);

    public void removeAllJobsBeforeTime(LocalDateTime time);

    public int[] getAllJobIds(String username);

    public JobDescription getJobDescription(int descriptionId);

    public JobConfiguration getJobConfiguration(int jobId);

    public JobStatus getJobStatus(int jobId);

    public JobInformation getJobInformation(int jobId);

    public JobResult getJobResult(int jobId);

    public void updateJobStatus(int jobId, JobStatus status);

    public void addJobResult(int jobId, JobResult result, ResultMetaData resultMetaData);

    public int getJobIdByMallobId(int mallobId);

    public SubmitType getSubmitType(int descriptionId);

    public long getSizeOfAllJobDescriptions();
}
