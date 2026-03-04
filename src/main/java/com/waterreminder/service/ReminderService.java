package com.waterreminder.service;

import com.waterreminder.ui.NotificationPopup;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hintergrund-Dienst der Wasser-Erinnerungen sendet –
 * auch wenn das Hauptfenster geschlossen ist.
 */
public class ReminderService {

    private static final ReminderService INSTANCE = new ReminderService();

    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "reminder-worker");
            t.setDaemon(false); // Kein Daemon → App läuft weiter
            return t;
        });

    private final NotificationPopup popup = new NotificationPopup();

    private ScheduledFuture<?> currentTask;
    private int intervalSeconds = 30 * 60;

    // Countdown für die UI (wird jede Sekunde dekrementiert)
    private final AtomicInteger secondsUntilNext = new AtomicInteger(0);
    private ScheduledFuture<?> countdownTask;

    // Callback → UI aktualisieren
    private Runnable onTick;
    private Runnable onReminder;
    private Runnable onDrink;

    private ReminderService() {}

    public static ReminderService getInstance() {
        return INSTANCE;
    }

    /** Startet den Reminder mit dem gesetzten Intervall. */
    public void start(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        reschedule();
        startCountdown();
    }

    /** Ändert das Intervall und startet neu. */
    public void setInterval(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        reschedule();
        startCountdown();
    }

    /** Setzt den Countdown zurück (z.B. nach manuellem Trinken). */
    public void reset() {
        reschedule();
        startCountdown();
    }

    /** Gibt die verbleibenden Sekunden zurück. */
    public int getSecondsUntilNext() {
        return secondsUntilNext.get();
    }

    /** Callback für UI-Tick (jede Sekunde auf FX-Thread). */
    public void setOnTick(Runnable callback) {
        this.onTick = callback;
    }

    /** Callback wenn Erinnerung ausgelöst wird. */
    public void setOnReminder(Runnable callback) {
        this.onReminder = callback;
    }

    /** Callback wenn im Popup auf 'Hab getrunken' geklickt wird. */
    public void setOnDrink(Runnable callback) {
        this.onDrink = callback;
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }

    // ──────────────────────────────────

    private void reschedule() {
        if (currentTask != null) currentTask.cancel(false);
        currentTask = scheduler.schedule(this::fireReminder,
            intervalSeconds, TimeUnit.SECONDS);
    }

    private void startCountdown() {
        if (countdownTask != null) countdownTask.cancel(false);
        secondsUntilNext.set(intervalSeconds);

        countdownTask = scheduler.scheduleAtFixedRate(() -> {
            int remaining = secondsUntilNext.decrementAndGet();
            if (remaining < 0) secondsUntilNext.set(0);
            if (onTick != null) Platform.runLater(onTick);
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void fireReminder() {
        Platform.runLater(() -> {
            popup.show(onDrink);
            if (onReminder != null) onReminder.run();
        });
        reschedule();
        startCountdown();
    }
}
