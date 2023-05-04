/**
 * Projet: envahisseurs de l'espace
 * Nom programmeur: Samuel Villeneuve
 * Date: 5 mai 2023
 * Resume du fonctionnement: Un jeu envahisseur de l'espace ou le joueur doit tirer sur les ennemis
 * sans qu'il ne se fasse toucher par les tirs ennemis. Les ennemis sont représentés par des carres rouges et ils sont placer vers le haut.
 *  Le joueur est placé en bas et est représenté par une carre bleue. Lorsqu'un ennemi ou le joueur est touché il y a une image d'explosion et un son d'explosion..
 *  Il y a aussi un son pour les tirs du joueur et un son pour les tirs des ennemis. Si les ennemis sont tous détruit, un message indique au joueur
 * qu'il a gagné et peut appuyer sur "R" pour redémarrer le jeu. Tandis que si le joueur est touché, un message indique au joueur qu'il a perdu
 * et il peut appuyer sur "R" pour redémarrer la partie
 * Description des variables:
 * tempsDeRedemarrage: Cette variable garde la trace du temps écoulé depuis le dernier redémarrage du jeu afin de déterminer si assez de temps s'est écoulé pour permettre aux ennemis de tirer.
 * explosionMediaPlayer: Cette variable est utilisé pour lire les fichiers audio de l'explosion du joueur
 * tirJoueurMediaPlayer: Cette variable est utilisé pour lire les fichiers audio des tirs du joueur
 * ennemiMediaPlayer: Cette variable est utilisé pour lire les fichiers audio des tirs ennemis
 * explosionAffichee: Cette variable booléenne indique si une explosion a déjà été affichée à l'écran ou non.
 * racine: racine est un objet Pane qui sert de conteneur principal pour tous les éléments visuels du jeu
 * temps: Cette variable mesure le temps écoulé depuis le dernier mouvement des ennemis.
 * Joueur: Contient le sprite du Joueur, sa position initiale, sa taille et sa couleur.
 */


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
    // Cette variable garde la trace du temps écoulé depuis le dernier redémarrage du jeu afin de déterminer si assez de temps s'est écoulé pour permettre aux ennemis de tirer.
    public double tempsDeRedemarrage;
    // Cette variable est utilisé pour lire les fichiers audio de l'explosion du joueur
    public MediaPlayer explosionMediaPlayer;
    // Cette variable est utilisé pour lire les fichiers audio des tirs du joueur
    public MediaPlayer tirJoueurMediaPlayer;
    // Cette variable est utilisé pour lire les fichiers audio des tirs ennemis
    public MediaPlayer ennemiMediaPlayer;
    // Cette variable booléenne indique si une explosion a déjà été affichée à l'écran ou non.
    public boolean explosionAffichee = false;

    // racine est un objet Pane qui sert de conteneur principal pour tous les éléments visuels du jeu
    public Pane racine = new Pane();

    // Cette variable mesure le temps écoulé depuis le dernier mouvement des ennemis.
    public double temps = 0;

    // Contient le sprite du Joueur, sa position initiale, sa taille et sa couleur.
    public Sprite Joueur = new Sprite(300, 750, 40, 40, "Joueur", Color.BLUE);

    /**
     * Configure l'interface utilisateur et d'initialise le jeu
     * @return Retourne le Pane
     */
    public Parent creationDeContenu()
    {
        // Appelle la méthode setPrefSize sur l'objet racine et définit sa largeur et sa hauteur préférées à 1000 x 1000 pixels.
        racine.setPrefSize(1000, 1000);

        // Met l'arriere plan en noir
        racine.setStyle("-fx-background-color: black;");

        // Ajoute l'objet Joueur à la liste des enfants de l'objet racine.
        racine.getChildren().add(Joueur);

        // Appel la méthode "creerTexteInstruction()" pour afficher les instruction
        racine.getChildren().add(creerTexteInstruction());

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
     * Crée une animation d'explosion lorsqu'un ennemi est touché par un tir du joueur.
     * L'explosion est créée à la position de l'ennemi et dure un certain temps avant d'être supprimée de l'affichage.
     * De plus, un son d'explosion est joué lors de la création de l'animation d'explosion.
     * @param ennemi Le Sprite représentant l'ennemi touché par un tir du joueur.
     */
    public void explosion(Sprite ennemi)
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
        // Arrête la lecture en cours, si nécessaire
        explosionMediaPlayer.stop();
        // Remet le lecteur à zéro
        explosionMediaPlayer.seek(Duration.ZERO);
        // Joue le son d'explosion
        explosionMediaPlayer.play();
    }

    /**
     * Crée une animation d'explosion spécifique au joueur lorsqu'il est touché par un tir ennemi.
     * L'explosion est créée à la position du joueur et dure un certain temps avant d'être supprimée de l'affichage.
     * De plus, un son d'explosion est joué lors de la création de l'animation d'explosion. L'explosion ne sera
     * affichée qu'une seule fois pour éviter des superpositions d'explosions en cas de dégâts multiples.
     * @param joueur Le Sprite représentant le joueur touché par un tir ennemi.
     */
    public void explosionJoueur(Sprite joueur)
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

            // Arrête la lecture en cours, si nécessaire
            explosionMediaPlayer.stop();
            // Remet le lecteur à zéro
            explosionMediaPlayer.seek(Duration.ZERO);
            // Joue le son d'explosion
            explosionMediaPlayer.play();
        }
    }


    /**
     * Constructeur de la classe SpaceInvaders, qui sert à charger les fichiers audio et à créer les objets MediaPlayer.
     * Les fichiers audio incluent les sons de tir du joueur, des ennemis et des explosions.
     * Les objets MediaPlayer créés sont tirJoueurMediaPlayer, ennemiMediaPlayer et explosionMediaPlayer.
     */
    public SpaceInvaders() // sert a charger fichier audio et creer "tirJoueurMediaPlayer"
    {
        // Cette ligne récupère l'URL de la ressource "tirLaserJoueur.mp3" et la stocke dans la variable "audioUrl".
        URL audioUrl = getClass().getResource("/tirLaserJoueur.mp3");
        // Crée un nouvel objet Media à partir de l'URL du fichier audio et stocke cet objet dans la variable "audioMedia".
        Media audioMedia = new Media(audioUrl.toString());
        // Crée un nouvel objet MediaPlayer en utilisant "audioMedia" et l'assigne à la variable "mediaPlayer".
        tirJoueurMediaPlayer = new MediaPlayer(audioMedia);

        // Récupère l'URL de la ressource "tirLaserEnnemi.mp3" et la stocke dans la variable ennemiAudioUrl.
        URL ennemiAudioUrl = getClass().getResource("/tirLaserEnnemi.mp3");
        // Crée un nouvel objet Media à partir de l'URL du fichier audio et stocke cet objet dans la variable "ennemiAudioMedia".
        Media ennemiAudioMedia = new Media(ennemiAudioUrl.toString());
        // crée un nouvel objet MediaPlayer en utilisant ennemiAudioMedia et l'assigne à la variable ennemiMediaPlayer.
        ennemiMediaPlayer = new MediaPlayer(ennemiAudioMedia);

        // Cette ligne récupère l'URL de la ressource "sonExplosion.mp3" et la stocke dans la variable "explosionAudioUrl".
        URL explosionAudioUrl = getClass().getResource("/sonExplosion.mp3");
        // Crée un nouvel objet Media à partir de l'URL du fichier audio et stocke cet objet dans la variable "explosionAudioMedia".
        Media explosionAudioMedia = new Media(explosionAudioUrl.toString());
        // crée un nouvel objet MediaPlayer en utilisant explosionAudioMedia et l'assigne à la variable explosionMediaPlayer.
        explosionMediaPlayer = new MediaPlayer(explosionAudioMedia);
    }


    /**
     * Cette méthode est responsable de créer et d'ajouter des ennemis au niveau.
     * Elle crée 5 ennemis de couleur rouge, les positionne horizontalement à une certaine distance les uns des autres et les affiches à l'écran.
     */
    public void prochainNiveau()
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


    /**
     * Cette méthode crée un flux à partir des enfants de racine, filtre les éléments qui sont des instances de la classe Sprite,
     * les transforme en objets Sprite et collecte les éléments du flux pour les convertir en une liste de Sprite.
     * @return Renvoie une liste d'objets Sprite présents parmi les enfants de l'objet racine.
     */
    public List<Sprite> sprites()
    {
        // Obtention de la liste des enfants de l'objet racine, création d'un flux stream à partir de cette liste et renvoi le flux.
        return racine.getChildren().stream()
                // Application d'un filtre sur le flux pour ne garder que les éléments qui sont des instances de la classe Sprite.
                .filter(n -> n instanceof Sprite)
                // Transformation des éléments du flux en objets de type Sprite en utilisant une opération de mappage.
                .map(n -> (Sprite) n)
                // Collecte des éléments du flux filtré et transformé pour les convertir en une List<Sprite>.
                .collect(Collectors.toList());
    }

    /**
     * Cette méthode est responsable de mettre à jour l'état du jeu. Donc, les deplacements des tirs,
     * marquer les objets comme mort s'ils ont été touchés, Vérifie si le joueur est "mort". Si oui, déclenche une explosion du joueur, etc.
     */
    public void miseAJour()
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
     * Méthode pour afficher un message de victoire à l'écran après la réussite du joueur.
     * Cette méthode crée un message de victoire et un message pour redémarrer le jeu,
     * puis les ajoute à un conteneur VBox et les positionne au centre de l'écran.
     */
    public void displayVictoryMessage()
    {
        // Création de deux objets Text pour afficher un message de victoire et un message pour redémarrer le jeu.
        Text victoryMessage = new Text("VOUS AVEZ GAGNÉ !");
        Text restartMessage = new Text("Appuyez sur R pour recommencer.");

        // Définition de la police, de la taille et de la couleur des deux objets Text.
        victoryMessage.setFont(Font.font("Verdana", 50));
        victoryMessage.setFill(Color.GREEN);
        restartMessage.setFont(Font.font("Verdana", 50));
        restartMessage.setFill(Color.GREEN);

        // Création d'un objet "VBox" pour contenir les deux objets "Text" avec un espacement de 10 pixels entre eux.
        VBox vbox = new VBox(10, victoryMessage, restartMessage);
        // Définition de l'alignement des éléments dans la VBox au centre.
        vbox.setAlignment(Pos.CENTER);

        // Deplacer les 2 textes
        vbox.widthProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutX(racine.getPrefWidth() / 2 - newValue.doubleValue() / 2 - 10));
        vbox.heightProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutY(racine.getPrefHeight() / 2 - newValue.doubleValue() / 2));

        // Création d'un nouvel objet Pane pour contenir la VBox.
        Pane messagePane = new Pane();
        // Définition de la largeur et de la hauteur préférées du messagePane pour correspondre à celles de l'objet racine.
        messagePane.setPrefSize(racine.getPrefWidth(), racine.getPrefHeight());
        // Ajout de la VBox aux enfants de messagePane.
        messagePane.getChildren().add(vbox);
        // Positionnement initial de la VBox au centre de l'objet racine.
        vbox.setLayoutX(racine.getPrefWidth() / 2 - vbox.getPrefWidth() / 2);
        vbox.setLayoutY(racine.getPrefHeight() / 2 - vbox.getPrefHeight() / 2);

        // Ajout du messagePane aux enfants de l'objet racine.
        racine.getChildren().add(messagePane);
    }


    /**
     * Méthode pour afficher un message de défaite à l'écran après la défaite du joueur.
     * Cette méthode crée un message de défaite et un message pour redémarrer le jeu,
     * puis les ajoute à un conteneur VBox et les positionne au centre de l'écran.
     */
    public void displayDefeatMessage()
    {
        // Création de deux objets Text pour afficher un message de victoire et un message pour redémarrer le jeu.
        Text defeatMessage = new Text("VOUS AVEZ PERDU !");
        Text restartMessage = new Text("Appuyez sur R pour recommencer.");

        // // Définition de la police, de la taille et de la couleur des deux objets Text.
        defeatMessage.setFont(Font.font("Verdana", 50));
        defeatMessage.setFill(Color.RED);
        restartMessage.setFont(Font.font("Verdana", 50));
        restartMessage.setFill(Color.RED);

        // Création d'un objet "VBox" pour contenir les deux objets "Text" avec un espacement de 10 pixels entre eux.
        VBox vbox = new VBox(10, defeatMessage, restartMessage);
        // Définition de l'alignement des éléments dans la VBox au centre.
        vbox.setAlignment(Pos.CENTER);

        // Deplacer les 2 textes
        vbox.widthProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutX(racine.getPrefWidth() / 2 - newValue.doubleValue() / 2 - 10));
        vbox.heightProperty().addListener((observable, oldValue, newValue) -> vbox.setLayoutY(racine.getPrefHeight() / 2 - newValue.doubleValue() / 2));

        // Création d'un nouvel objet Pane pour contenir la VBox.
        Pane messagePane = new Pane();
        // Définition de la largeur et de la hauteur préférées du messagePane pour correspondre à celles de l'objet racine.
        messagePane.setPrefSize(racine.getPrefWidth(), racine.getPrefHeight());
        // Ajout de la VBox aux enfants de messagePane.
        messagePane.getChildren().add(vbox);
        // Positionnement initial de la VBox au centre de l'objet racine.
        vbox.setLayoutX(racine.getPrefWidth() / 2 - vbox.getPrefWidth() / 2);
        vbox.setLayoutY(racine.getPrefHeight() / 2 - vbox.getPrefHeight() / 2);

        // Ajout du messagePane aux enfants de l'objet racine.
        racine.getChildren().add(messagePane);
    }

    /**
     * Méthode pour créer un objet Text contenant les instructions de jeu.
     * Cette méthode définit la police, la taille et la couleur du texte, ainsi que sa position sur l'écran.
     * @return Text L'objet Text contenant les instructions de jeu.
     */
    public Text creerTexteInstruction()
    {
        // Création d'un objet Text avec les instructions pour jouer.
        Text instructionText = new Text("Maintenir/appuyer sur A ou D pour se déplacer - Appuyer sur Barre Espace pour tirer");
        // Définition de la police et de la taille du texte.
        instructionText.setFont(Font.font("Verdana", 23));
        // Définition de la couleur du texte.
        instructionText.setFill(Color.WHITE);
        // Position horizontale du texte
        instructionText.setX(10);
        // Position verticale du texte
        instructionText.setY(990);
        // Retourne l'objet Text créé.
        return instructionText;
    }

    /**
     * Méthode pour redémarrer le jeu en réinitialisant les éléments de l'objet racine.
     * Cette méthode supprime tous les éléments enfants de l'objet racine, crée un nouveau Sprite pour le joueur,
     * déplace le joueur et l'objet racine avec des animations, et charge le prochain niveau.
     */
    public void redemarrerJeu()
    {
        // Suppression de tous les éléments enfants de l'objet racine.
        racine.getChildren().clear();
        // Création d'un nouvel objet Sprite pour le joueur.
        Joueur = new Sprite(300, 750, 40, 40, "Joueur", Color.BLUE);

        // Création d'une TranslateTransition pour déplacer le joueur vers le haut.
        TranslateTransition transitionHaut = new TranslateTransition(Duration.seconds(1), Joueur);
        // Définition des positions de départ et d'arrivée pour la transition.
        transitionHaut.setFromY(-racine.getPrefHeight() / 2);
        transitionHaut.setToY(750);

        // Création d'une TranslateTransition pour déplacer l'objet racine vers le bas.
        TranslateTransition transitionBas = new TranslateTransition(Duration.seconds(1), racine);
        // Définition des positions de départ et d'arrivée pour la transition.
        transitionBas.setFromY(racine.getPrefHeight() / 2);
        transitionBas.setToY(0);

        // Lancement des deux transitions.
        transitionHaut.play();
        transitionBas.play();

        // Ajout de l'objet Joueur aux enfants de l'objet racine.
        racine.getChildren().add(Joueur);
        // Ajout du texte d'instruction aux enfants de l'objet racine.
        racine.getChildren().add(creerTexteInstruction());
        // Appel de la méthode prochainNiveau() pour charger le niveau suivant.
        prochainNiveau();
        // Réinitialisation de la variable explosionAffichee à false.
        explosionAffichee = false;
        // Réinitialisation de la variable tempsDeRedemarrage à 0.
        tempsDeRedemarrage = 0;
    }


    /**
     * Méthode pour créer et gérer un tir à partir d'un Sprite donné (qui).
     * Cette méthode crée un nouveau Sprite représentant un tir, ajoute le tir à l'objet racine,
     * et joue le son approprié en fonction du type de Sprite (Joueur ou Ennemi) qui effectue le tir.
     * @param qui Le Sprite (Joueur ou Ennemi) qui effectue le tir.
     */
    public void tir(Sprite qui)
    {
        // Création d'un nouvel objet Sprite s représentant un tir avec des coordonnees de depart, sa dimension et la couleur du tir
        Sprite s = new Sprite((int) qui.getTranslateX() + 20, (int) qui.getTranslateY(), 5, 20, qui.type + "Tir", Color.PURPLE);

        // Ajout de l'objet s (le tir) à la liste des enfants de l'objet racine pour l'afficher à l'écran.
        racine.getChildren().add(s);

        // Vérification si l'objet qui est de type "Joueur".
        if (qui.type.equals("Joueur"))
        {
            // Arrête la lecture en cours, si nécessaire
            tirJoueurMediaPlayer.stop();
            // Remet le lecteur à zéro
            tirJoueurMediaPlayer.seek(Duration.ZERO);
            // Joue le son de tir de laser
            tirJoueurMediaPlayer.play();
        }
        // Sinon, vérification si l'objet qui est de type "ennemi".
        else if (qui.type.equals("ennemi"))
        {
            // Arrête la lecture en cours, si nécessaire
            ennemiMediaPlayer.stop();
            // Remet le lecteur à zéro
            ennemiMediaPlayer.seek(Duration.ZERO);
            // Joue le son de tir de laser ennemi
            ennemiMediaPlayer.play();
        }
    }

    /**
     * Méthode d'initialisation de l'application JavaFX.
     * Cette méthode crée et configure la scène, ajoute un écouteur d'événements pour les touches pressées,
     * et démarre l'affichage du stage.
     * @param stage Le stage principal de l'application JavaFX.
     * @throws Exception Si une exception se produit lors de l'initialisation.
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        // Crée une nouvelle scène avec le contenu créé par la méthode creationDeContenu()
        Scene scene = new Scene(creationDeContenu());

        // Ajoute un écouteur d'événements pour les touches pressées sur la scène
        scene.setOnKeyPressed(e ->
        {
            // Vérifie si le joueur n'est pas mort
            if (!Joueur.mort)
            {
                // Exécute le code correspondant à la touche pressée
                switch (e.getCode())
                {
                    case A:
                        // Déplace le joueur vers la gauche
                        Joueur.versLaGauche();
                        break;
                    case D:
                        // Déplace le joueur vers la droite
                        Joueur.versLaDroite();
                        break;
                    case SPACE:
                        // Fait tirer le joueur
                        tir(Joueur);
                        break;
                    case R:
                        // Redémarre le jeu
                        redemarrerJeu();
                        break;
                }
                // Si le joueur est mort et la touche R est pressée
            } else if (e.getCode() == KeyCode.R)
            {
                // Redémarre le jeu
                redemarrerJeu();
            }
        });

        // Définit la scène pour le stage
        stage.setScene(scene);
        // Affiche le stage
        stage.show();
    }


    /**
     * Vérifie si tous les ennemis ont été détruits.
     * @return Retourne true si tous les ennemis ont été détruits, sinon false.
     */
    public boolean toutLesEnnemisDetruits()
    {
        // Utilise un stream pour vérifier si aucun élément de type "ennemi" n'existe dans la liste des sprites
        return sprites().stream().noneMatch(s -> s.type.equals("ennemi"));
    }


    /**
     * Classe Sprite représentant un élément graphique dans le jeu, tel qu'un ennemi, un joueur ou un tir.
     * Un Sprite est composé d'un Rectangle et d'une ImageView (pour afficher une image) et peut être déplacé dans différentes directions.
     */
    public static class Sprite extends Group
    {
        // Rectangle représentant la forme du sprite
        Rectangle rectangle;
        // Indique si le sprite est mort ou non
        boolean mort = false;
        // Type du sprite (ex: "ennemi", "Joueur")
        final String type;
        // Vue d'image pour afficher une image associée au sprite
        ImageView imageView;

        /**
         * Constructeur pour créer un Sprite avec une couleur unie.
         * @param x La position initiale en x du Sprite.
         * @param y La position initiale en y du Sprite.
         * @param w La largeur du Sprite.
         * @param h La hauteur du Sprite.
         * @param type Le type du Sprite (ex: "ennemi", "Joueur").
         * @param couleur La couleur du Sprite.
         */
        Sprite(int x, int y, int w, int h, String type, Color couleur)
        {
            // Crée un rectangle de la couleur spécifiée
            rectangle = new Rectangle(w, h, couleur);
            // Initialise le type
            this.type = type;
            // Initialise la position x
            setTranslateX(x);
            // Initialise la position y
            setTranslateY(y);
            // Ajoute le rectangle aux enfants du groupe
            getChildren().add(rectangle);
        }

        /**
         * Constructeur pour créer un Sprite avec une image.
         * @param x La position initiale en x du Sprite.
         * @param y La position initiale en y du Sprite.
         * @param w La largeur du Sprite.
         * @param h La hauteur du Sprite.
         * @param type Le type du Sprite (ex: "ennemi", "Joueur").
         * @param image L'image du Sprite.
         */
        Sprite(int x, int y, int w, int h, String type, Image image)
        {
            // Crée un rectangle vide
            rectangle = new Rectangle(w, h);
            // Initialise le type
            this.type = type;
            // Initialise la position x
            setTranslateX(x);
            // Initialise la position y
            setTranslateY(y);

            // Crée une ImageView pour l'image
            imageView = new ImageView(image);
            // Ajuste la largeur de l'image
            imageView.setFitWidth(w);
            // Ajuste la hauteur de l'image
            imageView.setFitHeight(h);
            // Initialise la position x de l'image
            imageView.setTranslateX(x);
            // Initialise la position y de l'image
            imageView.setTranslateY(y);
            // Ajoute le rectangle et l'image aux enfants du groupe
            getChildren().addAll(rectangle, imageView);
        }

        /**
         * Déplace le Sprite de 5 unités vers la gauche.
         */
        public void versLaGauche()
        {
            setTranslateX(getTranslateX() - 5);
        }

        /**
         * Déplace le Sprite de 5 unités vers la droite.
         */
        public void versLaDroite()
        {
            setTranslateX(getTranslateX() + 5);
        }

        /**
         * Déplace le Sprite de 5 unités vers le haut.
         */
        public void versLeHaut()
        {
            setTranslateY(getTranslateY() - 5);
        }

        /**
         * Déplace le Sprite de 5 unités vers le bas.
         */
        public void versLeBas()
        {
            setTranslateY(getTranslateY() + 5);
        }
    }

    /**
     * Méthode principale pour lancer l'application JavaFX.
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args)
    {
        launch(args);
    }

}