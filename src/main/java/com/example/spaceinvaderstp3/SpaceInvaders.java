package com.example.spaceinvaderstp3;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

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
        return racine.getChildren().stream().map(n -> (Sprite)n).collect(Collectors.toList());
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

        racine.getChildren().removeIf(n ->
        {
            Sprite s = (Sprite) n;
            return s.mort;
        });

        if (temps > 2)
        {
            temps = 0;
        }
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
            }
        });

        stage.setScene(scene);
        stage.show();
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