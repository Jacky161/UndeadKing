import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * In game fence object.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Fence extends Actor {
    GreenfootImage fenceImg;
    
    public Fence() {
        this(0);
    }
    
    /** Constructor for fence, scales image and turns it to face the proper direction */
    public Fence(int turnNum) {
        fenceImg = getImage();
        ExtendedActor.scaleImage(fenceImg, 1.3);
        turn(turnNum);
    }
    
    /** Gets the InternalTile that the fence is colliding with */
    public InternalTile getIntersectingTile() {
        InternalTile tile = (InternalTile) getOneIntersectingObject(InternalTile.class);
        return tile;
    }
}
