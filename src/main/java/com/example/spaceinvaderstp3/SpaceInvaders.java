package com.example.spaceinvaderstp3;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;





import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;

public class SpaceInvaders extends Application
{
    private Pane racine = new Pane();

    private double temps = 0;

    private Sprite Joueur = new Sprite(300, 750, 40, 40, "Joueur", Color.BLUE);

    private Parent creationDeContenu()
    {
        racine.setPrefSize(1000, 1000);

        racine.getChildren().add(Joueur);

        // Va rendre les animation plus fluide
        AnimationTimer minuteur = new AnimationTimer()
        {
            @Override
            public void handle(long maintenant)
            {
                miseAJour();
            }
        };

        minuteur.start();

        prochainNiveau();

        return racine;
    }

    private void prochainNiveau()
    {
        for (int i = 0; i < 5; i++)
        {
            Sprite s = new Sprite(90 + i*100, 150, 30, 30, "ennemi", Color.RED);

            racine.getChildren().add(s);
        }
    }



    private List<Sprite> sprites()
    {
        return racine.getChildren().stream()
                .filter(n -> n instanceof Sprite)
                .map(n -> (Sprite) n)
                .collect(Collectors.toList());
    }

    private void miseAJour()
    {
        temps += 0.016;

        sprites().forEach(s ->
        {
            switch (s.type)
            {
                case "ennemiTir":
                    s.versLeBas();

                    if (s.getBoundsInParent().intersects(Joueur.getBoundsInParent()))
                    {
                        Joueur.mort = true;
                        s.mort = true;
                    }
                    break;

                case "JoueurTir":
                    s.versLeHaut();

                    sprites().stream().filter(e -> e.type.equals("ennemi")).forEach(ennemi ->
                    {
                        if (s.getBoundsInParent().intersects(ennemi.getBoundsInParent()))
                        {
                            ennemi.mort = true;
                            s.mort = true;
                        }
                    });

                    break;

                case "ennemi":

                    if (temps > 2)
                    {
                        if (Math.random() < 0.3)
                        {
                            tir(s);
                        }
                    }

                    break;
            }
        });

        racine.getChildren().removeIf(n -> n instanceof Sprite && ((Sprite) n).mort);


        if (temps > 2)
        {
            temps = 0;
        }

        if (toutLesEnnemisDetruits())
        {
            displayVictoryMessage();
        }

        if (Joueur.mort)
        {
            displayDefeatMessage();
        }
    }

    private void displayVictoryMessage()
    {
        Text victoryMessage = new Text("VOUS AVEZ GAGNÉ ! Appuyez sur R pour recommencer.");
        victoryMessage.setFont(Font.font("Verdana", 50));
        victoryMessage.setFill(Color.GREEN);

        VBox vbox = new VBox(10, victoryMessage);
        vbox.setAlignment(Pos.CENTER);

        Pane messagePane = new Pane();
        messagePane.setPrefSize(racine.getPrefWidth(), racine.getPrefHeight());
        messagePane.getChildren().add(vbox);
        vbox.setLayoutX(racine.getPrefWidth() / 2 - vbox.getPrefWidth() / 2);
        vbox.setLayoutY(racine.getPrefHeight() / 2 - vbox.getPrefHeight() / 2);

        racine.getChildren().add(messagePane);
    }

    private void displayDefeatMessage()
    {
        Text defeatMessage = new Text("VOUS AVEZ PERDU ! Appuyez sur R pour recommencer.");
        defeatMessage.setFont(Font.font("Verdana", 50));
        defeatMessage.setFill(Color.RED);

        VBox vbox = new VBox(10, defeatMessage);
        vbox.setAlignment(Pos.CENTER);

        Pane messagePane = new Pane();
        messagePane.setPrefSize(racine.getPrefWidth(), racine.getPrefHeight());
        messagePane.getChildren().add(vbox);
        vbox.setLayoutX(racine.getPrefWidth() / 2 - vbox.getPrefWidth() / 2);
        vbox.setLayoutY(racine.getPrefHeight() / 2 - vbox.getPrefHeight() / 2);

        racine.getChildren().add(messagePane);
    }

    private void redemarrerJeu() {
        racine.getChildren().clear();
        Joueur = new Sprite(300, 750, 40, 40, "Joueur", Color.BLUE);
        racine.getChildren().add(Joueur);
        prochainNiveau();
    }


    private void tir(Sprite qui)
    {
        Sprite s = new Sprite((int) qui.getTranslateX() + 20, (int) qui.getTranslateY(), 5, 20, qui.type + "Tir", Color.PURPLE);

        racine.getChildren().add(s);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        Scene scene = new Scene(creationDeContenu());

        scene.setOnKeyPressed(e ->
        {
            switch (e.getCode())
            {
                case A:
                    Joueur.versLaGauche();
                    break;
                case D:
                    Joueur.versLaDroite();
                    break;
                case SPACE:
                    tir(Joueur);
                    break;
                case R:
                    redemarrerJeu();
                    break;
            }
        });


        stage.setScene(scene);
        stage.show();
    }

    private boolean toutLesEnnemisDetruits()
    {
        return sprites().stream().noneMatch(s -> s.type.equals("ennemi"));
    }

    private static class Sprite extends Rectangle
    {
        boolean mort = false;
        final String type;

        Sprite(int x, int y, int w, int h, String type, Color couleur)
        {
            super(w, h, couleur);

            this.type = type;
            setTranslateX(x);
            setTranslateY(y);
        }

        void versLaGauche() {
            setTranslateX(getTranslateX() - 5);
        }

        void versLaDroite() {
            setTranslateX(getTranslateX() + 5);
        }

        void versLeHaut() {
            setTranslateY(getTranslateY() - 5);
        }

        void versLeBas() {
            setTranslateY(getTranslateY() + 5);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}