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
    @DisplayName("#listChores >> When chores is empty >> throw exception")
    void listChoresWhenChoreIsEmptyThrowException(){
        ChoreService service = new ChoreService();
        assertThrows(
                EmptyChoreListException.class,()->service.listChores(service)
        );
    }

    @Test
    @DisplayName("#listChores >> When chores is not empty >> print chores")
    void listChoresWhenChoreIsNotEmptyPrintChores(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        assertAll(
                ()->service.listChores(service)
        );
    }

    @Test
    @DisplayName("#editChore >> When chores is empty >> throw exception")
    void editChoresWhenChoreIsEmptyThrowException(){
        ChoreService service = new ChoreService();
        assertThrows(
                EmptyChoreListException.class,()->service.editChore(service,0,null)
        );
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty and without edit >> return the chore")
    void editChoresWhenChoreIsNotEmptyAndWithoutEditReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore("Chore #01(edited)", null, null));
        assertEquals("Chore #01(edited)",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty and edit description >> return the chore")
    void editChoresWhenChoreIsNotEmptyAndEditDescriptionReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore("Chore #01(edited)", null, null));
        assertEquals("Chore #01(edited)",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty and edit IsCompleted >> return the chore")
    void editChoresWhenChoreIsNotEmptyAndEditIsCompletedReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore(null, Boolean.TRUE, null));
        assertEquals("Chore #01",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty and edit Deadline >> return the chore")
    void editChoresWhenChoreIsNotEmptyAndEditDeadlineReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore(null, null, LocalDate.now().plusDays(1)));
        assertEquals("Chore #01",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now().plusDays(1), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty, edit Description and IsCompleted>> return the chore")
    void editChoresWhenChoreIsNotEmptyEditDescriptionAndIsCompletedReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore("Chore #01(edited)", Boolean.TRUE, null));
        assertEquals("Chore #01(edited)",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty, edit Description and Deadline>> return the chore")
    void editChoresWhenChoreIsNotEmptyEditDescriptionAndDeadLineReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore("Chore #01(edited)", null, LocalDate.now().plusDays(2)));
        assertEquals("Chore #01(edited)",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now().plusDays(2), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty, edit Deadline and IsCompleted>> return the chore")
    void editChoresWhenChoreIsNotEmptyEditIsCompletedAndDeadLineReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore(null, Boolean.TRUE, LocalDate.now().plusDays(3)));
        assertEquals("Chore #01",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now().plusDays(3), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#editChores >> When chores is not empty, edit Deadline, IsCompleted and Description >> return the chore")
    void editChoresWhenChoreIsNotEmptyEditDescriptionIsCompletedAndDeadLineReturnTheChore(){
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().isEmpty());
        service.editChore(service, 0, new Chore("Chore #01(edited)", Boolean.TRUE, LocalDate.now().plusDays(5)));
        assertEquals("Chore #01(edited)",  service.getChores().get(0).getDescription());
        assertEquals(Boolean.TRUE, service.getChores().get(0).getIsCompleted());
        assertEquals(LocalDate.now().plusDays(5), service.getChores().get(0).getDeadline());
    }
}
