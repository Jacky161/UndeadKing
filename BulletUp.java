import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Bullet powerup that lets the player fire in all 4 directions at once
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class BulletUp extends Powerup
{
    public BulletUp() {
        super("bullet");
        ExtendedActor.scaleImage(getImage(), 2);
    }
}
