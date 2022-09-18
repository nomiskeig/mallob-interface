package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.fallob.configuration.FallobConfiguration;
import org.springframework.http.HttpStatus;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.mallobio.input.MallobInput;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.mallobio.output.MallobOutputWatcherManager;
import edu.kit.fallob.springConfig.FallobException;

/**
 * This class provides the functionality to submit a Job in Mallob.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
public class JobToMallobSubmitter implements OutputLogLineListener {

    private final static int JOB_IS_SUBMITTING = 0;
    private final static int JOB_IS_VALID = 1;
    private final static int JOB_IS_NOT_VALID = 2;
    private final static String JOB_ID_REGEX = "I Mapping job \"%s.*\" to internal ID #[0-9]+";
    private final static String VALID_JOB_REGEX = "Introducing job #[0-9]+";
    private final static String NOT_VALID_JOB_REGEX = "\\[WARN\\] Rejecting submission %s.* - reason:";

    private String username;
    private int jobID;
    private MallobInput mallobInput;
    private Object monitor;
    private int jobStatus = JOB_IS_SUBMITTING;
    private Pattern validJobPattern;
    private Pattern notValidJobPattern;
    private Pattern jobIdPattern;
    private String errorMessage;
    private boolean hasMapping = false;

    public JobToMallobSubmitter(String username) {
        this.username = username;
        this.mallobInput = MallobInputImplementation.getInstance();
        this.monitor = new Object();

        String formattedNotValidJobPattern = String.format(NOT_VALID_JOB_REGEX, username);
        String formattedJobIdPattern = String.format(JOB_ID_REGEX, username);
        validJobPattern = Pattern.compile(VALID_JOB_REGEX);
        notValidJobPattern = Pattern.compile(formattedNotValidJobPattern);
        jobIdPattern = Pattern.compile(formattedJobIdPattern);
    }

    public int submitJobToMallob(JobConfiguration jobConfiguration, JobDescription jobDescription)
            throws IOException, FallobException {
        // copy over the descriptions
        String mallobFolder = FallobConfiguration.getInstance().getMallobBasePath();
        List<File> descriptions = jobDescription.getDescriptionFiles();
        File descriptionDir = new File(mallobFolder + "/descriptions");
        if (!descriptionDir.exists()) {
            descriptionDir.mkdir();
        }
        for (File description : descriptions) {
            Files.copy(description.toPath(),
                    Path.of(mallobFolder + "/descriptions/" + description.getName()));
        }
        int clientProcessID;
        try {
            clientProcessID = mallobInput.submitJobToMallob(username, jobConfiguration, jobDescription);
        } catch (IllegalArgumentException e) {
            // remove the descriptions again
            for (File description : descriptions) {
                File fileToDelete = new File(mallobFolder + "/descriptions/" + description.getName());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
            throw new FallobException(HttpStatus.BAD_REQUEST, e.getMessage());

        }

        synchronized (monitor) {
            while (jobStatus == JOB_IS_SUBMITTING || !hasMapping) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        // remove the descriptions again
        for (File description : descriptions) {
            File fileToDelete = new File(mallobFolder + "/descriptions/" + description.getName());
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
        if (jobStatus == JOB_IS_NOT_VALID) {
            throw new FallobException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        // job is valid and result can be detected
        MallobOutputWatcherManager watcherManager = MallobOutputWatcherManager.getInstance();
        watcherManager.addNewWatcher(username, jobConfiguration.getName(), clientProcessID);

        return jobID;

    }

    @Override
    public void processLine(String line) {
        Matcher jobIdMatcher = jobIdPattern.matcher(line);
        if (jobIdMatcher.find()) {
            jobID = Integer.parseInt(line.substring(line.indexOf('#') + 1));
            hasMapping = true;
            synchronized (monitor) {
                monitor.notify();
            }
        }
        Matcher validJobMatcher = validJobPattern.matcher(line);
        if (validJobMatcher.find()) {
            jobStatus = JOB_IS_VALID;
            synchronized (monitor) {
                monitor.notify();
            }
        }
        Matcher notValidJobMatcher = notValidJobPattern.matcher(line);
        if (notValidJobMatcher.find()) {
            jobStatus = JOB_IS_NOT_VALID;
            errorMessage = line.substring(line.indexOf("reason") + 8);
            synchronized (monitor) {
                monitor.notify();
            }
        }

    }

}
