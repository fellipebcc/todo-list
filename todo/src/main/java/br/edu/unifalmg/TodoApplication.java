package br.edu.unifalmg;

import br.edu.unifalmg.repository.ChoreRepository;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import br.edu.unifalmg.service.ChoreService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;

public class TodoApplication {

    public static void main(String[] args) {
        ChoreRepository repository = new FileChoreRepository();
        ChoreService service = new ChoreService(repository);
        service.loadChores();
        System.out.println("Tamanho da lista de chores: " + service.getChores().size());
        //service.saveChores();
    }

}

