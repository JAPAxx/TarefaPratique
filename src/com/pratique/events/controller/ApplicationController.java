package com.pratique.events.controller;

import com.pratique.events.model.Event;
import com.pratique.events.model.EventCategory;
import com.pratique.events.model.EventStatus;
import com.pratique.events.model.User;
import com.pratique.events.repository.FileEventRepository;
import com.pratique.events.view.ConsoleView;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApplicationController {
    private final FileEventRepository eventRepository;
    private final ConsoleView view;
    private final List<Event> events;
    private User currentUser;
    private int nextEventId;

    public ApplicationController(FileEventRepository eventRepository, ConsoleView view) {
        this.eventRepository = eventRepository;
        this.view = view;
        this.events = eventRepository.loadEvents();
        this.nextEventId = calculateNextEventId();
    }

    public void start() {
        view.showWelcome(events.size());
        view.showMessage("Vamos cadastrar o usuario da sessao.");
        registerOrUpdateUser();

        boolean running = true;
        while (running) {
            int option = view.showMainMenu(currentUser, events.size());

            try {
                switch (option) {
                    case 1:
                        registerOrUpdateUser();
                        break;
                    case 2:
                        createEvent();
                        break;
                    case 3:
                        listAllEvents();
                        break;
                    case 4:
                        confirmParticipation();
                        break;
                    case 5:
                        listConfirmedEvents();
                        break;
                    case 6:
                        cancelParticipation();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        view.showError("Opcao invalida. Tente novamente.");
                        break;
                }
            } catch (IllegalStateException | IllegalArgumentException exception) {
                view.showError(exception.getMessage());
            }
        }

        view.showMessage("Programa encerrado. Ate a proxima.");
    }

    private void registerOrUpdateUser() {
        if (currentUser != null) {
            view.showUserProfile(currentUser);
            view.showMessage("Atualize os dados do usuario.");
        }

        String name = view.promptRequiredText("Nome");
        String email = view.promptRequiredText("Email");
        String city = view.promptRequiredText("Cidade");
        int age = view.promptInt("Idade", 1);

        if (currentUser == null) {
            currentUser = new User(name, email, city, age);
        } else {
            currentUser.updateProfile(name, email, city, age);
        }

        view.showMessage("Usuario salvo com sucesso.");
    }

    private void createEvent() {
        String name = view.promptRequiredText("Nome do evento");
        String address = view.promptRequiredText("Endereco");
        EventCategory category = view.promptCategory();
        LocalDateTime startTime = view.promptDateTime("Horario do evento");
        int durationMinutes = view.promptInt("Duracao em minutos", 1);
        String description = view.promptRequiredText("Descricao");

        Event event = new Event(nextEventId, name, address, category, startTime, durationMinutes, description);
        nextEventId++;
        events.add(event);
        saveEvents();
        view.showMessage("Evento cadastrado com sucesso.");
    }

    private void listAllEvents() {
        List<Event> orderedEvents = getOrderedEvents(new ArrayList<>(events));

        if (orderedEvents.isEmpty()) {
            view.showMessage("Ainda nao ha eventos cadastrados.");
            return;
        }

        view.displayEvents(orderedEvents, LocalDateTime.now(), currentUser, true);
    }

    private void confirmParticipation() {
        List<Event> availableEvents = getAvailableEvents();

        if (availableEvents.isEmpty()) {
            view.showMessage("Nao ha eventos disponiveis para confirmar participacao.");
            return;
        }

        view.displayEvents(availableEvents, LocalDateTime.now(), currentUser, true);
        int selection = view.promptSelection("Escolha o numero do evento para participar (0 cancela)", availableEvents.size());

        if (selection == 0) {
            view.showMessage("Operacao cancelada.");
            return;
        }

        Event selectedEvent = availableEvents.get(selection - 1);
        if (selectedEvent.hasOccurred(LocalDateTime.now())) {
            throw new IllegalStateException("Nao e possivel participar de um evento que ja aconteceu.");
        }

        currentUser.confirmEvent(selectedEvent.getId());
        view.showMessage("Participacao confirmada com sucesso.");
    }

    private void listConfirmedEvents() {
        List<Event> confirmedEvents = getConfirmedEvents();

        if (confirmedEvents.isEmpty()) {
            view.showMessage("Voce ainda nao confirmou presenca em nenhum evento.");
            return;
        }

        view.displayEvents(confirmedEvents, LocalDateTime.now(), currentUser, true);
    }

    private void cancelParticipation() {
        List<Event> cancelableEvents = getCancelableEvents();

        if (cancelableEvents.isEmpty()) {
            view.showMessage("Nao ha participacoes futuras ou em andamento para cancelar.");
            return;
        }

        view.displayEvents(cancelableEvents, LocalDateTime.now(), currentUser, true);
        int selection = view.promptSelection("Escolha o numero do evento para cancelar (0 cancela)", cancelableEvents.size());

        if (selection == 0) {
            view.showMessage("Operacao cancelada.");
            return;
        }

        Event selectedEvent = cancelableEvents.get(selection - 1);
        currentUser.cancelEvent(selectedEvent.getId());
        view.showMessage("Participacao cancelada com sucesso.");
    }

    private List<Event> getOrderedEvents(List<Event> source) {
        LocalDateTime now = LocalDateTime.now();

        source.sort(Comparator
                .comparingInt((Event event) -> getStatusOrder(event.getStatus(now)))
                .thenComparing(Event::getStartTime));

        return source;
    }

    private List<Event> getAvailableEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> availableEvents = new ArrayList<>();

        for (Event event : events) {
            if (!event.hasOccurred(now) && !currentUser.participatesIn(event.getId())) {
                availableEvents.add(event);
            }
        }

        return getOrderedEvents(availableEvents);
    }

    private List<Event> getConfirmedEvents() {
        List<Event> confirmedEvents = new ArrayList<>();

        for (Event event : events) {
            if (currentUser.participatesIn(event.getId())) {
                confirmedEvents.add(event);
            }
        }

        return getOrderedEvents(confirmedEvents);
    }

    private List<Event> getCancelableEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> cancelableEvents = new ArrayList<>();

        for (Event event : events) {
            if (currentUser.participatesIn(event.getId()) && !event.hasOccurred(now)) {
                cancelableEvents.add(event);
            }
        }

        return getOrderedEvents(cancelableEvents);
    }

    private int calculateNextEventId() {
        int highestId = 0;

        for (Event event : events) {
            if (event.getId() > highestId) {
                highestId = event.getId();
            }
        }

        return highestId + 1;
    }

    private void saveEvents() {
        eventRepository.saveEvents(events);
    }

    private int getStatusOrder(EventStatus status) {
        switch (status) {
            case HAPPENING_NOW:
                return 0;
            case UPCOMING:
                return 1;
            default:
                return 2;
        }
    }
}
