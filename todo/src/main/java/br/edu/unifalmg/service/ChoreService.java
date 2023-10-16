package br.edu.unifalmg.service;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.enumerator.ChoreFilter;
import br.edu.unifalmg.exception.*;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.channels.FileLockInterruptionException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChoreService {

    private List<Chore> chores;

    public ChoreService() {
        chores = new ArrayList<>();
    }

    /**
     * Method to add a new chore
     *
     * @param description The description of the chore
     * @param deadline The deadline to fulfill the chore
     * @return Chore The new (and uncompleted) chore
     * @throws InvalidDescriptionException When the description is null or empty
     * @throws InvalidDeadlineException When the deadline is null or empty
     * @throws DuplicatedChoreException When the given chore already exists
     */
    public Chore addChore(String description, LocalDate deadline) {
        verifyChore(description,deadline);
        for (Chore chore : chores) {
            if (chore.getDescription().equals(description)
                    && chore.getDeadline().isEqual(deadline)) {
                throw new DuplicatedChoreException("The given chore already exists.");
            }
        }

//         Using anyMatch solution
//
//        boolean doesTheChoreExist = chores.stream().anyMatch(chore -> chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline));
//        if (doesTheChoreExist) {
//            throw new DuplicatedChoreException("The given chore already exists.");
//        }

        // Using Constructor with all arguments
        Chore chore = new Chore(description, Boolean.FALSE, deadline);


//         Using Lombok's builder
//
//         Chore chore = Chore.builder()
//                .description(description)
//                .deadline(deadline)
//                .isCompleted(false)
//                .build();

//         Using Getter and Setters
//
//         Chore chore = new Chore();
//         chore.setDescription(description);
//         chore.setDeadline(deadline);
//         chore.setIsCompleted(Boolean.FALSE);


        chores.add(chore);
        return chore;
    }

    /**
     * Get the added chores.
     *
     * @return List<Chore> The chores added until now.
     */
    public List<Chore> getChores() {
        return this.chores;
    }

    /**
     * Method to delete a given chore.
     *
     * @param description The description of the chore
     * @param deadline The deadline of the chore
     */
    public void deleteChore(String description, LocalDate deadline) {
        if (isChoreListEmpty.test(this.chores)) {
            throw new EmptyChoreListException("Unable to remove a chore from an empty list");
        }
        boolean isChoreExist = this.chores.stream().anyMatch((chore -> chore.getDescription().equals(description)
            && chore.getDeadline().isEqual(deadline)));
        if (!isChoreExist) {
            throw new ChoreNotFoundException("The given chore does not exist.");
        }

        this.chores = this.chores.stream().filter(chore -> !chore.getDescription().equals(description)
                && !chore.getDeadline().isEqual(deadline)).collect(Collectors.toList());
    }

    /**
     *
     * Method to toggle a chore from completed to uncompleted and vice-versa.
     *
     * @param description The chore's description
     * @param deadline The deadline to complete the chore
     * @throws ChoreNotFoundException When the chore is not found on the list
     */
    public void toggleChore(String description, LocalDate deadline) {
        boolean isChoreExist = this.chores.stream().anyMatch((chore) -> chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline));
        if (!isChoreExist) {
            throw new ChoreNotFoundException("Chore not found. Impossible to toggle!");
        }

        this.chores = this.chores.stream().map(chore -> {
            if (!chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline)) {
                return chore;
            }
            if (chore.getDeadline().isBefore(LocalDate.now())
                    && chore.getIsCompleted()) {
                throw new ToggleChoreWithInvalidDeadlineException("Unable to toggle a completed chore with a past deadline");
            }
            chore.setIsCompleted(!chore.getIsCompleted());
            return chore;
        }).collect(Collectors.toList());
    }

    public List<Chore> filterChores(ChoreFilter filter) {
        switch (filter) {
            case COMPLETED:
                return this.chores.stream().filter(Chore::getIsCompleted).collect(Collectors.toList());
            case UNCOMPLETED:
                return this.chores.stream().filter(chore -> !chore.getIsCompleted()).collect(Collectors.toList());
            case ALL:
            default:
                return this.chores;
        }
    }

    public String displayChores(){
        if(isChoreListEmpty.test(chores)){
            System.out.println("No chores to display");
            return "No chores to display\n";
        }
        System.out.println(this.toString());
        return this.toString();
    }

    public void changeChore(String description, LocalDate deadline, String changedDescription, LocalDate changedDeadline){
        verifyChore(changedDescription,changedDeadline);
        boolean isChoreExist = this.chores.stream().anyMatch((chore) -> chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline));
        if (!isChoreExist) {
            throw new ChoreNotFoundException("Chore not found. Impossible to toggle!");
        }
        this.chores = this.chores.stream().map(chore -> {
            if (!chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline)) {
                return chore;
            }
            chore.setDescription(changedDescription);
            chore.setDeadline(changedDeadline);
            return chore;
        }).collect(Collectors.toList());

    }

    private final Predicate<List<Chore>> isChoreListEmpty = List::isEmpty;

    private void verifyChore(String description, LocalDate deadline){
        if (Objects.isNull(description) || description.isEmpty())
            throw new InvalidDescriptionException("The description cannot be null or empty");

        if (Objects.isNull(deadline) || deadline.isBefore(LocalDate.now()))
            throw new InvalidDeadlineException("The deadline cannot be null or before the current date");
    }

    public void loadChores() throws Exception{
        File json = new File("todo/src/main/resources/chores.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        chores = mapper.readValue(json, new TypeReference<ArrayList<Chore>>(){});
    }

    @Override
    public String toString(){
        StringBuilder choresInformation = new StringBuilder();
        this.chores.forEach(chore-> choresInformation.append(chore.toString()).append("\n"));
        return choresInformation.toString();
    }

}
