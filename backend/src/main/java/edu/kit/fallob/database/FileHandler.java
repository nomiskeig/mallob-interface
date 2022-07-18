package edu.kit.fallob.database;

import java.io.File;

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
     * returns the file that is stored at the given path on the local filesystem
     * @param path the path from where the file should be retrieved
     * @return the file that was requested or null if the file couldn't be found
     */
    public static File getFileFromPath(String path) {
        File file = new File(path);

        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    /**
     * deletes the file that is stored at the given path from the local filesystem
     * @param path the path that points to the file
     * @return true if the process was successful and false if it wasn't
     */
    public static boolean deleteFileAtPath(String path) {
        File file = new File(path);
        return file.delete();
    }

    /**
     * returns the combined size of all files in the given directory
     * @param path the path that points to the directory
     * @return the size of the files in megabytes or -1 if the path couldn't be found
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
            return -1;
        }

    }

}
