package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu {

    private final Scanner scanner;

    public MainMenu() {
        this.scanner = new Scanner(System.in).useDelimiter("\n");
    }

    public void execute() throws SQLException {
        System.out.println("Bem-vindo ao gerenciador de boards, escolha a opção desejada");
        int option = -1;
        while (true) {
            displayMainMenu();
            option = scanner.nextInt();
            handleMenuOption(option);
        }
    }

    private void displayMainMenu() {
        System.out.println("1 - Criar um novo board");
        System.out.println("2 - Selecionar um board existente");
        System.out.println("3 - Excluir um board");
        System.out.println("4 - Sair");
    }

    private void handleMenuOption(int option) throws SQLException {
        switch (option) {
            case 1 -> createBoard();
            case 2 -> selectBoard();
            case 3 -> deleteBoard();
            case 4 -> System.exit(0);
            default -> System.out.println("Opção inválida, informe uma opção do menu");
        }
    }

    private void createBoard() throws SQLException {
        BoardEntity entity = new BoardEntity();
        System.out.println("Informe o nome do seu board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0'");
        int additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();
        addColumnsToBoard(columns, additionalColumns);

        entity.setBoardColumns(columns);
        try (var connection = getConnection()) {
            new BoardService(connection).insert(entity);
        }
    }

    private void addColumnsToBoard(List<BoardColumnEntity> columns, int additionalColumns) {
        columns.add(createColumn("Informe o nome da coluna inicial do board", INITIAL, 0));

        for (int i = 0; i < additionalColumns; i++) {
            columns.add(createColumn("Informe o nome da coluna de tarefa pendente do board", PENDING, i + 1));
        }

        columns.add(createColumn("Informe o nome da coluna final", FINAL, additionalColumns + 1));
        columns.add(createColumn("Informe o nome da coluna de cancelamento do board", CANCEL, additionalColumns + 2));
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        long id = scanner.nextLong();
        try (var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            queryService.findById(id).ifPresentOrElse(
                    board -> new BoardMenu(board).execute(),
                    () -> System.out.printf("Não foi encontrado um board com id %s\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board que será excluído");
        long id = scanner.nextLong();
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("O board %s foi excluído\n", id);
            } else {
                System.out.printf("Não foi encontrado um board com id %s\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String promptMessage, final BoardColumnKindEnum kind, final int order) {
        System.out.println(promptMessage);
        String columnName = scanner.next();
        BoardColumnEntity column = new BoardColumnEntity();
        column.setName(columnName);
        column.setKind(kind);
        column.setOrder(order);
        return column;
    }
}
