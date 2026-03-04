package com.waterreminder;

import com.waterreminder.service.ReminderService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Hauptklasse der AquaBuddy-App.
 * Das Hauptfenster kann geschlossen werden – die App läuft
 * im System-Tray weiter und sendet weiterhin Erinnerungen.
 */
public class WaterReminderApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // App läuft weiter wenn alle Fenster geschlossen sind
        Platform.setImplicitExit(false);

        // System-Tray aufbauen
        setupTray();

        // Hauptfenster laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 420, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        stage.setTitle("AquaBuddy");
        stage.setScene(scene);
        stage.setMinWidth(380);
        stage.setMinHeight(600);
        stage.setResizable(true);

        // Fenster schließen → nur verstecken, nicht beenden
        stage.setOnCloseRequest(e -> {
            e.consume();
            stage.hide();
        });

        stage.show();

        // Hintergrund-Reminder starten (Standard 30 Min)
        ReminderService.getInstance().start(30 * 60);
    }

    @Override
    public void stop() {
        ReminderService.getInstance().shutdown();
    }

    // ──────────────────────────────────────────
    //  SYSTEM TRAY
    // ──────────────────────────────────────────

    private void setupTray() {
        if (!SystemTray.isSupported()) return;

        // Kleines 16x16 Wasser-Icon per Code zeichnen
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new java.awt.Color(59, 130, 246)); // #3b82f6
        g.fillOval(2, 2, 12, 12);
        g.setColor(new java.awt.Color(255, 255, 255, 180));
        g.fillOval(5, 4, 4, 3);
        g.dispose();

        TrayIcon trayIcon = new TrayIcon(img, "AquaBuddy – Trink Wasser!");
        trayIcon.setImageAutoSize(true);

        // Rechtsklick-Menü
        PopupMenu menu = new PopupMenu();

        MenuItem openItem = new MenuItem("Öffnen");
        openItem.addActionListener(e -> Platform.runLater(this::showMainWindow));

        MenuItem quitItem = new MenuItem("Beenden");
        quitItem.addActionListener(e -> {
            ReminderService.getInstance().shutdown();
            Platform.exit();
            SystemTray.getSystemTray().remove(trayIcon);
        });

        menu.add(openItem);
        menu.addSeparator();
        menu.add(quitItem);

        trayIcon.setPopupMenu(menu);
        // Doppelklick → Fenster öffnen
        trayIcon.addActionListener(e -> Platform.runLater(this::showMainWindow));

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    private void showMainWindow() {
        if (primaryStage != null) {
            primaryStage.show();
            primaryStage.toFront();
        }
    }
}
