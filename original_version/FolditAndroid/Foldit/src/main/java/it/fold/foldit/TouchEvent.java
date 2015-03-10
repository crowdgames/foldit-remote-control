package it.fold.foldit;

/**
 * Created by jeffpyke on 7/29/13.
 */
// Deprecated way to represent a touch action
public class TouchEvent {
    private int x;
    private int y;
    private int type;
    private int customType;

    //private float scale;
    public TouchEvent(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public TouchEvent(int customType, int key) {
        this.customType = customType;
        this.x = key;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getType() {
        return type;
    }
    public int getCustomType() {
        return customType;
    }
    public boolean isCustom() {
        return customType != 0;
    }

}
