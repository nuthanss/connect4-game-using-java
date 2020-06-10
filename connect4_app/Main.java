package com.nuthan.connect4_app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private  Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane myGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();
        Scene scene = new Scene(myGridPane);

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane = (Pane) myGridPane.getChildren().get(0);
        menuPane.getChildren().addAll(menuBar);
        primaryStage.setScene(scene);
        primaryStage.setTitle("                           " +
                              "                            " +
                              "                Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    private MenuBar createMenu()
    {
        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New");
        newGame.setOnAction(event -> controller.resetGame());

        MenuItem resetGame = new MenuItem("Reset");
        resetGame.setOnAction(event -> controller.resetGame());

        MenuItem exitGame = new MenuItem("Exit");
        exitGame.setOnAction(event -> exit());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutme());

        MenuItem aboutgame = new MenuItem("About Game");
        aboutgame.setOnAction(event -> aboutgame());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        helpMenu.getItems().addAll(aboutMe,separator,aboutgame);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

            return menuBar;
    }

    private void aboutme() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("NUTHAN S S");
        alert.setContentText("Hi, This is my first java Desktop app that I built, " +
                             "soon I'll learn java and I'll become an java app Deveoper " +
                             "THANKYOU:)");
        alert.show();
    }


    private void aboutgame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About connect 4");
        alert.setHeaderText("How to play");
        alert.setContentText("Connect Four is a two-player connection game in " +
                             "which the players first choose a color and then take turns dropping " +
                             "colored discs from the top into a seven-column, six-row vertically " +
                             "suspended grid. The pieces fall straight down, occupying the next " +
                             "available space within the column. The objective of the game is to " +
                             "be the first to form a horizontal, vertical, or diagonal line of four " +
                             "of one's own discs. Connect Four is a solved game. The first player " +
                             "can always win by playing the right moves.");
        alert.show();
    }

    private void exit() {
        Platform.exit();
        System.exit(0);
    }

    private void reset() {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
