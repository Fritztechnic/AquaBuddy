package com.waterreminder.ui;

import com.waterreminder.util.FunMessages;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Kleines Always-On-Top Popup-Fenster, das ein lustiges GIF abspielt
 * und dich ans Wassertrinken erinnert.
 */
public class NotificationPopup {

    private static final int POPUP_WIDTH = 320;
    private static final int POPUP_HEIGHT = 380;
    private static final int AUTO_CLOSE_SECONDS = 15;

    private Stage popupStage;

    /**
     * Zeigt das Benachrichtigungs-Popup mit dem GIF an.
     * @param onDrink wird aufgerufen wenn der Nutzer auf "Hab getrunken!" klickt
     */
    public void show(Runnable onDrink) {
        if (popupStage != null && popupStage.isShowing()) {
            popupStage.toFront();
            return;
        }

        popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.setAlwaysOnTop(true);
        popupStage.setTitle("AquaBuddy Erinnerung");

        // === Layout ===
        VBox root = new VBox(0);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle(
            "-fx-background-color: #151515;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #252525;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        // -- Titelleiste mit Drag & Close --
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPadding(new Insets(6, 8, 2, 12));
        titleBar.setStyle("-fx-background-color: transparent;");

        Label titleLabel = new Label("TRINK WASSER!");
        titleLabel.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Button closeBtn = new Button("x");
        closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #555555;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 2 8 2 8;"
        );
        closeBtn.setOnAction(e -> close());
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
            "-fx-background-color: #2a2a2a;" +
            "-fx-text-fill: #cccccc;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 2 8 2 8;" +
            "-fx-background-radius: 6;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #555555;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 2 8 2 8;"
        ));

        titleBar.getChildren().addAll(titleLabel, closeBtn);

        // -- GIF-Anzeige via WebView --
        WebView webView = new WebView();
        webView.setPrefSize(POPUP_WIDTH - 24, 200);
        webView.setMaxSize(POPUP_WIDTH - 24, 200);

        // GIF aus JAR-Ressourcen laden (funktioniert offline + auf Windows)
        WebEngine engine = webView.getEngine();
        String gifUrl = getClass().getResource("/cat-drinking.gif").toExternalForm();
        String html = """
            <!DOCTYPE html>
            <html>
            <head><style>
                * { margin: 0; padding: 0; }
                body {
                    background: #151515;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    overflow: hidden;
                }
                img {
                    max-width: 100%%;
                    max-height: 100%%;
                    border-radius: 8px;
                    object-fit: contain;
                }
            </style></head>
            <body>
                <img src="%s" alt="Trink Wasser!"/>
            </body>
            </html>
            """.formatted(gifUrl);
        engine.loadContent(html);

        // Scrollbars deaktivieren
        webView.getChildrenUnmodifiable().addListener(
            (javafx.collections.ListChangeListener.Change<?> c) -> {
                // WebView scrollbar entfernen via CSS
            }
        );
        engine.setUserStyleSheetLocation("data:text/css," +
            "body { overflow: hidden !important; } " +
            "::-webkit-scrollbar { display: none !important; }"
        );

        StackPane gifContainer = new StackPane(webView);
        gifContainer.setPadding(new Insets(0, 12, 0, 12));

        // -- Nachricht --
        Label messageLabel = new Label(FunMessages.getRandomReminder());
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(POPUP_WIDTH - 48);
        messageLabel.setStyle(
            "-fx-text-fill: #cccccc;" +
            "-fx-font-size: 13px;" +
            "-fx-text-alignment: center;" +
            "-fx-padding: 10 12 6 12;"
        );

        // -- Button --
        Button drinkBtn = new Button("Hab getrunken!");
        drinkBtn.setStyle(
            "-fx-background-color: #3b82f6;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 18;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 24 8 24;"
        );
        drinkBtn.setOnAction(e -> {
            if (onDrink != null) onDrink.run();
            close();
        });
        drinkBtn.setOnMouseEntered(e -> drinkBtn.setStyle(
            "-fx-background-color: #4a8ff7;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 18;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 24 8 24;"
        ));
        drinkBtn.setOnMouseExited(e -> drinkBtn.setStyle(
            "-fx-background-color: #3b82f6;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 18;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 24 8 24;"
        ));

        VBox buttonBox = new VBox(drinkBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(4, 0, 12, 0));

        root.getChildren().addAll(titleBar, gifContainer, messageLabel, buttonBox);

        // -- Draggable machen --
        final double[] dragOffset = new double[2];
        titleBar.setOnMousePressed(e -> {
            dragOffset[0] = e.getScreenX() - popupStage.getX();
            dragOffset[1] = e.getScreenY() - popupStage.getY();
        });
        titleBar.setOnMouseDragged(e -> {
            popupStage.setX(e.getScreenX() - dragOffset[0]);
            popupStage.setY(e.getScreenY() - dragOffset[1]);
        });

        Scene scene = new Scene(root, POPUP_WIDTH, POPUP_HEIGHT);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popupStage.setScene(scene);

        // Position: unten rechts auf dem Bildschirm
        javafx.geometry.Rectangle2D screenBounds =
            javafx.stage.Screen.getPrimary().getVisualBounds();
        popupStage.setX(screenBounds.getMaxX() - POPUP_WIDTH - 20);
        popupStage.setY(screenBounds.getMaxY() - POPUP_HEIGHT - 20);

        popupStage.show();

        // Einblend-Animation
        root.setOpacity(0);
        root.setTranslateY(20);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), root);
        slideIn.setFromY(20);
        slideIn.setToY(0);
        new ParallelTransition(fadeIn, slideIn).play();

        // Auto-Close nach X Sekunden
        PauseTransition autoClose = new PauseTransition(Duration.seconds(AUTO_CLOSE_SECONDS));
        autoClose.setOnFinished(e -> close());
        autoClose.play();
    }

    /**
     * Schließt das Popup mit Ausblend-Animation.
     */
    private void close() {
        if (popupStage == null || !popupStage.isShowing()) return;

        VBox root = (VBox) popupStage.getScene().getRoot();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            if (popupStage != null) {
                popupStage.close();
                popupStage = null;
            }
        });
        fadeOut.play();
    }
}
