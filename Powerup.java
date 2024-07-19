import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Subclass that contains all powerups
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Powerup extends Actor
{
    /** Internal Variables */
    private String powerup;
    private int lifespan = 300;
    
    /** Constructor for the powerup */
    public Powerup(String powerup) {
        this.powerup = powerup;
    }
    
    /**
     * Act - Remove the powerup after being on screen for too long
     */
    public void act()
    {
        // Handle pausing
        UndeadWorld world = (UndeadWorld) getWorld();
        if (world.getGameState() == 1) return;
        
        lifespan--;
        
        if (lifespan <= 0) {
            getWorld().removeObject(this);
        }
    }
    
    /** Get what powerup this object is */
    public String getPowerup() {
        return powerup;
    }
}
