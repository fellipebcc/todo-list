package br.edu.unifalmg;

import br.edu.unifalmg.repository.ChoreRepository;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import br.edu.unifalmg.repository.impl.MysqlChoreRepository;
import br.edu.unifalmg.service.ChoreService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;

public class TodoApplication {

    public static void main(String[] args) {
        //ChoreRepository repository = new FileChoreRepository();
        ChoreRepository repository = new MysqlChoreRepository();
        ChoreService service = new ChoreService(repository);
        service.loadChores();
        //service.addChore("Testing write on file feature", LocalDate.now());
        System.out.println("Tamanho da lista de chores: " + service.getChores().size());
        //service.deleteChore("Testing write on file feature", LocalDate.now());
        //service.saveChores();
    }

}
