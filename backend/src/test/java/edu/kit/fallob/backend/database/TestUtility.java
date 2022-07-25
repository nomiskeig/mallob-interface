package edu.kit.fallob.backend.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class TestUtility {
    private static final String INITIAL_DATABASE_PATH = "./src/test/resources/database/fallobTestDatabase";
    private static final String DATABASE_COPY_PATH = "./src/test/resources/database/fallobTestDatabaseCopy";
    private static final String FILE_EXTENSION = ".mv.db";

    public static String createDatabaseCopy() {
        Path originalDatabase = Paths.get(INITIAL_DATABASE_PATH + FILE_EXTENSION);
        Path copiedDatabase = Paths.get(DATABASE_COPY_PATH + FILE_EXTENSION);

        try {
            Files.copy(originalDatabase, copiedDatabase, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return DATABASE_COPY_PATH;
    }

    public static void removeDatabaseCopy(String path) {
        Path databaseCopy = Paths.get(path + FILE_EXTENSION);

        try {
            Files.deleteIfExists(databaseCopy);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
