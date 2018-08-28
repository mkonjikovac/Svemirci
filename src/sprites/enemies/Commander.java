package sprites.enemies;

import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import static sprites.enemies.Enemy.*;

public class Commander extends Enemy{

    private static final double HELMET_LINE = EYE_WIDTH*3/2;
    
    private Path helmet;
    private Path holder;
    private Path crown;
    
    public Commander(){
        helmet = new Path(
            new MoveTo(-EN_WIDTH/2, EN_HEIGHT/2),
            new LineTo(-HELMET_LINE*3/2, EN_HEIGHT),
            new VLineTo(0),
            new LineTo(-HELMET_LINE*5/2, -HELMET_LINE),
            new LineTo(-HELMET_LINE*3/2, -HELMET_LINE*2),
            new LineTo(-HELMET_LINE/3, 0),
            new HLineTo(HELMET_LINE/3),
            new LineTo(HELMET_LINE*3/2, -HELMET_LINE*2),
            new LineTo(HELMET_LINE*5/2, -HELMET_LINE),
            new LineTo(HELMET_LINE*3/2 , 0),
            new VLineTo(EN_HEIGHT),
            new LineTo(EN_WIDTH/2, EN_HEIGHT/2),
            new ArcTo(EN_WIDTH*2/3, EN_HEIGHT*5/6, 270, -EN_WIDTH/2, EN_HEIGHT/2, true, false)
        );
        helmet.setFill(new ImagePattern(new Image("/resources/enemy/golden_armor.png")));
        
        holder = new Path(
            new MoveTo(-HELMET_LINE/2, -EN_HEIGHT*4/5),
            new LineTo(-HELMET_LINE, -EN_HEIGHT*5/6 - HELMET_LINE),
            new HLineTo(HELMET_LINE),
            new LineTo(HELMET_LINE/2, -EN_HEIGHT*4/5),
            new ClosePath()                
        );
        holder.setFill(new ImagePattern(new Image("/resources/enemy/golden_armor.png"))); //dark grey
        
        crown = new Path(
            new MoveTo(-EN_WIDTH/2, -EN_HEIGHT*5/6),
            new LineTo(-EN_WIDTH*3/4, -EN_HEIGHT),
            new ArcTo(EN_WIDTH*10/11, EN_HEIGHT*1.2, 120, EN_WIDTH*3/4, -EN_HEIGHT, false, true),
            new LineTo(EN_WIDTH/2, -EN_HEIGHT*5/6),
            new ArcTo(EN_WIDTH*5/12, EN_HEIGHT/8, 180, -EN_WIDTH/2, -EN_HEIGHT*5/6, false, false)
        );
        crown.setFill(new ImagePattern(new Image("/resources/enemy/red_feathers.png")));
        
        for(Path e: ears)
            e.setFill(new ImagePattern(new Image("/resources/enemy/red_feathers.png")));
        getChildren().addAll(helmet, holder, crown);
    }

    @Override
    public void update() {
        super.update();
    }
    
}