package br.edu.unifalmg.repository.impl;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.exception.InvalidDeadlineException;
import br.edu.unifalmg.exception.InvalidDescriptionException;
import br.edu.unifalmg.repository.ChoreRepository;
import br.edu.unifalmg.repository.book.ChoreBook;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySQLChoreRepository implements ChoreRepository {

    // Estabelecer uma conexão com o banco de dados
    private Connection connection;

    // Realizar operações no banco de dados
    // Executam em tempo de COMPILAÇÃO
    // Quando a consulta é executada uma única vez
    private Statement statement;
    // Realizar operações no banco de dados
    // Executar queries dinâmicas em tempo de execução
    // Ideal para quando vamos executar a query múltiplas vezes
    // Possui melhor perfomance quando comparado ao statement
    private PreparedStatement preparedStatement;
    // Utilizado para capturar o retorno de uma consulta
    // e.g.: Utilizei o statement para fazer uma consulta de todas as chores.
    // Eu uso o result set para capturar o resultado desta consulta
    private ResultSet resultSet;

    @Override
    public List<Chore> load() {
        if (!connectToMySQL()) {
            return new ArrayList<>();
        }

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(ChoreBook.FIND_ALL_CHORES);

            List<Chore> chores = new ArrayList<>();
            while(resultSet.next()) {
                // Poderíamos ter criado a Chore usando o construtor completo
                // OU poderíamos ter usado o construtor padrão + ter dado sets
                Chore chore = Chore.builder()
                        .description(resultSet.getString("description"))
                        .isCompleted(resultSet.getBoolean("isCompleted"))
                        .deadline(resultSet.getDate("deadline").toLocalDate())
                        .id(resultSet.getLong("id"))
                        .build();
                chores.add(chore);
            }
            return chores;
        } catch (SQLException exception) {
            System.out.println("Error when connecting to database.");
        } finally {
            closeConnections();
        }
        return null;
    }

    @Override
    public boolean saveAll(List<Chore> chores) {
        return false;
    }

    @Override
    public boolean save(Chore chore) {
        if (!connectToMySQL()) {
            return Boolean.FALSE;
        }
        try {
            preparedStatement = connection.prepareStatement(
                    ChoreBook.INSERT_CHORE);
            preparedStatement.setString(1, chore.getDescription());
            preparedStatement.setBoolean(2, chore.getIsCompleted());
            preparedStatement.setDate(3, Date.valueOf(chore.getDeadline()));
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (SQLException exception) {
            System.out.println("Error when inserting a new chore on database");
        } finally {
            closeConnections();
        }
        return false;
    }

    @Override
    public boolean update(Chore chore) {
        if (!connectToMySQL()) {
            return Boolean.FALSE;
        }
        if(Objects.isNull(chore.getDescription()) || chore.getDescription().isEmpty()){
            throw new InvalidDescriptionException("Descrição Inválida");
        }
        if(Objects.isNull(chore.getDeadline()) || chore.getDeadline().isBefore(LocalDate.now())){
            throw new InvalidDeadlineException("Deadline Inválida");
        }
        try {
            preparedStatement = connection.prepareStatement(
                    ChoreBook.UPDATE_CHORE);
            preparedStatement.setString(1, chore.getDescription());
            preparedStatement.setDate(2, Date.valueOf(chore.getDeadline()));
            preparedStatement.setLong(3,chore.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return Boolean.TRUE;
            }
        } catch (SQLException exception) {
            System.out.println("Error when updating a chore on database");
        } finally {
            closeConnections();
        }

        return false;
    }

    private boolean connectToMySQL() {
        Dotenv dotenv = Dotenv.configure().load();
         String userName = dotenv.get("USER_NAME");
         String password = dotenv.get("PASSWORD");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/todo_list?"
                            + "user="+ userName +"&password="+password);
            return Boolean.TRUE;
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println("Error when connecting to database. Try again later");
        }
        return Boolean.FALSE;
    }

    private void closeConnections() {
        try {
            if (Objects.nonNull(connection) && !connection.isClosed()) {
                connection.close();
            }
            if (Objects.nonNull(statement) && !statement.isClosed()) {
                statement.close();
            }
            if (Objects.nonNull(preparedStatement) && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
            if (Objects.nonNull(resultSet) && !resultSet.isClosed()) {
                resultSet.close();
            }
        } catch (SQLException exception) {
            System.out.println("Error when closing database connections.");
        }
    }

}
