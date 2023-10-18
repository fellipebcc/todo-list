package br.edu.unifalmg.service;

import br.edu.unifalmg.service.ChoreService;

public class ChoreMain {
    public static void main(String[] args) {
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        ChoreService choreService = new ChoreService();
        choreService.loadChoresFromJsonFile("/home/pcmoraes/Área de Trabalho/faculdade/Disciplinas-Faculdade/disciplinas/Gestão CIclo de Vida/atividades/todo-list/todo/chores_read.json");

    }
}
