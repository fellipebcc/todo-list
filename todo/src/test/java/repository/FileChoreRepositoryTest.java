package repository;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.impl.FileChoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class FileChoreRepositoryTest {

    @InjectMocks
    private FileChoreRepository repository;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#load > When the file is found > When the content is empty > Return empty list")
    void loadWhenTheFileIsFoundWhenTheContentIsEmptyReturnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(MismatchedInputException.class);

        List<Chore> response = repository.load();
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is not found (or path is invalid) > Return an empty list")
    void loadWhenTheFileIsNotFoundOrPathIsInvalidReturnAnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(FileNotFoundException.class);

        List<Chore> response = repository.load();
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is loaded > Return a chores' list")
    void loadWhenTheFileIsLoadedReturnAChoresList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenReturn(new Chore[] {
                new Chore("First Chore", Boolean.FALSE, LocalDate.now()),
                new Chore("Second Chore", Boolean.TRUE, LocalDate.now().minusDays(5))
        });

        List<Chore> chores = repository.load();
        assertAll(
                () -> assertEquals(2, chores.size()),
                () -> assertEquals("First Chore", chores.get(0).getDescription()),
                () -> assertEquals(LocalDate.now().minusDays(5), chores.get(1).getDeadline())
        );
    }

    private FileChoreRepository choreRepository;
    private Path testFilePath;

    @BeforeEach
    void setUp() {
        // Use a temporary file for testing
        testFilePath = Path.of("test-chore-data.json");
        choreRepository = new FileChoreRepository(testFilePath);
    }

    @Test
    void testSaveAndLoad() {
        // Arrange
        List<Chore> choresToSave = new ArrayList<>();
        choresToSave.add(new Chore("Chore 1", false, LocalDate.now()));
        choresToSave.add(new Chore("Chore 2", true, LocalDate.now().plusDays(1)));

        // Act: Save chores to the repository
        boolean saveResult = choreRepository.save(choresToSave);
        List<Chore> loadedChores = choreRepository.load();

        // Assert: Check if saving and loading worked correctly
        assertTrue(saveResult);
        assertNotNull(loadedChores);
        assertEquals(2, loadedChores.size());
        assertEquals("Chore 1", loadedChores.get(0).getDescription());
        assertEquals("Chore 2", loadedChores.get(1).getDescription());
    }

    @Test
    void testLoadEmptyFile() {
        // Arrange: Create an empty file
        try {
            File emptyFile = testFilePath.toFile();
            emptyFile.createNewFile();

            // Act: Load data from the empty file
            List<Chore> loadedChores = choreRepository.load();

            // Assert: Check if an empty list is returned
            assertNotNull(loadedChores);
            assertTrue(loadedChores.isEmpty());
        } catch (IOException e) {
            fail("Failed to create an empty file for testing.");
        }
    }

    @Test
    void testSaveFileError() {
        // Arrange: Mock an IOException when saving
        FileChoreRepository mockRepository = mock(FileChoreRepository.class);
        doThrow(IOException.class).when(mockRepository).save(anyList());

        // Act: Try to save with a mocked repository
        boolean saveResult = mockRepository.save(new ArrayList<>());

        // Assert: Check if an error occurred during saving
        assertFalse(saveResult);
    }

    @Test
    void testLoadFileError() {
        // Arrange: Mock an IOException when loading
        FileChoreRepository mockRepository = mock(FileChoreRepository.class);
        doThrow(IOException.class).when(mockRepository).load();

        // Act: Try to load with a mocked repository
        List<Chore> loadedChores = mockRepository.load();

        // Assert: Check if an error occurred during loading (empty list returned)
        assertNotNull(loadedChores);
        assertTrue(loadedChores.isEmpty());
    }

    @Test
    void testInvalidFilePath() {
        // Arrange: Use an invalid file path
        Path invalidFilePath = Path.of("invalid_path_that_does_not_exist.json");
        FileChoreRepository invalidRepository = new FileChoreRepository(invalidFilePath);

        // Act: Try to load and save using the invalid file path
        List<Chore> loadedChores = invalidRepository.load();
        boolean saveResult = invalidRepository.save(new ArrayList<>());

        // Assert: Check if loading and saving with an invalid file path didn't cause exceptions
        assertNotNull(loadedChores);
        assertTrue(loadedChores.isEmpty());
        assertTrue(saveResult);
    }

    @AfterEach
    void cleanUp() {
        // Delete the test file after testing
        File testFile = testFilePath.toFile();
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}