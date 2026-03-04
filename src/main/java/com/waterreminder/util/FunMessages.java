package com.waterreminder.util;

import java.util.Random;

/**
 * 🎉 Lustige Sprüche und Nachrichten rund ums Wassertrinken!
 * Weil Hydration Spaß machen soll!
 */
public class FunMessages {

    private static final Random random = new Random();

    // ========================
    //  ERINNERUNGS-SPRÜCHE
    // ========================
    private static final String[] REMINDER_MESSAGES = {
        "Hey, trink bitte Wasser!",
        "Ich pass auf dich auf \ntrink ma was bitti!",
        "Kleine Erinnerung von jemandem,\ndem du sehr am Herzen liegst.",
        "Du bist das Schönste in meinem Leben \nbitte trink mal was ^^.",
        "Psst...,\nTrink ma Waaser jetzt!",
        "Ich wünschte ich könnte dir\ngerade ein ganz ganz kelines bissl Wasser bringen.",
        "Weil du mir wichtig bist \nbitte trink ein Glas Wasser!",
        "Kleine Pause, tief durchatmen,\nein Glas Wasser trinken.",
    };

    // ========================
    //  NACH DEM TRINKEN
    // ========================
    private static final String[] DRINKING_MESSAGES = {
        "Ich bin so stolz auf dich!",
        "Du bist die Beste!",
        "Danke, dass du so gut auf dich achtest.",
        "Das freut mich wirklich sehr!",
        "Siehst du? Du schaffst das!",
        "So macht Trinken Spaß!",
    };

    // ========================
    //  ZIEL ERREICHT
    // ========================
    private static final String[] GOAL_REACHED_MESSAGES = {
        "Tagesziel erreicht!\nDu bist subba.",
        "Ich bin soooooooo stolz auf dich!",
        "Du hast dein Ziel erreicht –\ngenau wie bei allem, was du willst!",
        "Das verdient eine Umarmung!\n(Die kriegste immer wennde willst hihi!)",
    };

    // ========================
    //  MOTIVATIONS-SPRÜCHE
    // ========================
    private static final String[] MOTIVATION_MESSAGES = {
        "Du schaffst das!",
        "Wer braucht Energy Drinks\nwenn man H₂O hat?",
        "Wasser ist der OG aller Getränke!",
        "Du bist auf dem richtigen Weg!",
        "Der Ozean war auch mal ein Tropfen.",
        "Hydration Station! Nächster Halt: DU!",
    };

    // ========================
    //  WASSER-FAKTEN
    // ========================
    private static final String[] WATER_FACTS = {
        "Fakt: Das Gehirn besteht zu 75% aus Wasser!",
        "Fakt: Dehydration kann Kopfschmerzen verursachen!",
        "Fakt: Wasser hilft beim Abnehmen!",
        "Fakt: Schon 2% Wassermangel mindert die Leistung!",
        "Fakt: Wasser reguliert die Körpertemperatur!",
        "Fakt: Deine Haut liebt Wasser mehr als jede Creme!",
        "Fakt: Wasser hilft beim Transport von Nährstoffen!",
        "Fakt: Ein Mensch kann ~3 Tage ohne Wasser überleben!",
        "Fakt: Kaltes Wasser verbrennt extra Kalorien!",
        "Fakt: Die Erde hat 1,4 Milliarden km³ Wasser!",
    };

    // ========================
    //  EMOJI WASSER-ANIMATION
    // ========================
    private static final String[] WATER_EMOJIS = {
        "💧", "🌊", "🚿", "🧊", "💦", "🐳", "🐟", "🌧️", "☔", "🏊",
    };

    // === Öffentliche Methoden ===

    public static String getRandomReminder() {
        return REMINDER_MESSAGES[random.nextInt(REMINDER_MESSAGES.length)];
    }

    public static String getRandomDrinkingMessage() {
        return DRINKING_MESSAGES[random.nextInt(DRINKING_MESSAGES.length)];
    }

    public static String getRandomGoalReachedMessage() {
        return GOAL_REACHED_MESSAGES[random.nextInt(GOAL_REACHED_MESSAGES.length)];
    }

    public static String getRandomMotivation() {
        return MOTIVATION_MESSAGES[random.nextInt(MOTIVATION_MESSAGES.length)];
    }

    public static String getRandomFact() {
        return WATER_FACTS[random.nextInt(WATER_FACTS.length)];
    }

    public static String getRandomWaterEmoji() {
        return WATER_EMOJIS[random.nextInt(WATER_EMOJIS.length)];
    }

    /**
     * Gibt einen passenden Spruch basierend auf der Tageszeit zurück.
     */
    public static String getTimeBasedGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 6) {
            return "Nachteule? Trink erstmal Wasser!";
        } else if (hour < 10) {
            return "Guten Morgen! Starte den Tag mit Wasser!";
        } else if (hour < 12) {
            return "Vormittag! Perfekte Zeit für ein Glas!";
        } else if (hour < 14) {
            return "Mittagszeit! Wasser zum Essen nicht vergessen!";
        } else if (hour < 17) {
            return "Nachmittags-Durst? Wasser marsch!";
        } else if (hour < 20) {
            return "Feierabend! Belohn dich mit einem Glas Wasser!";
        } else {
            return "Abend! Noch ein Glas vor dem Schlafen?";
        }
    }

    /**
     * Gibt einen Kommentar zum Fortschritt zurück.
     */
    public static String getProgressComment(double progress) {
        if (progress <= 0) {
            return "Noch kein Wasser? Deine Zellen weinen!";
        } else if (progress < 0.25) {
            return "Ein guter Anfang! Weiter so!";
        } else if (progress < 0.5) {
            return "Fast die Hälfte! Du rockst!";
        } else if (progress < 0.75) {
            return "Über die Hälfte! Mega!";
        } else if (progress < 1.0) {
            return "SO KURZ VOR DEM ZIEL! COME ON!";
        } else {
            return "ZIEL ERREICHT! Du bist ein Aqua-Held!";
        }
    }
}
