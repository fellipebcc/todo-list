package br.edu.unifalmg.repository.impl;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.ChoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileChoreRepository implements ChoreRepository {

    private ObjectMapper mapper;

    public FileChoreRepository() {
        mapper = new ObjectMapper().findAndRegisterModules();
    }

    @Override
    public List<Chore> load() {
        try {
            return Arrays.asList(
                    mapper.readValue(new File("chores.json"), Chore[].class)
            );

            // Using TypeReference
//            return mapper.readValue(new File("chores.json"),
//                    new TypeReference<>() {
//                    });
        } catch(IOException exception) {
            System.out.println("ERROR: Unable to open file.");
            return new ArrayList<>();
        }
    }

}