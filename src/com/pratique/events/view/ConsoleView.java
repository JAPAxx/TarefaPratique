package com.pratique.events.view;

import com.pratique.events.model.Event;
import com.pratique.events.model.EventCategory;
import com.pratique.events.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome(int eventCount) {
        System.out.println("==============================================");
        System.out.println(" Sistema de Eventos da Cidade");
        System.out.println("==============================================");
        System.out.println("Eventos carregados do arquivo: " + eventCount);
        System.out.println();
    }

    public int showMainMenu(User user, int eventCount) {
        System.out.println();
        System.out.println("Usuario atual: " + user.getName() + " | Cidade: " + user.getCity());
        System.out.println("Eventos cadastrados: " + eventCount);
        System.out.println();
        System.out.println("1 - Cadastrar ou atualizar usuario");
        System.out.println("2 - Cadastrar evento");
        System.out.println("3 - Listar todos os eventos");
        System.out.println("4 - Confirmar participacao em evento");
        System.out.println("5 - Ver eventos com presenca confirmada");
        System.out.println("6 - Cancelar participacao");
        System.out.println("0 - Sair");
        return promptSelection("Escolha uma opcao", 0, 6);
    }

    public void showUserProfile(User user) {
        System.out.println();
        System.out.println("Perfil atual");
        System.out.println("Nome: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Cidade: " + user.getCity());
        System.out.println("Idade: " + user.getAge());
        System.out.println("Participacoes confirmadas: " + user.getConfirmedEventIds().size());
        System.out.println();
    }

    public void displayEvents(List<Event> events, LocalDateTime reference, User user, boolean showIndex) {
        System.out.println();
        System.out.println("Lista de eventos");
        System.out.println("----------------------------------------------");

        for (int index = 0; index < events.size(); index++) {
            Event event = events.get(index);
            String prefix = showIndex ? "[" + (index + 1) + "] " : "";
            String participation = user != null && user.participatesIn(event.getId())
                    ? "Presenca confirmada"
                    : "Sem confirmacao";

            System.out.println(prefix + event.getName() + " | " + event.getCategory().getLabel());
            System.out.println("Horario: " + event.getStartTime().format(OUTPUT_FORMATTER)
                    + " | Duracao: " + event.getDurationMinutes() + " min");
            System.out.println("Status: " + event.getStatus(reference).getLabel() + " | " + participation);
            System.out.println("Endereco: " + event.getAddress());
            System.out.println("Descricao: " + event.getDescription());
            System.out.println("----------------------------------------------");
        }
    }

    public String promptRequiredText(String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            showError("Campo obrigatorio. Tente novamente.");
        }
    }

    public int promptInt(String label, int minimumValue) {
        while (true) {
            System.out.print(label + ": ");
            String value = scanner.nextLine().trim();
            try {
                int parsedValue = Integer.parseInt(value);
                if (parsedValue >= minimumValue) {
                    return parsedValue;
                }
            } catch (NumberFormatException ignored) {
            }
            showError("Digite um numero inteiro maior ou igual a " + minimumValue + ".");
        }
    }

    public EventCategory promptCategory() {
        EventCategory[] categories = EventCategory.values();
        System.out.println("Categorias disponiveis:");
        for (int index = 0; index < categories.length; index++) {
            System.out.println((index + 1) + " - " + categories[index].getLabel());
        }

        int selectedOption = promptSelection("Escolha a categoria", 1, categories.length);
        return categories[selectedOption - 1];
    }

    public LocalDateTime promptDateTime(String label) {
        while (true) {
            System.out.print(label + " (dd/MM/yyyy HH:mm): ");
            String value = scanner.nextLine().trim();
            try {
                return LocalDateTime.parse(value, INPUT_FORMATTER);
            } catch (DateTimeParseException exception) {
                showError("Data invalida. Use o formato dd/MM/yyyy HH:mm.");
            }
        }
    }

    public int promptSelection(String label, int maxOption) {
        return promptSelection(label, 0, maxOption);
    }

    public int promptSelection(String label, int minOption, int maxOption) {
        while (true) {
            System.out.print(label + ": ");
            String value = scanner.nextLine().trim();
            try {
                int option = Integer.parseInt(value);
                if (option >= minOption && option <= maxOption) {
                    return option;
                }
            } catch (NumberFormatException ignored) {
            }
            showError("Escolha um numero entre " + minOption + " e " + maxOption + ".");
        }
    }

    public void showMessage(String message) {
        System.out.println();
        System.out.println(message);
        System.out.println();
    }

    public void showError(String message) {
        System.out.println("Erro: " + message);
    }
}
