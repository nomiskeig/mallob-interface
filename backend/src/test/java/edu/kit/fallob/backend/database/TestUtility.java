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
    private static final String TIME_PATTERN = "HHmmssSSS";

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
}
