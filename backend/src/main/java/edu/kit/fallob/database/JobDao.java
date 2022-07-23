package edu.kit.fallob.database;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.dataobjects.ResultMetaData;
import edu.kit.fallob.dataobjects.SubmitType;

import java.time.LocalDateTime;
import java.util.List;

public interface JobDao {

    public int saveJobConfiguration(JobConfiguration configuration, String username, int mallobId);

    public int saveJobDescription(JobDescription description, String username);

    public void removeOldestJobDescription();

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

    public int getMallobIdByJobId(int jobId);

    public long getSizeOfAllJobDescriptions();

    public List<Integer> getAllRunningJobs();
}
