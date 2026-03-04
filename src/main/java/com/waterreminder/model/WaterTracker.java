package com.waterreminder.model;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Verfolgt den täglichen Wasserkonsum und den Fortschritt.
 */
public class WaterTracker {

    private final IntegerProperty glassesConsumed = new SimpleIntegerProperty(0);
    private final IntegerProperty dailyGoal = new SimpleIntegerProperty(8);
    private final IntegerProperty glassSize = new SimpleIntegerProperty(250); // ml
    private final DoubleProperty progress = new SimpleDoubleProperty(0.0);
    private final IntegerProperty streakDays = new SimpleIntegerProperty(0);
    private final StringProperty lastDrinkTime = new SimpleStringProperty("--:--");
    private final BooleanProperty goalReached = new SimpleBooleanProperty(false);

    private LocalDate currentDate = LocalDate.now();

    // === Getters & Properties ===

    public int getGlassesConsumed() { return glassesConsumed.get(); }
    public IntegerProperty glassesConsumedProperty() { return glassesConsumed; }

    public int getDailyGoal() { return dailyGoal.get(); }
    public void setDailyGoal(int goal) { dailyGoal.set(goal); updateProgress(); }
    public IntegerProperty dailyGoalProperty() { return dailyGoal; }

    public int getGlassSize() { return glassSize.get(); }
    public void setGlassSize(int size) { glassSize.set(size); }
    public IntegerProperty glassSizeProperty() { return glassSize; }

    public double getProgress() { return progress.get(); }
    public DoubleProperty progressProperty() { return progress; }

    public int getStreakDays() { return streakDays.get(); }
    public IntegerProperty streakDaysProperty() { return streakDays; }

    public String getLastDrinkTime() { return lastDrinkTime.get(); }
    public StringProperty lastDrinkTimeProperty() { return lastDrinkTime; }

    public boolean isGoalReached() { return goalReached.get(); }
    public BooleanProperty goalReachedProperty() { return goalReached; }

    /**
     * Trinkt ein Glas Wasser!
     * @return true wenn das Tagesziel gerade erreicht wurde
     */
    public boolean drinkGlass() {
        checkNewDay();
        boolean wasNotReached = !goalReached.get();
        glassesConsumed.set(glassesConsumed.get() + 1);
        updateProgress();

        // Zeitstempel aktualisieren
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        lastDrinkTime.set(now.format(formatter));

        // Prüfe ob Ziel gerade erreicht
        if (wasNotReached && goalReached.get()) {
            streakDays.set(streakDays.get() + 1);
            return true; // Ziel gerade erreicht!
        }
        return false;
    }

    /**
     * Berechnet den gesamten Wasserverbrauch in ml.
     */
    public int getTotalMl() {
        return glassesConsumed.get() * glassSize.get();
    }

    /**
     * Gibt den Verbrauch in Litern zurück.
     */
    public double getTotalLiters() {
        return getTotalMl() / 1000.0;
    }

    /**
     * Setzt den Tracker für einen neuen Tag zurück.
     */
    private void checkNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(currentDate)) {
            // Neuer Tag! Wenn gestern das Ziel nicht erreicht wurde, Streak zurücksetzen.
            if (!goalReached.get()) {
                streakDays.set(0);
            }
            glassesConsumed.set(0);
            goalReached.set(false);
            progress.set(0.0);
            lastDrinkTime.set("--:--");
            currentDate = today;
        }
    }

    private void updateProgress() {
        double p = Math.min(1.0, (double) glassesConsumed.get() / dailyGoal.get());
        progress.set(p);
        goalReached.set(glassesConsumed.get() >= dailyGoal.get());
    }

    /**
     * Entfernt das letzte Glas (Undo).
     */
    public void undoLastGlass() {
        if (glassesConsumed.get() > 0) {
            glassesConsumed.set(glassesConsumed.get() - 1);
            updateProgress();
        }
    }
}
