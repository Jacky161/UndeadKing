import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * InternalTile mainly used for pathfinding for Undead. By itself, it does not do much.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class InternalTile extends Actor
{
    /** Internal Variables */
    private final int X;
    private final int Y;
    
    /** Constructor for InternalTile. */
    public InternalTile(boolean debug, int x, int y) {
        this.X = x;
        this.Y = y;
        
        // Debug mode leaves tiles visible in the world
        if (debug) return;
        setImage("InternalTileTransparent.png");
    }
    
    /** Act method to ensure the tile cannot be dragged and moved while game is not running. */
    public void act() {
        if (getX() != X || getY() != Y) setLocation(X, Y);
    }
}
