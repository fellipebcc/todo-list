package br.edu.unifalmg.repository.book;

public class ChoreBook {

    public static final String FIND_ALL_CHORES = "SELECT * FROM db2019108034.Chore";
    public static final String FIND_CHORE_BY_DESCRIPTION = "SELECT * FROM db2019108034.Chore WHERE description = ?";
    public static final String INSERT_CHORE = "INSERT INTO db2019108034.Chore (description, deadline, isCompleted) VALUES (?, ?, ?)";
    public static final String UPDATE_CHORE = "UPDATE db2019108034.Chore SET isCompleted = ? WHERE description = ? AND deadline = ?";
    public static final String DELETE_CHORE = "DELETE FROM db2019108034.Chore WHERE description = ? AND deadline = ?";
    public static final String DELETE_ALL_CHORES = "DELETE FROM db2019108034.Chore";


}
