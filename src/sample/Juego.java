package sample;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//
public class Juego extends Application implements EventHandler<KeyEvent> {
    public static int ALTO_MENU = 30; // Margen vertical para el menú (se deja abajo)
    public static int ANCHO_PANTALLA = 500;
    public static int ALTO_PANTALLA = 300;
    public static int ANCHO = 10; // Ancho de la serpiente
    public static int RETARDO_ANIMADOR = 100000000; // Controla la velocidad del juego

    public enum Direccion {
        Up, Down, Left, Right
    };

    private Direccion dir = Direccion.Up; // Dirección de la serpiente
    private double cabezaX = ANCHO_PANTALLA / 2; // Posición inicial de la serpiente 400
    private double cabezaY = ALTO_PANTALLA * 4 / 5; // Poisición inicial de la serpiente 480
    private int tamano = 2; // Tamaño inicial de la serpiente
    private Pane raiz; // Raíz de la escena JavaFX
    private ArrayList<Rectangle> trozos = new ArrayList<>();// Lista de trozos de la serpiente
    private Circle fruta; // Pues eso, la fruta.
    private Text puntos; // Texto con los puntos de la partida
    private AnimationTimer anim; // Animador del juego

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage ventana) throws Exception {


//        Parent root = FXMLLoader.load(getClass().getResource("estilo.fxml"));
//        ventana.setTitle("Tres en Raya");
//        Scene scene = new Scene(root, 600, 400);
//        ventana.setScene(scene);
//        ventana.show();


        ventana.setWidth(ANCHO_PANTALLA);
        ventana.setHeight(ALTO_PANTALLA);
        ventana.setTitle("SNAKE");
        ventana.setResizable(false);
        raiz = new Pane();
        Scene escena = new Scene(raiz);

        escena.getStylesheets().add(getClass().getResource("/sample/aplication.css").toExternalForm());
        ventana.setScene(escena);
        ponerFruta();
        puntos = new Text();
        puntos.setFont(Font.font(20));
        puntos.setFill(Color.WHITE);
        puntos.setX(10);
        puntos.setY(20);
        raiz.getChildren().add(puntos);
        ponerPuntos();

        escena.setOnKeyPressed(this);

        anim = new AnimationTimer() {
            long ultimaAnimacion = 0;

            @Override
            public void handle(long now) {
                if (ultimaAnimacion == 0) {
                    ultimaAnimacion = now;
                    return;
                }
                if (now - ultimaAnimacion < RETARDO_ANIMADOR)
                    return;

                switch (dir) {
                    case Down:
                        cabezaY += ANCHO;
                        break;
                    case Up:
                        cabezaY -= ANCHO;
                        break;
                    case Right:
                        cabezaX += ANCHO;
                        break;
                    case Left:
                        cabezaX -= ANCHO;
                        break;
                }
                // ¿Me he salido de la pantalla?
                if (cabezaX < 0)
                    cabezaX = ANCHO_PANTALLA;
                if (cabezaX > ANCHO_PANTALLA)
                    cabezaX = 0;
                if (cabezaY < 0)
                    cabezaY = ALTO_PANTALLA - ALTO_MENU;
                if (cabezaY > ALTO_PANTALLA - ALTO_MENU)
                    cabezaY = 0;

                // ¿He chocado?
                for (Rectangle r : trozos)
                    if (r.getX() == cabezaX)
                        if (r.getY() == cabezaY)
                            gameOver();

                // ¿Como Fruta?
                if (fruta.getCenterX() >= cabezaX && fruta.getCenterX() <= cabezaX + ANCHO)
                    if (fruta.getCenterY() >= cabezaY && fruta.getCenterY() <= cabezaY + ANCHO) {
                        System.out.println("Come fruta");
                        tamano++;
                        raiz.getChildren().remove(fruta);
                        ponerFruta();
                        ponerPuntos();
                    }
                // Mover a la serpiente
                Rectangle trozo = new Rectangle(ANCHO, ANCHO);
                trozo.setFill(Color.GREEN);
                trozo.setStroke(Color.NAVY);
                trozo.setX(cabezaX);
                trozo.setY(cabezaY);
                raiz.getChildren().add(trozo);
                if (!trozos.isEmpty()) {
                    if (trozos.size() >= tamano)
                        //Quitamos el último trozo
                        raiz.getChildren().remove(trozos.remove(0));
                }
                trozos.add(trozo);	//Ponemos un nuevo trozo
                ultimaAnimacion = now;
            }
        };

        ventana.show();
        anim.start();
    }

    private void ponerPuntos() {
        puntos.setText("Pts: " + (tamano - 1));
    }

    private void ponerFruta() {
        fruta = new Circle(ANCHO / 2 - 1);
        fruta.setFill(Color.LIGHTGREEN);
        double x = Math.rint(Math.random() * (ANCHO_PANTALLA - ANCHO) / ANCHO) * ANCHO + ANCHO / 2;
        double y = Math.rint(Math.random() * (ALTO_PANTALLA - ALTO_MENU - ANCHO) / ANCHO) * ANCHO + ANCHO / 2;
        // System.out.println("Fruta en " + x + ", " + y);
        fruta.setCenterX(x);
        fruta.setCenterY(y);
        raiz.getChildren().add(fruta);
    }

    @Override
    public void handle(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                if (!dir.equals(Direccion.Down))
                    dir = Direccion.Up;
                break;
            case DOWN:
                if (!dir.equals(Direccion.Up))
                    dir = Direccion.Down;
                break;
            case RIGHT:
                if (!dir.equals(Direccion.Left))
                    dir = Direccion.Right;
                break;
            case LEFT:
                if (!dir.equals(Direccion.Right))
                    dir = Direccion.Left;
                break;
            default:
        }
        // System.out.println(this.dir);
    }

    private void gameOver() {
        anim.stop();
        Text gameOver = new Text("GAME OVER");
        gameOver.setFont(Font.font(35));
        gameOver.setFill(Color.WHITE);
        gameOver.setStroke(Color.RED);
        gameOver.setY(ALTO_PANTALLA / 2);
        gameOver.setX(ANCHO_PANTALLA / 2 - 125);
        raiz.getChildren().add(gameOver);
    }

}