package br.edu.unifalmg.service;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.enumerator.ChoreFilter;
import br.edu.unifalmg.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChoreServiceTest {

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
        ChoreService service = new ChoreService();
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
        ChoreService service = new ChoreService();
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
    @DisplayName("#ListsChores > When ListChores is not empty > print ListsChores")
    void ListsChoresWhenListChoresIsNotEmptyPrintListChores(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        assertAll( () -> service.listsChores(service));
    }

    @Test
    @DisplayName("#ListsChores > When ListChores is empty > Exception")
    void ListChoresWhenListChoresIsEmptyException(){
        ChoreService service = new ChoreService();
        assertThrows(
                EmptyChoreListException.class,() ->service.listsChores(service)
        );
    }
    @Test
    @DisplayName("#editChore > When chores is empty > exception")
    void editChoresWhenChoreIsEmptyException(){
        ChoreService service = new ChoreService();
        assertThrows(
                EmptyChoreListException.class,()->service.editChore(service,0,null)
        );
    }

    @Test
    @DisplayName("#editChores >  When chores no changed > return chore")
    void editChoresWhenChoresNoChangedReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore("Chore modified", null, null));
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals("Chore modified",  service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores > When chores edit description > return chore")
    void editChoresWhenChoreEditDescriptionReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore("Chore modified", null, null));
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals("Chore modified",  service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores > When chores edit status > return chore")
    void editChoresWhenChoreEditStatusReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore(null, Boolean.TRUE, null));
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
        assertEquals("Chore",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#editChores > When chores edit deadline > return chore")
    void editChoresWhenChoreEditDeadlineReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore(null, null, LocalDate.now().plusDays(7)));
        assertEquals("Chore",  service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now().plusDays(7), service.getChores().get(0).getDeadline());
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#editChores > When chores edit description and status > return  chore")
    void editChoresWhenChoreEditDescriptionAndStatusReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore("Chore modified", Boolean.TRUE, null));
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
        assertEquals("Chore modified",  service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores > When chores edit description and deadline > return chore")
    void editChoresWhenChoreEditDescriptionAndDeadLineReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore("Chore modified", null, LocalDate.now().plusDays(9)));
        assertEquals(LocalDate.now().plusDays(9), service.getChores().get(0).getDeadline());
        assertEquals("Chore modified",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());

    }

    @Test
    @DisplayName("#editChores > When chores edit deadline and status > return chore")
    void editChoresWhenChoreEditStatusAndDeadLineReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore(null, Boolean.TRUE, LocalDate.now().plusDays(15)));
        assertEquals("Chore",  service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now().plusDays(15), service.getChores().get(0).getDeadline());
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#editChores > When chores edit Deadline, status and description > return chore")
    void editChoresWhenChoreEditDescriptionStatusAndDeadLineReturnChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore", Boolean.FALSE, LocalDate.now()));
        service.editChore(service, 0, new Chore("Chore modified", Boolean.TRUE, LocalDate.now().plusDays(4)));
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now().plusDays(4), service.getChores().get(0).getDeadline());
        assertEquals("Chore modified",  service.getChores().get(0).getDescription());
    }

}