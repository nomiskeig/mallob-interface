package edu.kit.fallob.backend.database;

import edu.kit.fallob.database.FileHandler;
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

public class FileHandlerTests {

    private static final String INITIAL_FILE_PATH = "src\\test\\resources\\database\\";
    private static final String SAVE_FILE_PATH = "src\\test\\resources\\";

    private static final String FILE_NAME = "test.txt";
    private static final String FILE_COPY_NAME = "testCopy.txt";

    @BeforeEach
    public void setup() throws IOException {
        Path originalPath = Paths.get(INITIAL_FILE_PATH + FILE_NAME);
        Path copyPath = Paths.get(INITIAL_FILE_PATH + FILE_COPY_NAME);
        Files.copy(originalPath, copyPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * test the correct saving of a file
     */
    @Test
    public void testSave() {
        File initialFile = new File(INITIAL_FILE_PATH + FILE_COPY_NAME);
        Assertions.assertTrue(initialFile.isFile());

        FileHandler.saveFileAtPath(initialFile, SAVE_FILE_PATH + FILE_COPY_NAME);

        File newFile = new File(SAVE_FILE_PATH + FILE_COPY_NAME);
        Assertions.assertTrue(newFile.isFile());

        Assertions.assertFalse(initialFile.isFile());
    }

    /**
     * test the correct retrieval of a file
     */
    @Test
    public void testGetFile() {
        File file = new File(INITIAL_FILE_PATH + FILE_COPY_NAME);

        File testFile = FileHandler.getFileFromPath(INITIAL_FILE_PATH + FILE_COPY_NAME);

        Assertions.assertEquals(file, testFile);
    }

    /**
     * test the correct removal of a file
     */
    @Test
    public void testRemove() {
        File file = new File(INITIAL_FILE_PATH + FILE_COPY_NAME);
        Assertions.assertTrue(file.isFile());

        FileHandler.deleteFileAtPath(INITIAL_FILE_PATH + FILE_COPY_NAME);

        Assertions.assertFalse(file.isFile());
    }
    

    @AfterEach
    public void removeFile() throws IOException {
       Files.deleteIfExists(Paths.get(INITIAL_FILE_PATH + FILE_COPY_NAME));
       Files.deleteIfExists(Paths.get(SAVE_FILE_PATH + FILE_COPY_NAME));
    }
}
