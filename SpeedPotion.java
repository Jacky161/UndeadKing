import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Speed potion that makes the player faster temporarily
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class SpeedPotion extends Powerup
{
    public SpeedPotion() {
        super("speed");
        ExtendedActor.scaleImage(getImage(), 2.5);
    }
}
