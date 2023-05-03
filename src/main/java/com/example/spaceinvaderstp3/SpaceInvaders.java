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

/**
 * La classe qui fait fonctionner le jeu au complet
 */
public class SpaceInvaders extends Application
{
    private double tempsDeRedemarrage;
    private MediaPlayer mediaPlayer;
    private MediaPlayer ennemiMediaPlayer;
    private boolean explosionAffichee = false;

    private Pane racine = new Pane();

    private double temps = 0;

    private Sprite Joueur = new Sprite(300, 750, 40, 40, "Joueur", Color.BLUE);

    /**
     * Configure l'interface utilisateur et d'initialise le jeu
     * @return Retourne le Pane
     */
    private Parent creationDeContenu()
    {
        // Appelle la méthode setPrefSize sur l'objet racine et définit sa largeur et sa hauteur préférées à 1000 x 1000 pixels.
        racine.setPrefSize(1000, 1000);

        // Met l'arriere plan en noir
        racine.setStyle("-fx-background-color: black;");

        // Ajoute l'objet Joueur à la liste des enfants de l'objet racine.
        racine.getChildren().add(Joueur);

        // Va rendre les animation plus fluide en founissant un timer pour les animations
        AnimationTimer minuteur = new AnimationTimer()
        {
            /**
             * Appel la méthode miseAJour pour mettre à jour l'état du jeu
             * @param maintenant contient l'état actuel du jeu
             */
            @Override
            public void handle(long maintenant)
            {
                // Appel la méthode miseAJour
                miseAJour();
            }
        };

        // Démarre le timer d'animation en appelant la méthode start sur l'objet minuteur.
        minuteur.start();

        // Cette ligne appelle la méthode prochainNiveau pour charger le niveau suivant.
        prochainNiveau();

        // Retourne le Pane
        return racine;
    }

