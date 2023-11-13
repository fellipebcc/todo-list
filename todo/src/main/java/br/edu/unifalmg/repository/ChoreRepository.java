package br.edu.unifalmg.repository;

import br.edu.unifalmg.domain.Chore;

import java.util.List;

public interface ChoreRepository {

    List<Chore> load();

<<<<<<< HEAD
    boolean save(List<Chore> chores);
=======
    boolean saveAll(List<Chore> chores);

    boolean save(Chore chore);
>>>>>>> 8a145508ae9bb84231ccde42183ffa2aff2410b9

}
