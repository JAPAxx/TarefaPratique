package com.pratique.events;

import com.pratique.events.controller.ApplicationController;
import com.pratique.events.repository.FileEventRepository;
import com.pratique.events.view.ConsoleView;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        FileEventRepository eventRepository = new FileEventRepository(Path.of("events.data"));
        ConsoleView view = new ConsoleView();

        ApplicationController controller = new ApplicationController(eventRepository, view);
        controller.start();
    }
}