    /**
     *
     * @param ennemi
     */
    private void explosion(Sprite ennemi)
    {
        // Récupère l'URL de la ressource "Explosion.gif" et la stocke dans la variable explosionUrl.
        URL explosionUrl = getClass().getResource("/Explosion.gif");
        // Crée un nouvel objet Image à partir de l'URL de l'explosion en convertissant l'URL en chaîne de caractères.
        Image explosionImage = new Image(explosionUrl.toString());
        // Crée un nouvel objet Sprite appelé explosion avec les mêmes coordonnées x et y, largeur et hauteur que l'objet ennemi
        Sprite explosion = new Sprite((int) ennemi.getTranslateX(), (int) ennemi.getTranslateY(), (int) ennemi.rectangle.getWidth(), (int) ennemi.rectangle.getHeight(), "explosion", explosionImage);
        // Corrigent le positionnement de l'image d'explosion en réinitialisant les translations x et y à 0.
        explosion.imageView.setTranslateX(0);
        explosion.imageView.setTranslateY(0);
        // Ajoute l'objet Sprite explosion à la liste des enfants de l'objet racine, pour qu'il soit affiché à l'écran.
        racine.getChildren().add(explosion);
        // Crée un nouvel objet Timeline qui exécutera une action après un certain temps  et supprime l'objet explosion de la liste des enfants de racine.
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> racine.getChildren().remove(explosion)));
        // Supprime l'objet explosion de la liste des enfants de racine.
        timeline.play();
    }

    /**
     *
     * @param joueur
     */
    private void explosionJoueur(Sprite joueur)
    {
        // Vérifie si le gif de l'explosion n'a pas ete afficher
        if (!explosionAffichee)
        {
            // Récupère l'URL de la ressource "ExplosionJoueur.gif" et la stocke dans la variable explosionJoueurUrl.
            URL explosionJoueurUrl = getClass().getResource("/ExplosionJoueur.gif");
            // Crée un nouvel objet Image à partir de l'URL de l'explosion en convertissant l'URL en chaîne de caractères.
            Image explosionJoueurImage = new Image(explosionJoueurUrl.toString());
            // Crée un nouvel objet Sprite appelé explosion avec les mêmes coordonnées x et y, largeur et hauteur que l'objet joueur
            Sprite explosion = new Sprite((int) joueur.getTranslateX(), (int) joueur.getTranslateY(), (int) joueur.rectangle.getWidth(), (int) joueur.rectangle.getHeight(), "explosion", explosionJoueurImage);
            // Corrigent le positionnement de l'image d'explosion en réinitialisant les translations x et y à 0.
            explosion.imageView.setTranslateX(0);
            explosion.imageView.setTranslateY(0);
            // Ajoute l'objet Sprite explosionJoueur à la liste des enfants de l'objet racine, pour qu'il soit affiché à l'écran.
            racine.getChildren().add(explosion);
            // Crée un nouvel objet Timeline qui exécutera une action après un certain temps et supprime l'objet explosion de la liste des enfants de racine.
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> racine.getChildren().remove(explosion)));
            // Supprime l'objet explosion de la liste des enfants de racine.
            timeline.play();

            // L'explosion a déjà ete fait donc il ne le refera plus
            explosionAffichee = true;
        }
    }


    /**
     *
     */
    public SpaceInvaders() // sert a charger fichier audio et creer mediaPLayer mais il faut changer le nom "SpaceInvaders()"
    {
        // Cette ligne récupère l'URL de la ressource "tirLaserJoueur.mp3" et la stocke dans la variable audioUrl.
        URL audioUrl = getClass().getResource("/tirLaserJoueur.mp3");

        Media audioMedia = new Media(audioUrl.toString());
        mediaPlayer = new MediaPlayer(audioMedia);

        URL ennemiAudioUrl = getClass().getResource("/tirLaserEnnemi.mp3");
        Media ennemiAudioMedia = new Media(ennemiAudioUrl.toString());
        ennemiMediaPlayer = new MediaPlayer(ennemiAudioMedia);
    }


    /**
     * Cette méthode est responsable de créer et d'ajouter des ennemis au niveau.
     * Elle crée 5 ennemis de couleur rouge, les positionne horizontalement à une certaine distance les uns des autres et les affiches à l'écran.
     */
    private void prochainNiveau()
    {
        // Déclare une boucle "for" qui s'exécute 5 fois pour placer 5 ennemis
        for (int i = 0; i < 5; i++)
        {
            // Crée un nouvel objet Sprite appelé 's' avec les coordonnées x et y avec une distance entre chaque "ennemi" et les mets en rouge
            Sprite s = new Sprite(90 + i*100, 150, 30, 30, "ennemi", Color.RED);

            // Ajoute l'objet Sprite 's' à la liste des enfants de l'objet racine. Donc, les ennemis seront affichés sur l'objet racine.
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

    /**
     * Cette méthode est responsable de mettre à jour l'état du jeu. Donc, les deplacements des tirs,
     * marquer les objets comme mort s'ils ont été touchés, Vérifie si le joueur est "mort". Si oui, déclenche une explosion du joueur, etc.
     */
    private void miseAJour()
    {
        // Augmente la valeur de la variable temps de 0.016.
        temps += 0.016;
        // Augmente la valeur de la variable tempsDeRedemarrage de 0.016.
        tempsDeRedemarrage += 0.016;

        // Appelle la méthode sprites() et exécute une action pour chaque élément (s) de la liste retournée par cette méthode.
        sprites().forEach(s ->
        {
            // Commence une instruction switch basée sur le type de l'objet 's'.
            switch (s.type)
            {
                // Si le type de l'objet 's' est "ennemiTir", le code suivant sera exécuté.
                case "ennemiTir":
                    // Appelle la méthode versLeBas sur l'objet 's'.
                    s.versLeBas();

                    //  vérifie si les limites de l'objet 's' et de l'objet Joueur se croisent.
                    if (s.getBoundsInParent().intersects(Joueur.getBoundsInParent()))
                    {
                        // Définit les attributs mort des objets Joueur et 's' sur true.
                        Joueur.mort = true;
                        s.mort = true;
                    }
                    break;

                    // Si le type de l'objet 's' est "JoueurTir", le code suivant sera exécuté.
                case "JoueurTir":
                    // Appelle la méthode "versLeHaut" sur l'objet 's'.
                    s.versLeHaut();

                    // Appelle la méthode "sprites()" et crée un flux des éléments "ennemi". Ensuite, elle exécute une action pour chaque élément (ennemi).
                    sprites().stream().filter(e -> e.type.equals("ennemi")).forEach(ennemi ->
                    {
                        // Cette ligne vérifie si les limites de l'objet 's' et de l'objet ennemi se croisent.
                        if (s.getBoundsInParent().intersects(ennemi.getBoundsInParent()))
                        {
                            // Cette partie du code définira les attributs mort des objets ennemi et 's' en "true"
                            ennemi.mort = true;
                            s.mort = true;
                            //  Appellera la méthode explosion pour faireexploser l'objet ennemi comme argument.
                            explosion(ennemi);
                        }
                    });

                    break;

                // Si le type de l'objet 's' est "ennemi", le code suivant sera exécuté.
                case "ennemi":

                    // Attend 2 secondes apres le demarrage d'une partie avant de tirer
                    if (tempsDeRedemarrage > 2 && temps > 2)
                    {
                        // Les ennemis vont tirer au hazard
                        if (Math.random() < 0.3)
                        {
                            // Appel la fonction tir pour faire tirer les ennemis
                            tir(s);
                        }
                    }
                    break;
            }
        });

        // Supprime tous les objets Sprite marqués comme "mort" de la liste des enfants de l'objet racine.
        racine.getChildren().removeIf(n -> n instanceof Sprite && ((Sprite) n).mort);


        // Si la valeur de la variable temps est supérieure à 2, ça réinitialise temps à 0
        if (temps > 2)
        {
            temps = 0;
        }

        // Vérifie si tous les ennemis sont détruits en appelant la méthode "toutLesEnnemisDetruits()". Ensuite, appel la méthode "displayVictoryMessage()".
        if (toutLesEnnemisDetruits())
        {
            displayVictoryMessage();
        }

        // Si l'attribut mort du joueur est true, une explosion est déclenchée pour le joueur
        if (Joueur.mort)
        {
            // Appelle la méthode "explosionJoueur(Joueur)" pour faire exploser le joueur
            explosionJoueur(Joueur);
            // Supprime le joueur de la liste des enfants de racine
            racine.getChildren().remove(Joueur);
            // Affiche Le message de défaite
            displayDefeatMessage();
        }
    }

    /**
     *
     */
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