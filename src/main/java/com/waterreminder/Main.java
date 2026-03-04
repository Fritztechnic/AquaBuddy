package com.waterreminder;

import javafx.application.Application;

/**
 * Launcher-Klasse (wird benötigt damit JavaFX ohne module-info korrekt startet)
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(WaterReminderApp.class, args);
    }
}
