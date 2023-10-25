package br.edu.unifalmg.repository;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
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

public class FileChoreRepositoryTest {

    @InjectMocks
    private FileChoreRepository repository;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("load > When the file is empty > then return an empty list")
    void loadWhenTheFileIsEmptyThenReturnAnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(MismatchedInputException.class);

        List<Chore> response = repository.load();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("load > When the file is not found (or path is invalid) > then return an empty list")
    void loadWhenTheFileIsNotFoundThenReturnAnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(FileNotFoundException.class);

        List<Chore> response = repository.load();
        Assertions.assertTrue(response.isEmpty());
    }
    @Test
    @DisplayName("load > When the file is valid > then return a list of chores")
    void loadWhenTheFileIsValidThenReturnAListOfChores() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenReturn(new Chore[]{
                new Chore("First Chore", Boolean.FALSE, LocalDate.now()),
                new Chore("Second Chore", Boolean.TRUE, LocalDate.now().minusDays(1))
        });
        List<Chore> chores = repository.load();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, chores.size()),
                () -> Assertions.assertEquals("First Chore", chores.get(0).getDescription()),
                () -> Assertions.assertEquals(LocalDate.now(), chores.get(0).getDeadline())
        );
    }
}
