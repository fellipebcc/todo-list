package br.edu.unifalmg.service;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.enumerator.ChoreFilter;
import br.edu.unifalmg.exception.*;
import br.edu.unifalmg.repository.ChoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChoreServiceTest {

    @InjectMocks
    private ChoreService service;

    @Mock
    private ChoreRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#addChore > When the description is invalid > Throw an exception")
    void addChoreWhenTheDescriptionIsInvalidThrowAnException() {
        ChoreService service = new ChoreService();
        assertAll(
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, LocalDate.now().minusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When the deadline is invalid > Throw an exception")
    void addChoreWhenTheDeadlineIsInvalidThrowAnException() {
        ChoreService service = new ChoreService();
        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", null)),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When adding a chore > When the chore already exists > Throw an exception")
    void addChoreWhenAddingAChoreWhenTheChoreAlreadyExistsThrowAnException() {
        Mockito.when(
                repository.save(new Chore("Description", Boolean.FALSE, LocalDate.now()))
        ).thenReturn(Boolean.TRUE);
        service.addChore("Description", LocalDate.now());
        assertThrows(DuplicatedChoreException.class,
                () -> service.addChore("Description", LocalDate.now()));
    }

    @Test
    @DisplayName("#addChore > When the chore's list is empty > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListIsEmptyWhenAddingANewChoreAddTheChore() {
        ChoreService service = new ChoreService();
        Chore response = service.addChore("Description", LocalDate.now());
        assertAll(
                () -> assertEquals("Description", response.getDescription()),
                () -> assertEquals(LocalDate.now(), response.getDeadline()),
                () -> assertEquals(Boolean.FALSE, response.getIsCompleted())
        );
    }

    @Test
    @DisplayName("#addChore > When the chore's list has at least one element > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListHasAtLeastOneElementWhenAddingANewChoreAddTheChore() {
        Mockito.when(
                repository.save(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()))
        ).thenReturn(Boolean.TRUE);
        Mockito.when(
                repository.save(new Chore("Chore #02", Boolean.FALSE, LocalDate.now().plusDays(2)))
        ).thenReturn(Boolean.TRUE);
        service.addChore("Chore #01", LocalDate.now());
        service.addChore("Chore #02", LocalDate.now().plusDays(2));
        assertAll(
                () -> assertEquals(2, service.getChores().size()),
                () -> assertEquals("Chore #01", service.getChores().get(0).getDescription()),
                () -> assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted()),
                () -> assertEquals("Chore #02", service.getChores().get(1).getDescription()),
                () -> assertEquals(LocalDate.now().plusDays(2), service.getChores().get(1).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(1).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#deleteChore > When the list is empty > Throw an exception")
    void deleteChoreWhenTheListIsEmptyThrowAnException() {
        ChoreService service = new ChoreService();
        assertThrows(EmptyChoreListException.class, () -> {
            service.deleteChore("Qualquer coisa", LocalDate.now());
        });
    }


    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore does not exist > Throw an exception")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreDoesNotExistThrowAnException() {
        ChoreService service = new ChoreService();
        service.addChore("Description", LocalDate.now());
        assertThrows(ChoreNotFoundException.class, () -> {
            service.deleteChore("Chore to be deleted", LocalDate.now().plusDays(5));
        });
    }

    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore exists > Delete the chore")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreExistsDeleteTheChore() {
        ChoreService service = new ChoreService();

        service.addChore("Chore #01", LocalDate.now().plusDays(1));
        assertEquals(1, service.getChores().size());

        assertDoesNotThrow(() -> service.deleteChore("Chore #01", LocalDate.now().plusDays(1)));
        assertEquals(0, service.getChores().size());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsValidToggleTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > When toggle the chore twice > Toggle chore")
    void toggleChoreWhenTheDeadlineIsValidWhenToggleTheChoreTwiceToggleTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertFalse(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the chore does not exist > Throw an exception")
    void toggleChoreWhenTheChoreDoesNotExistThrowAnException() {
        ChoreService service = new ChoreService();
        assertThrows(ChoreNotFoundException.class, () -> service.toggleChore("Chore #01", LocalDate.now()));
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is uncompleted > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusInUncompletedToggleTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());
        service.getChores().get(0).setDeadline(LocalDate.now().minusDays(1));

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When status is completed > Throw an exception")
    void toggleChoreWhenTheDeadlineIsInvalidWhenStatusIsCompletedThrowAnException() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().minusDays(1)));

        assertThrows(ToggleChoreWithInvalidDeadlineException.class, () ->
                service.toggleChore("Chore #01", LocalDate.now().minusDays(1))
        );

    }

    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is empty > Return all chores")
    void filterChoresWhenTheFilterIsAllWhenTheListIsEmptyReturnAllChores() {
        ChoreService service = new ChoreService();
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is not empty > Return all chores")
    void filterChoresWhenTheFilterIsAllWhenTheListIsNotEmptyReturnAllChores() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertAll(
            () -> assertEquals(2, response.size()),
            () -> assertEquals("Chore #01", response.get(0).getDescription()),
            () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted()),
            () -> assertEquals("Chore #02", response.get(1).getDescription()),
            () -> assertEquals(Boolean.TRUE, response.get(1).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsEmptyReturnAnEmptyList() {
        ChoreService service = new ChoreService();
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #02", response.get(0).getDescription()),
                () -> assertEquals(Boolean.TRUE, response.get(0).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsEmptyReturnAnEmptyList() {
        ChoreService service = new ChoreService();
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #01", response.get(0).getDescription()),
                () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted())
        );
    }

    @Test
<<<<<<< HEAD
    @DisplayName("#printChores > When the list is  empty > print: No chores to display")
    void printChoresWhenTheListIsEmptyPrintNoChoresToDisplay() {
        ChoreService service = new ChoreService();
        assertEquals("No chores to display\n",service.displayChores());
    }

    @Test
    @DisplayName("#printChores > When the list has one chore and it has completed status > print the information about the chore ")
    void printChoresWhenTheListHasOneChoreAndItHasCompletedStatusPrintTheInformationAboutTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Escrever teste",LocalDate.of(2023,10,29));
        service.getChores().get(0).setIsCompleted(true);
        assertEquals("Descrição: Escrever teste Deadline: 29/10/2023 Status: Completa\n",service.displayChores());
    }

    @Test
    @DisplayName("#printChores > When the list has one chore and it has uncompleted status > print the information about the chore ")
    void printChoresWhenTheListHasOneChoreAndItHasUncompletedStatusPrintTheInformationAboutTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Escrever teste",LocalDate.of(2023,10,29));
        assertEquals("Descrição: Escrever teste Deadline: 29/10/2023 Status: Incompleta\n",service.displayChores());
    }

    @Test
    @DisplayName("#printChores > When the list has more than one chore > print the information about the chores")
    void printChoresWhenTheListHasMoreThanOneChorePrintTheInformationAboutTheChores() {
        ChoreService service = new ChoreService();
        service.addChore("Escrever teste", LocalDate.of(2023, 10, 29));
        service.getChores().get(0).setIsCompleted(true);
        service.addChore("Implementar código", LocalDate.of(2023, 10, 12));
        assertEquals("Descrição: Escrever teste Deadline: 29/10/2023 Status: Completa\n" +
                              "Descrição: Implementar código Deadline: 12/10/2023 Status: Incompleta\n",
                               service.displayChores());
    }

    @Test
    @DisplayName("#changeChore > When the description is invalid > throw an exception ")
    void changeChoreWhenTheDescriptionIsInvalidThrowAnException() {
        ChoreService service = new ChoreService();
        service.addChore("Description",LocalDate.now());
        String choreDescription = service.getChores().get(0).getDescription();
        LocalDate choreDate = service.getChores().get(0).getDeadline();
        assertAll(
                ()-> assertThrows(InvalidDescriptionException.class, () -> {
                    service.changeChore(choreDescription,choreDate, null ,choreDate);
                }),
                ()-> assertThrows(InvalidDescriptionException.class, () -> {
                    service.changeChore(choreDescription,choreDate,"",choreDate);
                })
        );

    }

    @Test
    @DisplayName("#changeChore > When deadline is Invalid > throw an exception")
    void changeChoreWhenDeadlineIsInvalidThrowException() {

        ChoreService service = new ChoreService();
        service.addChore("Original Description", LocalDate.now());
        String choreDescription = service.getChores().get(0).getDescription();
        LocalDate choreDate = service.getChores().get(0).getDeadline();

        assertThrows(InvalidDeadlineException.class, () -> service.changeChore(choreDescription,choreDate,choreDescription,LocalDate.now().minusDays(5)));
    }

    @Test
    @DisplayName("#changeChore > When change only the description and it is valid > change chore description")
    void changeChoreWhenChangeOnlyTheDescriptionAndItIsValidChangeDescription() {
        ChoreService service = new ChoreService();
        String originalDescription = "Original Description";
        service.addChore(originalDescription, LocalDate.now());
        String choreDescription = service.getChores().get(0).getDescription();
        LocalDate choreDate = service.getChores().get(0).getDeadline();
        String newDescription = "New Description";
        service.changeChore(choreDescription,choreDate,newDescription,choreDate);

        assertTrue(service.getChores().get(0).getDescription().equals(newDescription));
    }

    @Test
    @DisplayName("#changeChore > When change only the deadline and it is valid > change chore deadline")
    void changeChoreWhenChangeOnlyTheDeadlineAndItIsValidChangeDescription() {
        ChoreService service = new ChoreService();
        LocalDate originalDate = LocalDate.now();

        service.addChore("Description", originalDate);

        String choreDescription = service.getChores().get(0).getDescription();
        LocalDate choreDate = service.getChores().get(0).getDeadline();

        LocalDate newDate = LocalDate.now().plusDays(3);

        service.changeChore(choreDescription,choreDate,choreDescription,newDate);

        assertEquals(newDate, service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#changeChore > When chore with given description and deadline does not exist > throw an exception")
    void changeChoreWhenChoreWithGivenDescriptionDoesNotExistThrowException() {

        ChoreService service = new ChoreService();

        service.addChore("Description", LocalDate.now().plusDays(2));
        String choreDescription = service.getChores().get(0).getDescription();
        LocalDate choreDate = service.getChores().get(0).getDeadline();
        assertThrows(ChoreNotFoundException.class, () -> {
            service.changeChore("Nonexistent Chore", LocalDate.now(),"Test",LocalDate.now().plusDays(3));
        });
    }

    @Test
    @DisplayName("#changeChore > When change the deadline and the description and it is valid > change chore deadline and description")
    void changeChoreWhenChangeTheDeadlineAndDescriptionItIsValidChangeDeadlineAndDescription() {
        ChoreService service = new ChoreService();
        LocalDate originalDate = LocalDate.now();
        service.addChore("Description", originalDate);

        LocalDate newDate = LocalDate.now().plusDays(3);
        String newDescription = "New Description";

        service.changeChore(service.getChores().get(0).getDescription(),service.getChores().get(0).getDeadline(),newDescription,newDate);
        assertAll(
                ()-> assertEquals(newDescription, service.getChores().get(0).getDescription()),
                () -> assertEquals(newDate, service.getChores().get(0).getDeadline())
=======
    @DisplayName("#loadChores > When the chores are loaded > Update the chore list")
    void loadChoresWhenTheChoresAreLoadedUpdateTheChoreList() {
        Mockito.when(repository.load()).thenReturn(new ArrayList<>() {{
            add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
            add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now().minusDays(2)));
        }});
        service.loadChores();
//        int size = service.getChores().size();
//        assertEquals(2, size);
        List<Chore> loadedChores = service.getChores();
        assertAll(
                () -> assertEquals(2, loadedChores.size()),
                () -> assertEquals("Chore #01", loadedChores.get(0).getDescription()),
                () -> assertEquals(Boolean.FALSE, loadedChores.get(0).getIsCompleted()),
                () -> assertEquals(LocalDate.now(), loadedChores.get(0).getDeadline()),
                () -> assertEquals("Chore #02", loadedChores.get(1).getDescription()),
                () -> assertEquals(Boolean.TRUE, loadedChores.get(1).getIsCompleted()),
                () -> assertEquals(LocalDate.now().minusDays(2), loadedChores.get(1).getDeadline())
>>>>>>> 8a145508ae9bb84231ccde42183ffa2aff2410b9
        );
    }

    @Test
<<<<<<< HEAD
    @DisplayName("#save > When the list is empty > return true")
    void saveWhenTheListIsEmptyReturnTrue(){
        ChoreService service = new ChoreService(repository);
        List<Chore> chores = service.getChores();
        Mockito.when(this.repository.save(chores)).thenReturn(true);
        assertTrue(service::saveChores);
    }

    @Test
    @DisplayName("#save > When the list has one chore > return true")
    void saveWhenTheListHasOneChoreReturnTrue(){
        ChoreService service = new ChoreService(repository);
        service.getChores().add(Chore.builder().description("test save").deadline(LocalDate.now().plusDays(3)).build());
        List<Chore> chores = service.getChores();
        Mockito.when(this.repository.save(chores)).thenReturn(true);
        assertTrue(service::saveChores);
    }

    @Test
    @DisplayName("#save > When the repository fails > return false")
    void saveWhenTheRepositoryFailsReturnFalse(){
        ChoreService service = new ChoreService(repository);
        List<Chore> chores = service.getChores();
        Mockito.when(this.repository.save(chores)).thenReturn(false);
        assertFalse(service::saveChores);
    }



=======
    @DisplayName("#loadChores > When no chores are loaded > Update the chore list")
    void loadChoresWhenNoChoresAreLoadedUpdateTheChoreList() {
        Mockito.when(repository.load()).thenReturn(new ArrayList<>());
        service.loadChores();
        List<Chore> loadChores = service.getChores();
        assertTrue(loadChores.isEmpty());
    }

>>>>>>> 8a145508ae9bb84231ccde42183ffa2aff2410b9
}
