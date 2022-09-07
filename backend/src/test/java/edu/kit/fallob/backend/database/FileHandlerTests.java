package edu.kit.fallob.backend.database;

import edu.kit.fallob.database.FileHandler;
import edu.kit.fallob.springConfig.FallobException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FileHandlerTests {

    private static final String FILE_REGEX = "%s\\..*";
    private static final String INITIAL_FILE_PATH = "./src/test/resources/database/";
    private static final String SAVE_FILE_PATH = "./src/test/resources/";

    private static final String FILE_NAME = "test";
    private static final String FILE_COPY_NAME = "testCopy";
    private static final String FILE_EXTENSION = ".txt";

    @BeforeEach
    public void setup() throws IOException {
        Path originalPath = Paths.get(INITIAL_FILE_PATH + FILE_NAME + FILE_EXTENSION);
        Path copyPath = Paths.get(INITIAL_FILE_PATH + FILE_COPY_NAME + FILE_EXTENSION);
        Files.copy(originalPath, copyPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * test the correct saving of a file
     */
    @Test
    public void testSave() throws FallobException{
        File initialFile = new File(INITIAL_FILE_PATH + FILE_COPY_NAME + FILE_EXTENSION);
        Assertions.assertTrue(initialFile.isFile());

        FileHandler.saveFileAtPath(initialFile, SAVE_FILE_PATH + FILE_COPY_NAME + FILE_EXTENSION);

        File newFile = new File(SAVE_FILE_PATH + FILE_COPY_NAME + FILE_EXTENSION);
        Assertions.assertTrue(newFile.isFile());

        Assertions.assertFalse(initialFile.isFile());
    }

    /**
     * test the correct retrieval of a file
     */
    @Test
    public void testGetFile() {
        File file = new File(INITIAL_FILE_PATH + FILE_COPY_NAME + FILE_EXTENSION);

        String regex = String.format(FILE_REGEX, FILE_COPY_NAME);

        List<File> testFiles = FileHandler.getFilesByRegex(INITIAL_FILE_PATH, regex);

        Assertions.assertEquals(testFiles.size(), 1);
        Assertions.assertEquals(file, testFiles.get(0));
    }

    /**
     * test the correct removal of a file
     */
    @Test
    public void testRemove() {
        File file = new File(INITIAL_FILE_PATH + FILE_COPY_NAME +FILE_EXTENSION);
        Assertions.assertTrue(file.isFile());

        String regex = String.format(FILE_REGEX, FILE_COPY_NAME);

        FileHandler.deleteFilesByRegex(INITIAL_FILE_PATH, regex);

        Assertions.assertFalse(file.isFile());
    }


    @AfterEach
    public void removeFile() throws IOException {
       Files.deleteIfExists(Paths.get(INITIAL_FILE_PATH + FILE_COPY_NAME + FILE_EXTENSION));
       Files.deleteIfExists(Paths.get(SAVE_FILE_PATH + FILE_COPY_NAME + FILE_EXTENSION));
    }
}
