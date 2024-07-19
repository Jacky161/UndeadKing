import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Bullets are shot by the player, dissapear after a certain range.
 * They damage enemies by their damage value (based on gun shot from).
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Bullet extends ExtendedActor
{
    private final int RANGE;
    private final int DAMAGE;
    private int[] startPos = new int[2];
    private GreenfootSound undeadDie;
    
    
    /** Constructor for a default bullet */
    public Bullet(int startX, int startY, int facingDirection, double movementSpeed) {
        super(facingDirection, movementSpeed);
        RANGE = 350;
        startPos[0] = startX;
        startPos[1] = startY;
        DAMAGE = 1;
        undeadDie = new GreenfootSound("undead_die.wav");
        undeadDie.setVolume(85);
        
        // Set the orientation of the bullet
        switch (facingDirection) {
            case 0:
                turn(270);
                break;
            case 1:
                break;
            case 2:
                turn(180);
                break;
            case 3:
                turn(90);
                break;
        }
        
        scaleImage(getImage(), 3);
    }
    
    /**
     * Bullet moves, checks collision with undead, removes itself after running out of range or is at edge.
     */
    public void act() {
        // Handle pausing
        UndeadWorld world = (UndeadWorld) getWorld();
        if (world.getGameState() == 1) return;
        
        move();
        checkCollision();
        
        try {
            if (isAtEdge() || Math.abs(startPos[0] - getX()) > RANGE || Math.abs(startPos[1] - getY()) > RANGE || isTouching(Fence.class)) {
                getWorld().removeObject(this);
            }
        } catch (Exception e) {
            
        }
        
    }
    
    /** Checks for collision with undead and removes them */
    private void checkCollision() {
        Undead undead = (Undead) getOneIntersectingObject(Undead.class);
        
        if (undead == null) return;
        
        UndeadWorld world = (UndeadWorld) getWorld();
        world.removeObject(undead);
        world.getScore().increment(10);
        undeadDie.play();
        world.removeObject(this);
    }
}
