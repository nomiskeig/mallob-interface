package edu.kit.fallob.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that is responsible for storing and retrieving files on the local filesystem
 * @author Valentin Schenk
 * @version 1.0
 */
public final class FileHandler {

    private static final double BIT_TO_MB_RATIO = 0.000000125;

    /**
     * saves the given file at the given path on the local filesystem
     * @param file the file that should be saved
     * @param path the path at which the file should get saved
     */
    public static void saveFileAtPath(File file, String path) {
        file.renameTo(new File(path));
    }

    /**
     * returns all files in the given directory whose names match the given regex pattern
     * @param directoryPath the directory where the files are saved
     * @param regex a string that contains the regex pattern that is used to find the wanted files
     * @return a list of all files whose filename matches the regex pattern
     */
    public static List<File> getFilesByRegex(String directoryPath, String regex) {
        File directory = new File(directoryPath);

        Pattern pattern = Pattern.compile(regex);

        if (directory.isDirectory()) {
            List<File> files = new ArrayList<>();
            File[] allFiles = directory.listFiles();

            for (File file : allFiles) {
                String fileName = file.getName();

                //check if the filename matches the regex pattern
                Matcher matcher = pattern.matcher(fileName);

                if (matcher.find()) {
                    files.add(file);
                }
            }
            return files;
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * deletes the files from the filesystem whose names match the given regex pattern
     * @param path the path that points to the directory where the files are stored
     * @param regex a string that contains the regex pattern that is used to determine the files that should be removed
     */
    public static void deleteFilesByRegex(String path, String regex) {
        File directory = new File(path);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            Pattern pattern = Pattern.compile(regex);

            for (File file: files) {
                String fileName = file.getName();

                //check if the filename matches the regex pattern
                Matcher matcher = pattern.matcher(fileName);

                if (matcher.find()) {
                    file.delete();
                }
            }
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * returns the combined size of all files in the given directory
     * @param path the path that points to the directory
     * @return the size of the files in megabytes
     */
    public static long getSizeOfDirectory(String path) {
        File directory = new File(path);

        if (directory.isDirectory()) {
            long size = 0;

            //iterate over all files in the directory
            for (File file: directory.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                }
            }

            return (long) (size / BIT_TO_MB_RATIO);
        } else {
            throw new RuntimeException();
        }

    }

}
