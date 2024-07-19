import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Health class that manages the player's health.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Health extends Actor
{
    /** Internal Variables */
    private Counter healthCount = new Counter(6, 0, 6, 10, false, false);
    private GreenfootImage[] healthSprites = new GreenfootImage[7];
    private GreenfootImage powerupSprite;
    private int regenTime = 0;
    private boolean powerup = false;
    
    /** Constructor for Health Tracker */
    public Health() {
        // Prepare Health Sprites
        prepareImages();
    }
    
    /** Import all images for health */
    private void prepareImages() {
        int index = 0;
        for (double i = 0.0; i < 3.1; i += 0.5) {
            healthSprites[index] = new GreenfootImage("heart" + i + ".png");
            index++; 
        }
        powerupSprite = new GreenfootImage("heartPowerup.png");
        
        setImage(healthSprites[6]);
    }
    
    /**
     * Act - For health, we regenerate after enough time has passed
     */
    public void act() {
        // Handle pausing
        UndeadWorld world = (UndeadWorld) getWorld();
        if (world.getGameState() == 1) return;
        
        regenTime++;
        healthCount.act();
        
        if (regenTime > 120 || powerup) {
            healthCount.increment();
            updateImage();
        }
    }
    
    /** Decrements health by 1 */
    public void decrement() {
        regenTime = 0;
        healthCount.decrement();
        updateImage();
    }
    
    /** Powerup makes it so health increments every frame (invincible basically) */
    public void powerup() {
        powerup = powerup ? false : true;
    }
    
    /** Gets the current value of health */
    public int getValue() {
        return healthCount.getValue();
    }
    
    /** Updates the image displayed on screen */
    private void updateImage() {
        if (powerup) {
            setImage(powerupSprite);
            return;
        }
        
        setImage(healthSprites[healthCount.getValue()]);
    }
}
