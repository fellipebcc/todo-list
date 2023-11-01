package br.edu.unifalmg.repository;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import br.edu.unifalmg.service.ChoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Assertions;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

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
                new Chore("Second Chore", TRUE, LocalDate.now().minusDays(5))
        });

        List<Chore> chores = repository.load();
        assertAll(
                () -> assertEquals(2, chores.size()),
                () -> assertEquals("First Chore", chores.get(0).getDescription()),
                () -> assertEquals(LocalDate.now().minusDays(5), chores.get(1).getDeadline())
        );
    }

    @Test
    @DisplayName("#save > When saving a valid file > Should return true")
    void saveWhenSavingAValidFileShouldReturnTrue() throws IOException {
        // Create a list of chores
        List<Chore> chores = new ArrayList<>();
        chores.add(new Chore("Chore 1", true, LocalDate.now()));
        chores.add(new Chore("Chore 2", false, LocalDate.now().plusDays(2)));

        // Mock the ObjectMapper's writeValue method to simulate a successful save
        Mockito.doNothing().when(mapper).writeValue(new File("chores.json"), chores);

        // Call the save method and assert that it returns true
        boolean result = repository.save(chores);
        assertTrue(result);
    }

    @Test
    @DisplayName("#save > When saving an invalid file > Should return false")
    void saveWhenSavingAnInvalidFileShouldReturnFalse() throws IOException {
        // Cria uma lista de chores para ser salva
        List<Chore> chores = new ArrayList<>();
        chores.add(new Chore("Chore 1", true, LocalDate.now()));
        chores.add(new Chore("Chore 2", false, LocalDate.now().plusDays(2)));

        // Mock o método writeValue do ObjectMapper para simular um save mal sucedido
        Mockito.doThrow(IOException.class).when(mapper).writeValue(new File("chores.json"), chores);

        // Chama o método save e verifica se ele retorna false
        boolean result = repository.save(chores);
        assertFalse(result);
    }

    @Test
    @DisplayName("#save > When saving a null file > Should return false")
    void saveWhenSavingANullFileShouldReturnFalse() throws IOException {
        // Mock o método writeValue do ObjectMapper para simular um save mal sucedido
        Mockito.doThrow(IOException.class).when(mapper).writeValue(new File("chores.json"), null);

        // Chama o método save e verifica se ele retorna false
        boolean result = repository.save(null);
        assertFalse(result);
    }

    @Test
    @DisplayName("#save > When saving an empty file > Should return false")
    void saveWhenSavingAnEmptyFileShouldReturnFalse() throws IOException {
        // Cria uma lista de chores vazia para ser salva
        List<Chore> chores = new ArrayList<>();

        // Mock o método writeValue do ObjectMapper para simular um save mal sucedido
        Mockito.doThrow(IOException.class).when(mapper).writeValue(new File("chores.json"), chores);

        // Chama o método save e verifica se ele retorna false
        boolean result = repository.save(chores);
        assertFalse(result);
    }

    @Test
    @DisplayName("#save > When saving a file with a null chore > Should return false")
    void saveWhenSavingAFileWithANullChoreShouldReturnFalse() throws IOException {
        // Cria uma lista de chores para ser salva
        List<Chore> chores = new ArrayList<>();
        chores.add(null);

        // Mock o método writeValue do ObjectMapper para simular um save mal sucedido
        Mockito.doThrow(IOException.class).when(mapper).writeValue(new File("chores.json"), chores);

        // Chama o método save e verifica se ele retorna false
        boolean result = repository.save(chores);
        assertFalse(result);
    }
}
