package br.com.dio.ui;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;

    public void execute() {
        try {
            displayWelcomeMessage();
            int option;
            do {
                displayMenu();
                option = scanner.nextInt();
                processMenuOption(option);
            } while (option != 10);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void displayWelcomeMessage() {
        System.out.printf("Bem-vindo ao board %s, selecione a operação desejada\n", entity.getId());
    }

    private void displayMenu() {
        System.out.println("1 - Criar um card");
        System.out.println("2 - Mover um card");
        System.out.println("3 - Bloquear um card");
        System.out.println("4 - Desbloquear um card");
        System.out.println("5 - Cancelar um card");
        System.out.println("6 - Ver board");
        System.out.println("7 - Ver coluna com cards");
        System.out.println("8 - Ver card");
        System.out.println("9 - Voltar para o menu anterior");
        System.out.println("10 - Sair");
    }

    private void processMenuOption(int option) throws SQLException {
        switch (option) {
            case 1 -> createCard();
            case 2 -> moveCardToNextColumn();
            case 3 -> blockCard();
            case 4 -> unblockCard();
            case 5 -> cancelCard();
            case 6 -> showBoard();
            case 7 -> showColumn();
            case 8 -> showCard();
            case 9 -> System.out.println("Voltando para o menu anterior...");
            case 10 -> System.exit(0);
            default -> System.out.println("Opção inválida, tente novamente!");
        }
    }

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Informe o Assunto do card");
        card.setTitle(scanner.next());
        System.out.println("Informe as informações do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            new CardService(connection).create(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = getBoardColumnsInfo();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();
        var boardColumnsInfo = getBoardColumnsInfo();
        try (var connection = getConnection()) {
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do card que será desbloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio do card");
        var reason = scanner.next();
        try (var connection = getConnection()) {
            new CardService(connection).unblock(cardId, reason);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = getBoardColumnsInfo();
        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var board = new BoardQueryService(connection).showBoardDetails(entity.getId());
            board.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> 
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    private void showColumn() throws SQLException {
        var selectedColumnId = selectColumnFromBoard();
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s\n", 
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> displayCardDetails(c),
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId)
                    );
        }
    }

    private long selectColumnFromBoard() {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        long selectedColumnId;
        do {
            System.out.printf("Escolha uma coluna do board %s pelo id\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        } while (!columnsIds.contains(selectedColumnId));
        return selectedColumnId;
    }

    private List<BoardColumnInfoDTO> getBoardColumnsInfo() {
        return entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
    }

    private void displayCardDetails(var card) {
        System.out.printf("Card %s - %s.\n", card.id(), card.title());
        System.out.printf("Descrição: %s\n", card.description());
        System.out.println(card.blocked() ?
                "Está bloqueado. Motivo: " + card.blockReason() :
                "Não está bloqueado");
        System.out.printf("Já foi bloqueado %s vezes\n", card.blocksAmount());
        System.out.printf("Está no momento na coluna %s - %s\n", card.columnId(), card.columnName());
    }
}
