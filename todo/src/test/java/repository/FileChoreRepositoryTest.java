package repository;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import br.edu.unifalmg.service.ChoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileChoreRepositoryTest {

    @InjectMocks
    private FileChoreRepository repository;

    @Mock
    private ObjectMapper mapper;

    public FileChoreRepositoryTest() {
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#save > When the file is not found (or path is invalid) > return false ")
    void saveWhenTheFileIsNotFoundOrPathIsInvalidThrowAnException() throws IOException{
        ChoreService service = new ChoreService(repository);
        List<Chore> chores = service.getChores();
        Mockito.doThrow(IOException.class).when(mapper).writeValue(new File("todo/src/main/resources/chores.json"), chores);
        assertFalse(()->repository.save(chores));
    }

    @Test
    @DisplayName("#save > When the file is found > return true")
    void saveWhenTheFileIsFoundReturnTrue() throws IOException{
        ChoreService service = new ChoreService(repository);
        List<Chore> chores = service.getChores();
        Mockito.doNothing().when(mapper).writeValue(new File("todo/src/main/resources/chores.json"), chores);
        assertTrue(()->repository.save(chores));
    }
}
