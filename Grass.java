import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Grass exists in the world as decorative objects
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Grass extends Actor
{
    /** Constructor for grass. Picks a random grass sprite to use */
    public Grass() {
        int grassRand = Greenfoot.getRandomNumber(4) + 1;
        
        setImage("grass/grass" + grassRand + ".png");
    }
    
    /** Checks grass for any spawn range issues (called when grass is created) */
    public boolean checkRange() {
        // Can't spawn too close to fences
        if (getObjectsInRange(50, Fence.class).size() > 0) {
            return true;
        }
        
        // Try not to spawn too close to each other
        if (getObjectsInRange(5, Grass.class).size() > 0) {
            return true;
        }
        return false;
    }
}