import java.awt.*;
import java.util.List;

/**
 * Created by Dima Stoyanov.
 */



public class Action {

    enum Type {
        MOVE, BACKUP
    }

    Type type;
    Point knightPoint = null;
    List<Point> backupPoints = null;

    Action(Point movePoint){
        this.knightPoint = movePoint;
        type = Type.MOVE;
    }

    Action(List<Point> backupPoints, Point knightPoint){
        this.backupPoints = backupPoints;
        this.knightPoint = knightPoint;
        type = Type.BACKUP;
    }


}
