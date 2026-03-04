package com.waterreminder.controller;

import com.waterreminder.model.WaterTracker;
import com.waterreminder.service.ReminderService;
import com.waterreminder.util.FunMessages;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller für die Hauptansicht.
 * Erinnerungs-Timer läuft im ReminderService (Hintergrund-Thread).
 */
public class MainController implements Initializable {

    // === FXML-Elemente ===
    @FXML private Label waterDropEmoji;
    @FXML private Label glassCountLabel;
    @FXML private Label goalLabel;
    @FXML private Label totalMlLabel;
    @FXML private Label messageLabel;
    @FXML private Label timerLabel;
    @FXML private Label streakLabel;
    @FXML private Label lastDrinkLabel;
    @FXML private Label factLabel;
    @FXML private Label progressPercentLabel;
    @FXML private Label greetingLabel;

    @FXML private ProgressBar progressBar;
    @FXML private Button drinkButton;
    @FXML private Button undoButton;
    @FXML private Slider intervalSlider;
    @FXML private Label intervalValueLabel;
    @FXML private Slider goalSlider;
    @FXML private Label goalValueLabel;

    @FXML private VBox mainContainer;
    @FXML private VBox celebrationOverlay;
    @FXML private StackPane rootPane;

    private final WaterTracker tracker = new WaterTracker();
    private final ReminderService reminderService = ReminderService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBindings();
        setupSliders();
        setupAnimations();
        setupReminderService();
        showRandomFact();
        messageLabel.setText(FunMessages.getRandomReminder());
        updateGreeting();
    }

    // ──────────────────────────────────────────
    //  BINDINGS
    // ──────────────────────────────────────────

    private void setupBindings() {
        glassCountLabel.textProperty().bind(
            Bindings.format("%d", tracker.glassesConsumedProperty())
        );
        goalLabel.textProperty().bind(
            Bindings.format("/ %d Gläser", tracker.dailyGoalProperty())
        );
        tracker.glassesConsumedProperty().addListener((obs, o, n) -> {
            totalMlLabel.setText(String.format("%.1f L getrunken", tracker.getTotalLiters()));
            progressPercentLabel.setText(String.format("%.0f%%", tracker.getProgress() * 100));
        });
        progressBar.progressProperty().bind(tracker.progressProperty());
        streakLabel.textProperty().bind(
            Bindings.format("%d Tage Streak", tracker.streakDaysProperty())
        );
        lastDrinkLabel.textProperty().bind(
            Bindings.format("Zuletzt: %s", tracker.lastDrinkTimeProperty())
        );
        undoButton.disableProperty().bind(tracker.glassesConsumedProperty().isEqualTo(0));
        tracker.goalReachedProperty().addListener((obs, o, reached) -> {
            if (reached) drinkButton.getStyleClass().add("drink-button-completed");
            else         drinkButton.getStyleClass().remove("drink-button-completed");
        });
    }

    // ──────────────────────────────────────────
    //  REMINDER SERVICE
    // ──────────────────────────────────────────

    private void setupReminderService() {
        reminderService.setOnTick(() -> {
            int s = reminderService.getSecondsUntilNext();
            timerLabel.setText(String.format("Nächste Erinnerung: %02d:%02d", s / 60, s % 60));
        });
        reminderService.setOnReminder(() -> {
            messageLabel.setText(FunMessages.getRandomReminder());
            animateReminderAlert();
        });
        // Popup-Button "Hab getrunken!" → Counter erhöhen
        reminderService.setOnDrink(() -> {
            boolean goalJustReached = tracker.drinkGlass();
            if (goalJustReached) {
                messageLabel.setText(FunMessages.getRandomGoalReachedMessage());
                showCelebration();
            } else {
                messageLabel.setText(FunMessages.getRandomDrinkingMessage());
            }
            animateWaterDrop();
            reminderService.reset();
            showRandomFact();
        });
    }

    // ──────────────────────────────────────────
    //  SLIDER SETUP
    // ──────────────────────────────────────────

    private void setupSliders() {
        intervalSlider.setMin(10);
        intervalSlider.setMax(7200);
        intervalSlider.setValue(1800);
        intervalSlider.setBlockIncrement(10);
        intervalSlider.valueProperty().addListener((obs, o, n) -> {
            int sec = n.intValue();
            intervalValueLabel.setText(formatInterval(sec));
            reminderService.setInterval(sec);
        });
        intervalValueLabel.setText("30 min");

        goalSlider.setMin(4);
        goalSlider.setMax(15);
        goalSlider.setValue(8);
        goalSlider.setBlockIncrement(1);
        goalSlider.valueProperty().addListener((obs, o, n) -> {
            tracker.setDailyGoal(n.intValue());
            goalValueLabel.setText(n.intValue() + " Gläser");
        });
        goalValueLabel.setText("8 Gläser");
    }

    private String formatInterval(int totalSeconds) {
        if (totalSeconds < 60) return totalSeconds + " sek";
        int min = totalSeconds / 60;
        int sec = totalSeconds % 60;
        return sec > 0 ? min + " min " + sec + "s" : min + " min";
    }

    // ──────────────────────────────────────────
    //  BUTTON ACTIONS
    // ──────────────────────────────────────────

    @FXML
    private void onDrinkButtonClicked() {
        boolean goalJustReached = tracker.drinkGlass();

        if (goalJustReached) {
            messageLabel.setText(FunMessages.getRandomGoalReachedMessage());
            showCelebration();
        } else {
            messageLabel.setText(FunMessages.getRandomDrinkingMessage());
        }

        animateWaterDrop();
        animateDrinkButton();
        spawnSplashParticles();
        reminderService.reset();
        showRandomFact();
    }

    @FXML
    private void onUndoClicked() {
        tracker.undoLastGlass();
        messageLabel.setText("Glas entfernt.");
    }

    // ──────────────────────────────────────────
    //  ANIMATIONEN
    // ──────────────────────────────────────────

    private void setupAnimations() {
        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(3), waterDropEmoji);
        floatAnim.setByY(-6);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.play();
    }

    private void animateWaterDrop() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(250), waterDropEmoji);
        scale.setFromX(1.0); scale.setFromY(1.0);
        scale.setToX(1.15);  scale.setToY(1.15);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    private void animateDrinkButton() {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(150), drinkButton);
        pulse.setFromX(1.0); pulse.setFromY(1.0);
        pulse.setToX(0.92);  pulse.setToY(0.92);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();
    }

    private void animateReminderAlert() {
        FadeTransition fade = new FadeTransition(Duration.millis(300), messageLabel);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.play();

        mainContainer.getStyleClass().add("reminder-flash");
        PauseTransition remove = new PauseTransition(Duration.seconds(2));
        remove.setOnFinished(e -> mainContainer.getStyleClass().remove("reminder-flash"));
        remove.play();
    }

    private void spawnSplashParticles() {
        for (int i = 0; i < 4; i++) {
            Text p = new Text("·");
            p.setStyle("-fx-font-size: 14px; -fx-fill: #3b82f6;");
            p.setOpacity(0.6);
            if (rootPane == null) continue;
            rootPane.getChildren().add(p);
            StackPane.setAlignment(p, Pos.CENTER);

            double angle = Math.random() * 360;
            double dist  = 30 + Math.random() * 50;
            TranslateTransition move = new TranslateTransition(Duration.millis(500), p);
            move.setByX(Math.cos(Math.toRadians(angle)) * dist);
            move.setByY(Math.sin(Math.toRadians(angle)) * dist - 40);

            FadeTransition fade = new FadeTransition(Duration.millis(500), p);
            fade.setFromValue(0.6); fade.setToValue(0.0);
            fade.setOnFinished(e -> rootPane.getChildren().remove(p));

            new ParallelTransition(move, fade).play();
        }
    }

    private void showCelebration() {
        if (celebrationOverlay == null) return;
        celebrationOverlay.setVisible(true);
        celebrationOverlay.setOpacity(0);

        Label lbl = new Label("Tagesziel erreicht!");
        lbl.getStyleClass().add("celebration-text");
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);
        celebrationOverlay.getChildren().setAll(lbl);

        FadeTransition in  = new FadeTransition(Duration.millis(400), celebrationOverlay);
        in.setFromValue(0); in.setToValue(1);

        FadeTransition out = new FadeTransition(Duration.millis(600), celebrationOverlay);
        out.setFromValue(1); out.setToValue(0);
        out.setOnFinished(e -> {
            celebrationOverlay.setVisible(false);
            celebrationOverlay.getChildren().clear();
        });

        new SequentialTransition(in, new PauseTransition(Duration.seconds(2)), out).play();
        spawnConfetti();
    }

    private void spawnConfetti() {
        String[] dots = {"·", "•", "◦"};
        for (int i = 0; i < 8; i++) {
            Text p = new Text(dots[(int)(Math.random() * dots.length)]);
            p.setStyle("-fx-font-size: 16px; -fx-fill: #3b82f6;");
            if (rootPane == null) continue;
            rootPane.getChildren().add(p);
            p.setTranslateX(-100 + Math.random() * 200);
            p.setTranslateY(-200);

            TranslateTransition fall = new TranslateTransition(
                Duration.millis(1200 + Math.random() * 1000), p);
            fall.setByY(400 + Math.random() * 100);
            fall.setByX(-30 + Math.random() * 60);

            FadeTransition fade = new FadeTransition(Duration.millis(1800), p);
            fade.setFromValue(0.5); fade.setToValue(0.0);
            fade.setOnFinished(e -> rootPane.getChildren().remove(p));

            ParallelTransition pt = new ParallelTransition(fall, fade);
            pt.setDelay(Duration.millis(Math.random() * 300));
            pt.play();
        }
    }

    // ──────────────────────────────────────────
    //  HILFSMETHODEN
    // ──────────────────────────────────────────

    private void updateGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        String greeting;
        if (hour < 10)      greeting = "Guten Morgen!";
        else if (hour < 14) greeting = "Guten Tag!";
        else if (hour < 18) greeting = "Guten Nachmittag!";
        else                greeting = "Guten Abend!";
        greetingLabel.setText(greeting);
    }

    private void showRandomFact() {
        factLabel.setText(FunMessages.getRandomFact());
    }
}
