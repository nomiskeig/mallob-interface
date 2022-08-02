package edu.kit.fallob.backend.database;

import org.junit.jupiter.api.Assertions;
import org.springframework.security.core.parameters.P;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public final class TestUtility {
    private static final String JDBC_FORMAT = "jdbc:h2:%s;IFEXISTS=TRUE";
    private static final String DATABASE_USER = "fallob";
    private static final String DATABASE_PASSWORD = "";
    private static final String INITIAL_DATABASE_PATH = "./src/test/resources/database/fallobTestDatabase";
    private static final String DATABASE_COPY_PATH = "./src/test/resources/database/fallobTestDatabaseCopy";
    private static final String FILE_EXTENSION = ".mv.db";
    private static final String LOG_EXTENSION = ".trace.db";
    private static final String TXT_EXTENSION = ".txt";
    private static final String TIME_PATTERN = "HHmmssSSS";
    private static final String DESCRIPTION_DIR_PATH = "./src/test/resources/database/descriptions";
    private static final String RESULT_DIR_PATH = "./src/test/resources/database/results";
    private static final String INITIAL_TEST_FILE_PATH = "./src/test/resources/database/test";
    private static final String COPY_TEST_FILE_PATH = "./src/test/resources/database/testCopy";

    public static String createDatabaseCopy() {
        //i have no fucking idea why this is necessary but if the names of 2 copies are the same the tests don't work correctly
        //it probably happens because of the internal caching of the file
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_PATTERN);
        String time = LocalDateTime.now().format(timeFormat);

        Path originalDatabase = Paths.get(INITIAL_DATABASE_PATH + FILE_EXTENSION);
        Path copiedDatabase = Paths.get(DATABASE_COPY_PATH + time + FILE_EXTENSION);

        Assertions.assertTrue(Files.exists(originalDatabase));
        Assertions.assertFalse(Files.exists(copiedDatabase));

        try {
            Files.copy(originalDatabase, copiedDatabase);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(Files.exists(copiedDatabase));
        
        return DATABASE_COPY_PATH + time;
    }

    public static Connection getConnection(String path) {
        String jdbcPath = String.format(JDBC_FORMAT, path);
        try {
            return DriverManager.getConnection(jdbcPath, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeDatabaseCopy(String path) {
        Path databaseCopy = Paths.get(path + FILE_EXTENSION);
        Path databaseCopyLog = Paths.get(path + LOG_EXTENSION);

        Assertions.assertTrue(Files.exists(databaseCopy));

        try {
            Files.delete(databaseCopy);
            //delete log file if exists
            Files.deleteIfExists(databaseCopyLog);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertFalse(Files.exists(databaseCopy));
    }

    public static String createTestFileCopy() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_PATTERN);
        String time = LocalDateTime.now().format(timeFormat);

        Path originalFile = Paths.get(INITIAL_TEST_FILE_PATH + TXT_EXTENSION);
        Path copiedFile = Paths.get(COPY_TEST_FILE_PATH + time + TXT_EXTENSION);

        Assertions.assertTrue(Files.exists(originalFile));
        Assertions.assertFalse(Files.exists(copiedFile));

        try {
            Files.copy(originalFile, copiedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(Files.exists(copiedFile));

        return COPY_TEST_FILE_PATH + time + TXT_EXTENSION;
    }

    public static void removeTestFileCopy(String path) throws IOException {
        Path file = Paths.get(path);
        Files.deleteIfExists(file);
    }

    public static String createNewConfigurationDir() {
        new File(DESCRIPTION_DIR_PATH).mkdirs();
        return DESCRIPTION_DIR_PATH;
    }

    public static String createNewResultDir() {
        new File(RESULT_DIR_PATH).mkdirs();
        return RESULT_DIR_PATH;
    }

    public static void deleteDirs() {
        File descriptionDirectory = new File(DESCRIPTION_DIR_PATH);

        File[] allFiles = descriptionDirectory.listFiles();
        if (allFiles != null) {
            for (File file: allFiles) {
                file.delete();
            }
        }
        descriptionDirectory.delete();

        File resultDirectory = new File(RESULT_DIR_PATH);

        allFiles = resultDirectory.listFiles();
        if (allFiles != null) {
            for (File file: allFiles) {
                file.delete();
            }
        }
        resultDirectory.delete();
    }
}
