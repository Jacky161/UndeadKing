import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Health potion that makes the player invincible
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class HealthPotion extends Powerup
{
    public HealthPotion() {
        super("health");
        ExtendedActor.scaleImage(getImage(), 2.5);
    }
}
