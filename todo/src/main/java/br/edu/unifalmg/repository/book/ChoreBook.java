package br.edu.unifalmg.repository.book;

public class ChoreBook {

    public static final String FIND_ALL_CHORES = "SELECT * FROM todo_list.chore";

    public static final String INSERT_CHORE = "INSERT INTO todo_list.chore (`description`, `isCompleted`, `deadline`) VALUES (?,?,?)";

    public static final String UPDATE_CHORE = "UPDATE todo_list.chore SET description=?,deadline=? WHERE id=?";

}
