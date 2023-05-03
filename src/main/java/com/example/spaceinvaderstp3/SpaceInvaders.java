package com.example.spaceinvaderstp3;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.TranslateTransition;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class SpaceInvaders extends Application
{
    private double tempsDeRedemarrage;
    private MediaPlayer mediaPlayer;
    private MediaPlayer ennemiMediaPlayer;
    private boolean explosionAffichee = false;

    private Pane racine = new Pane();

    private double temps = 0;

    private Sprite Joueur = new Sprite(300, 750, 40, 40, "Joueur", Color.BLUE);

    private Parent creationDeContenu()
    {
        racine.setPrefSize(1000, 1000);

        racine.setStyle("-fx-background-color: black;");

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

    private void explosion(Sprite ennemi)
    {
        URL explosionUrl = getClass().getResource("/Explosion.gif");
        Image explosionImage = new Image(explosionUrl.toString());
        Sprite explosion = new Sprite((int) ennemi.getTranslateX(), (int) ennemi.getTranslateY(), (int) ennemi.rectangle.getWidth(), (int) ennemi.rectangle.getHeight(), "explosion", explosionImage);
        explosion.imageView.setTranslateX(0); // Ajouter cette ligne pour corriger le positionnement de l'image
        explosion.imageView.setTranslateY(0); // Ajouter cette ligne pour corriger le positionnement de l'image
        racine.getChildren().add(explosion);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> racine.getChildren().remove(explosion)));
        timeline.play();
    }

    private void explosionJoueur(Sprite joueur)
    {
        if (!explosionAffichee) {
            URL explosionJoueurUrl = getClass().getResource("/ExplosionJoueur.gif");
            Image explosionJoueurImage = new Image(explosionJoueurUrl.toString());
            Sprite explosion = new Sprite((int) joueur.getTranslateX(), (int) joueur.getTranslateY(), (int) joueur.rectangle.getWidth(), (int) joueur.rectangle.getHeight(), "explosion", explosionJoueurImage);
            explosion.imageView.setTranslateX(0); // Ajouter cette ligne pour corriger le positionnement de l'image
            explosion.imageView.setTranslateY(0); // Ajouter cette ligne pour corriger le positionnement de l'image
            racine.getChildren().add(explosion);

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> racine.getChildren().remove(explosion)));
            timeline.play();

            explosionAffichee = true;
        }
    }


    public SpaceInvaders() //***********************************************************************************sert a charger fichier audio et creer mediaPLayer mais il faut changer le nom "SpaceInvaders()"
    {
        URL audioUrl = getClass().getResource("/tirLaserJoueur.mp3");
        Media audioMedia = new Media(audioUrl.toString());
        mediaPlayer = new MediaPlayer(audioMedia);

        URL ennemiAudioUrl = getClass().getResource("/tirLaserEnnemi.mp3");
        Media ennemiAudioMedia = new Media(ennemiAudioUrl.toString());
        ennemiMediaPlayer = new MediaPlayer(ennemiAudioMedia);
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
        tempsDeRedemarrage += 0.016;

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
                            explosion(ennemi);
                        }
                    });

                    break;

                case "ennemi":

                    if (tempsDeRedemarrage > 2 && temps > 2) {
                        if (Math.random() < 0.3) {
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
            explosionJoueur(Joueur);
            racine.getChildren().remove(Joueur);
            displayDefeatMessage();
        }
    }

    private void displayVictoryMessage()
    {
        Text victoryMessage = new Text("VOUS AVEZ GAGNÉ !");
        Text restartMessage = new Text("Appuyez sur R pour recommencer.");

        victoryMessage.setFont(Font.font("Verdana", 50));
        victoryMessage.setFill(Color.GREEN);
        restartMessage.setFont(Font.font("Verdana", 50));
        restartMessage.setFill(Color.GREEN);

        VBox vbox = new VBox(10, victoryMessage, restartMessage);
        vbox.setAlignment(Pos.CENTER);

        // Deplacer les 2 textes
        vbox.widthProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutX(racine.getPrefWidth() / 2 - newValue.doubleValue() / 2 - 10));
        vbox.heightProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutY(racine.getPrefHeight() / 2 - newValue.doubleValue() / 2));


        Pane messagePane = new Pane();
        messagePane.setPrefSize(racine.getPrefWidth(), racine.getPrefHeight());
        messagePane.getChildren().add(vbox);
        vbox.setLayoutX(racine.getPrefWidth() / 2 - vbox.getPrefWidth() / 2);
        vbox.setLayoutY(racine.getPrefHeight() / 2 - vbox.getPrefHeight() / 2);

        racine.getChildren().add(messagePane);
    }

    private void displayDefeatMessage()
    {
        Text defeatMessage = new Text("VOUS AVEZ PERDU !");
        Text restartMessage = new Text("Appuyez sur R pour recommencer.");

        defeatMessage.setFont(Font.font("Verdana", 50));
        defeatMessage.setFill(Color.RED);
        restartMessage.setFont(Font.font("Verdana", 50));
        restartMessage.setFill(Color.RED);

        VBox vbox = new VBox(10, defeatMessage, restartMessage);
        vbox.setAlignment(Pos.CENTER);

        // Deplacer les 2 textes
        vbox.widthProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutX(racine.getPrefWidth() / 2 - newValue.doubleValue() / 2 - 10));
        vbox.heightProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutY(racine.getPrefHeight() / 2 - newValue.doubleValue() / 2));


        Pane messagePane = new Pane();
        messagePane.setPrefSize(racine.getPrefWidth(), racine.getPrefHeight());
        messagePane.getChildren().add(vbox);
        vbox.setLayoutX(racine.getPrefWidth() / 2 - vbox.getPrefWidth() / 2);
        vbox.setLayoutY(racine.getPrefHeight() / 2 - vbox.getPrefHeight() / 2);

        racine.getChildren().add(messagePane);
    }

    private void redemarrerJeu()
    {
        racine.getChildren().clear();
        Joueur = new Sprite(300, 750, 40, 40, "Joueur", Color.BLUE);

        // Ajout de la transition
        TranslateTransition transitionHaut = new TranslateTransition(Duration.seconds(1), Joueur);
        transitionHaut.setFromY(-racine.getPrefHeight() / 2);
        transitionHaut.setToY(750);

        TranslateTransition transitionBas = new TranslateTransition(Duration.seconds(1), racine);
        transitionBas.setFromY(racine.getPrefHeight() / 2);
        transitionBas.setToY(0);

        transitionHaut.play();
        transitionBas.play();

        racine.getChildren().add(Joueur);
        prochainNiveau();
        explosionAffichee = false;
        tempsDeRedemarrage = 0;

    }



    private void tir(Sprite qui)
    {
        Sprite s = new Sprite((int) qui.getTranslateX() + 20, (int) qui.getTranslateY(), 5, 20, qui.type + "Tir", Color.PURPLE);

        racine.getChildren().add(s);

        if (qui.type.equals("Joueur"))
        {
            mediaPlayer.stop(); // Arrête la lecture en cours, si nécessaire
            mediaPlayer.seek(Duration.ZERO); // Remet le lecteur à zéro
            mediaPlayer.play(); // Joue le son de tir de laser
        }
        else if (qui.type.equals("ennemi"))
        {
            ennemiMediaPlayer.stop(); // Arrête la lecture en cours, si nécessaire
            ennemiMediaPlayer.seek(Duration.ZERO); // Remet le lecteur à zéro
            ennemiMediaPlayer.play(); // Joue le son de tir de laser ennemi
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(creationDeContenu());

        scene.setOnKeyPressed(e -> {
            if (!Joueur.mort) {
                switch (e.getCode()) {
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
            } else if (e.getCode() == KeyCode.R) {
                redemarrerJeu();
            }
        });

        stage.setScene(scene);
        stage.show();
    }


    private boolean toutLesEnnemisDetruits()
    {
        return sprites().stream().noneMatch(s -> s.type.equals("ennemi"));
    }

    private static class Sprite extends Group
    {
        Rectangle rectangle;
        boolean mort = false;
        final String type;
        ImageView imageView;


        Sprite(int x, int y, int w, int h, String type, Color couleur) {
            rectangle = new Rectangle(w, h, couleur);
            this.type = type;
            setTranslateX(x);
            setTranslateY(y);
            getChildren().add(rectangle);
        }

        Sprite(int x, int y, int w, int h, String type, Image image) {
            rectangle = new Rectangle(w, h);
            this.type = type;
            setTranslateX(x);
            setTranslateY(y);

            imageView = new ImageView(image);
            imageView.setFitWidth(w);
            imageView.setFitHeight(h);
            imageView.setTranslateX(x);
            imageView.setTranslateY(y);
            getChildren().addAll(rectangle, imageView);
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