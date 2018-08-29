package main;

import sprites.shots.Shot;
import sprites.Projectile;
import sprites.enemies.*;
import cameras.Camera;
import java.util.*;
import javafx.animation.*;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.transform.*;
import javafx.stage.*;
import javafx.util.Duration;
import sprites.*;
import sprites.awards.*;
import sprites.shots.Boomerang;
import sprites.shots.Stream;

public class Main extends Application {    
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;
    
    public static final int ENEMIES_IN_A_ROW = 8;
    public static final int ENEMIES_IN_A_COLUMN = 4;
    
    private static final int HARD = 5;
    
    private static final String GAME = "Svemirci";

    private Background background;
    private static Player player;
    private static List<Enemy> enemies;
    private static List<Enemy> shotEnemies = new ArrayList<>();
    private List<Shot> shots;
    
    public static Camera camera;
    
    private Group root;
    private double time = 0;
    private boolean theEnd = false;
    
    private int time_passed = 0;
    private String time_msg = "Time: ";
    private Text time_text;
    
    private static int points = 0;
    private String end_msg = " POINTS WON";
    private int lives = Background.LIVES_CNT;
    private String life_msg = "LIFE LOST, %d REMAIN";
    private Text end_text;    
    private static List<Coin> coins = new ArrayList<>();
    
    private static List<Bonus> bonuses = new ArrayList<>();
    private static Bonus collectedRed = null;
    private static List<Bonus> collectedYellow = new ArrayList<>();
        
    private boolean rst = false; //random shoot time
    private static List<Projectile> projs = new ArrayList<>();
    
    private static String points_msg = " Points: ";
    private static Text points_text;
    
