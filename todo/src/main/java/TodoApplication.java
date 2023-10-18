import br.edu.unifalmg.repository.impl.FileChoreRepository;
import br.edu.unifalmg.service.ChoreService;

import java.io.IOException;
import java.sql.SQLOutput;

public class TodoApplication {



    public static void main(String[] args) throws IOException {
        ChoreService service = new ChoreService(new FileChoreRepository());
        service.loadChores();
        System.out.println("Tamanho da lista de chores:  " + service.getChores().size());
    }
}