    private static List<Sprite> delObjects = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) {
        enemies = new LinkedList<>();
        root = new Group();
        camera = new Camera();
        
        background = new Background(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(background);
        
        displayTime();        
        displayPoints();
        
        player = new Player();
        player.setTranslateX(WINDOW_WIDTH / 2);
        player.setTranslateY(WINDOW_HEIGHT * 0.95);
        camera.getChildren().add(player);
        
        for (int i = 0; i < ENEMIES_IN_A_COLUMN; i++) 
            for (int j = 0; j < ENEMIES_IN_A_ROW; j++) {
                Enemy enemy;
                if (i + j % 3 == 1)
                    enemy = new Warrior();
                else
                    if (i + j % 3 == 2)
                        enemy = new Commander();
                    else
                        enemy = new Scout();
                enemy.setTranslateX((j+1) * WINDOW_WIDTH / (ENEMIES_IN_A_ROW + 1));
                Timeline arrival = new Timeline(
                        new KeyFrame(Duration.ZERO, 
                                new KeyValue(enemy.translateYProperty(), -((ENEMIES_IN_A_ROW - i-1)*100), Interpolator.EASE_OUT)), //ENEMIES_SPACE = 100
                        new KeyFrame(Duration.seconds(4), 
                                t->{
                                    enemy.setUpdate(true);
                                },
                                new KeyValue(enemy.translateYProperty(), (i+1) * 100, Interpolator.EASE_BOTH))//ENEMIES_SPACE = 100
                        
                        
                );
                arrival.play();
                enemy.showBar(camera);
                camera.getChildren().add(enemy);
                
                //blinking
                ScaleTransition st;
                if ((i + j) % 2 == 0){
                   st = new ScaleTransition(Duration.seconds(2), enemy.getLeftEye()); 
                }else{
                   st = new ScaleTransition(Duration.seconds(2), enemy.getRightEye());
                }
                st.setFromY(1);
                st.setToY(0.2);
                st.setAutoReverse(true);
                st.setCycleCount(Animation.INDEFINITE);
                st.play();                
                enemies.add(enemy);
            }
        
        root.getChildren().add(camera);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(player);
        scene.setOnKeyReleased(player);
        
        primaryStage.setTitle(GAME);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {                
                update();
                if (time_passed < (int)time){
                    time_passed++;
                    time_text.setText(time_msg + time_passed);
                    if (Math.random() < 0.15){
                        rst = true;
                    }
                }
            }
        }.start();
    }
    
    public void resetPlayer(){
        player.reset();
        player.setTranslateX(WINDOW_WIDTH / 2);
        player.setTranslateY(WINDOW_HEIGHT * 0.95);
        Timeline playerAnotherShot = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(player.opacityProperty(), 0, Interpolator.DISCRETE)),
                new KeyFrame(Duration.seconds(1), new KeyValue(player.opacityProperty(), 1, Interpolator.DISCRETE))
        );
        playerAnotherShot.play();
        
    }
    
    public void updatePlayer(){
        if (!player.invincible()){
            lives--;
            if (!background.loseLife()){
                end_text = new Text(WINDOW_WIDTH/2 - 100, WINDOW_HEIGHT/2 - 30, String.format(life_msg, lives));
                end_text.setFill(Color.ORANGERED);
                end_text.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 30));
                root.getChildren().add(end_text);
                FadeTransition ft = new FadeTransition(Duration.seconds(2), end_text);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.play();
                resetPlayer();
            }  
        }
    }
    
    public void update() {
        if (theEnd == false) {
            
            camera.getChildren().clear();  
            
            //enemy player update
            for(int i = 0; i < enemies.size(); i++){
                Enemy enemy = enemies.get(i);
                if (enemy.getBoundsInParent().intersects(player.getBoundsInParent())){ 
                    updatePlayer();
                } 
            }          
             
            if (enemies.isEmpty() || (lives == 0)) {
                theEnd = true;
                camera.getChildren().clear();
                camera.getChildren().addAll(shots);
                camera.getChildren().addAll(enemies);
                camera.getChildren().addAll(coins);
                camera.getChildren().addAll(projs);
                end_text = new Text(WINDOW_WIDTH/2 - 100, WINDOW_HEIGHT/2 - 30, points + end_msg);
                end_text.setFill(Color.ORANGERED);
                end_text.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 30));
                root.getChildren().add(end_text);
            }else {  
                //random enemy shooting
                if (rst){
                    int randEnemy = (int)(Math.random() * (enemies.size() - 1));
                    Enemy e = enemies.get(randEnemy);
                    double x = e.getTranslateX() + e.getBody().getWidth()/2;
                    double y = e.getTranslateY() + e.getBody().getHeight()/2;
                    projs.add(new Projectile(x, y));
                    rst = false;
                }                  
                
                //display game objects
                //player
                camera.getChildren().add(player);
                
                //enemy and shots update
                shots = player.getShots(); 
                shots.forEach(s -> {              
                    if (!(s instanceof Boomerang) && (s.getTranslateY() < 50)) {
                        if (s instanceof Stream){
                            enemies.forEach(e -> e.setRedMark(false));
                            collectedRed = null;
                        }
                        Main.removeSprite(s);
                        return;
                    }               
                    for (int j = 0; j < enemies.size(); j++) {
                        Enemy currentEnemy = enemies.get(j);
                        if (s.getBoundsInParent().intersects(currentEnemy.getBoundsInParent())) {
                            if (s instanceof Stream || s instanceof Boomerang){
                                if (currentEnemy.isRedMark()){
                                    currentEnemy.enemyShot();
                                    currentEnemy.setRedMark(false);
                                }
                            }else{
                                Main.removeSprite(s);
                                currentEnemy.enemyShot();
                            }                                
                            break;
                        }
                    }
                });
                shots.removeAll(delObjects);
                shots.forEach(e -> e.update());
                camera.getChildren().addAll(shots);                
                
                //coins                
                coins.forEach(c -> {
                    c.update();
                    if (c.getBoundsInParent().intersects(player.getBoundsInParent())){
                        points++; points_text.setText(points_msg + points); Main.removeSprite(c);
                    }                    
                });
                coins.removeAll(delObjects);
                camera.getChildren().addAll(coins);
                
                //enemies                 
                camera.getChildren().addAll(shotEnemies);
                camera.getChildren().addAll(enemies);  
                enemies.forEach(e -> {e.update(); e.showBar(camera);});
                
                //projectiles                                
                projs.forEach(p -> {
                    p.update();
                    if (p.getBoundsInParent().intersects(player.getBoundsInParent()))
                            updatePlayer();
                });
                projs.removeAll(delObjects);
                camera.getChildren().addAll(projs);
                
                //bonuses        
                for(int i = 0; i < bonuses.size(); i++){
                    Bonus bonus = bonuses.get(i);
                    bonus.update();
                    if (bonus.getBoundsInParent().intersects(player.getBoundsInParent())){
                        bonus.consumed();
                        bonuses.remove(bonus);
                        break;
                    }
                }
                camera.getChildren().addAll(bonuses);
                
                camera.updateCamera(player);
                player.setShots(shots);
                player.update();
                background.update();
                time += 1.0 / 60;
            }   

        }
    }
    
    public static void removeSprite(Sprite sprite){
        delObjects.add(sprite);
    }
    
    public static void destroyEnemy(Enemy enemy){
        enemies.remove(enemy);
        shotEnemies.add(enemy);                        
        Rotate rot = new Rotate();
        enemy.getTransforms().add(rot);
        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(rot.angleProperty(), 0)),
                new KeyFrame(Duration.seconds(1),
                        t -> {
                            double x = enemy.getTranslateX() + enemy.getBody().getWidth()/2;
                            double y = enemy.getTranslateY() + enemy.getBody().getHeight()/2;
                            shotEnemies.remove(enemy);
                            camera.getChildren().remove(enemy);
                            double rand = Math.random();
                            points += enemy.enemyStrength()/HARD;// points won from kill shot
                            points_text.setText(points_msg + points);
//                            if (rand < 0.6){
//                                if (rand < 0.1){
                                    //bonuses.add(new Bonus(Bonus.pickBonus(), x, y));
                                    Bonus bonus = new Bonus(Bonus.RedBonus.Stream, x, y);
                                    bonuses.add(bonus);
                                    if (bonus.getBonusType() instanceof Bonus.RedBonus)
                                        collectedRed = bonus;
                                    else
                                        if (bonus.getBonusType() instanceof Bonus.YellowBonus)
                                            collectedYellow.add(bonus);
//                                }else
//                                    coins.add(new Coin(x, y));                                          
//                            }
                        },
                        new KeyValue(rot.angleProperty(), 360))
        );
        tl.play();
    }
    
    public static void setEnemyRedMark(){
        enemies.forEach(e -> e.setRedMark(true));
    }
    
    
    private void displayTime() {
        time_text = new Text(WINDOW_WIDTH/2 - 15, 20, time_msg + time_passed);
        time_text.setFill(Color.RED);
        time_text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13));
        root.getChildren().add(time_text); 
    }
    
    
    public void displayPoints(){
        points_text = new Text(5, Life.getHeght()*2 + 20, points_msg + points);
        points_text.setFill(Color.RED);
        points_text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13));
        root.getChildren().add(points_text); 
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    
}
