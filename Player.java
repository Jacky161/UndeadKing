import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * This is you. The player. Your job is to kill the Undead and survive as
 * long as possible.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Player extends ExtendedActor
{
    /** Internal Variables **/
    private final double SCALE_FACTOR = 2.5;
    private GreenfootImage[] sprites = new GreenfootImage[4];
    private GreenfootImage[] forwardAnim = new GreenfootImage[6];
    private GreenfootImage[] rightAnim = new GreenfootImage[6];
    private GreenfootImage[] leftAnim = new GreenfootImage[6];
    private GreenfootImage[] backwardAnim = new GreenfootImage[6];
    private Counter animIndex = new Counter(0, 0, forwardAnim.length - 1, 5);
    private int[] lastFramePos = new int[2];
    private ArrayList<String> keyActOrder = new ArrayList<String>();
    GreenfootSound gunshot = new GreenfootSound("gunshot.wav");
    
    /** Player Variables **/
    private Timer bulletTimer = new Timer(20);
    private double bulletSpeed = 7.0;
    private Timer footstepTimer = new Timer(10);
    private Health health;
    
    /** Powerup Variables */
    private String activePowerup = "";
    private boolean powerupApplied = false;
    private int powerupTimer;
    private final double MOVEMENT_BUFF = 1.5;
    
    /** Creates the player with proper sprites */
    public Player() {
        super(0, 3.5);
        
        // Prepare sprites
        prepareImages();
        
        // Set sprite
        updateImage();
        
        // Set sound volume
        gunshot.setVolume(95);
    }
    
    /**
     * Act - do whatever the Player wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        // Handle pausing
        UndeadWorld world = (UndeadWorld) getWorld();
        if (world.getGameState() == 1) return;
        
        animIndex.act();
        footstepTimer.incrementCycles();
        
        updateDirection();
        updateImage();
        lastFramePos[0] = getX();
        lastFramePos[1] = getY();
        if (getIsMoving()) {
            move();
            if (footstepTimer.timePassed()) {
                GreenfootSound footstep = new GreenfootSound("footstep.wav");
                footstep.setVolume(90);
                footstep.play();
            }
        }
        updateCoordinates();
        shoot();
        checkCollision();
        
        // Apply powerup
        applyPowerup();
        
        // System.out.println("Health: " + health.getValue());
        checkDeath();
    }
    
    /** Initializes the player's health (should only run once, executed by world constructor) */
    public void initHealth() {
        // Add player health
        health = new Health();
        getWorld().addObject(health, 60, 20);
    }
    
    /** Prepare all sprite data for the player */
    private void prepareImages() {
        // Import all player sprites
        sprites[0] = new GreenfootImage("playerForward.png");
        sprites[1] = new GreenfootImage("playerRight.png");
        sprites[2] = new GreenfootImage("playerRight.png");
        sprites[3] = new GreenfootImage("playerBackward.png");
        // Import Animation Sprites
        for (int i = 0; i < forwardAnim.length; i++) {
            forwardAnim[i] = new GreenfootImage("movingAnimations/playerForward" + (i + 1) + ".png");
            rightAnim[i] = new GreenfootImage("movingAnimations/playerRight" + (i + 1) + ".png");
            leftAnim[i] = new GreenfootImage("movingAnimations/playerRight" + (i + 1) + ".png");
            leftAnim[i].mirrorHorizontally();
            backwardAnim[i] = new GreenfootImage("movingAnimations/playerBackward" + (i + 1) + ".png");
        }
        
        // Flip image index 2 to become facing left
        sprites[2].mirrorHorizontally();
        
        // Scale up all sprites
        for (int i = 0; i < sprites.length; i++) {
            scaleImage(sprites[i], SCALE_FACTOR);
        }
        for (int i = 0; i < forwardAnim.length; i++) {
            scaleImage(forwardAnim[i], SCALE_FACTOR);
            scaleImage(rightAnim[i], SCALE_FACTOR);
            scaleImage(leftAnim[i], SCALE_FACTOR);
            scaleImage(backwardAnim[i], SCALE_FACTOR);
        }
    }
    
    /** Updates the direction the player is facing based on keyboard */
    private void updateDirection() {
        if (Greenfoot.isKeyDown("w")) {
            if (!keyActOrder.contains("w")) keyActOrder.add("w");
        } else keyActOrder.remove("w");
        if (Greenfoot.isKeyDown("a")) {
            if (!keyActOrder.contains("a")) keyActOrder.add("a");
        } else keyActOrder.remove("a");
        if (Greenfoot.isKeyDown("s")) {
            if (!keyActOrder.contains("s")) keyActOrder.add("s");
        } else keyActOrder.remove("s");
        if (Greenfoot.isKeyDown("d")) {
            if (!keyActOrder.contains("d")) keyActOrder.add("d");
        } else keyActOrder.remove("d");
        if (keyActOrder.size() == 0) {
            setIsMoving(false);
            return;
        }
        setIsMoving(true);
        
        switch (keyActOrder.get(keyActOrder.size() - 1)) {
            case "w":
                setFacingDirection('b');
                break;
            case "a":
                setFacingDirection('l');
                break;
            case "s":
                setFacingDirection('f');
                break;
            case "d":
                setFacingDirection('r');
                break;
        }
    }
    
    /** Update to a standing still image or animated movement */
    private void updateImage() {
        // If not moving, we only need to set the standing still image
        if (!getIsMoving()) {
            setImage(sprites[getFacingDirection()]);
            animIndex.setValue(0);
            return;
        }
        
        // If moving, start animating
        switch (getFacingDirection()) {
            case 0: // Forward
                setImage(forwardAnim[animIndex.getValue()]);
                break;
            case 1: // Right
                setImage(rightAnim[animIndex.getValue()]);
                break;
            case 2: // Left
                setImage(leftAnim[animIndex.getValue()]);
                break;
            case 3: // Backward
                setImage(backwardAnim[animIndex.getValue()]);
                break;
        }
        animIndex.increment();
    }
    
    /** Fires a bullet in the same direction the player is facing */
    private void shoot() {
        bulletTimer.incrementCycles();
        if (!Greenfoot.isKeyDown("space")) return;
        
        if (!bulletTimer.timePassed()) return;
        
        createBullet(getFacingDirection());
        gunshot.play();
    }
    
    /** Create a bullet at the coordinates and direction */
    private void createBullet(int direction) {
        int bulletSpawnX = getX();
        int bulletSpawnY = getY();
        
        switch (direction) {
            case 0:
                bulletSpawnY += 30;
                bulletSpawnX -= 10;
                break;
            case 1:
                bulletSpawnX += 30;
                bulletSpawnY += 10;
                break;
            case 2:
                bulletSpawnX -= 30;
                bulletSpawnY += 10;
                break;
            case 3:
                bulletSpawnY -= 30;
                bulletSpawnX += 10;
                break;
        }
        
        UndeadWorld world = (UndeadWorld) getWorld();
        world.addObject(new Bullet(getX(), getY(), direction, bulletSpeed), bulletSpawnX, bulletSpawnY);
    }
    
    /** Checks for collision */
    private void checkCollision() {
        GreenfootImage lastImage = getImage();
        setImage(sprites[getFacingDirection()]); // Ensure a consistant hitbox
        
        // Move back to the last position if touching fence
        if (isTouching(Fence.class)) {
            setLocation(lastFramePos[0], lastFramePos[1]);
        }
        
        // Add powerup buff if touching powerup
        if (isTouching(Powerup.class)) {
            addPowerup();
        }
        
        Undead undead = (Undead) getOneIntersectingObject(Undead.class);

        setImage(lastImage);
        if (undead == null) return;
        
        health.decrement();
    }
    
    /** Adds a powerup to the player */
    private void addPowerup() {
        // Get one intersecting powerup
        Powerup powerup = (Powerup) getOneIntersectingObject(Powerup.class);
        String type = powerup.getPowerup();
        activePowerup = type;
        
        getWorld().removeObject(powerup);
        Greenfoot.playSound("powerup.wav");
    }
    
    /** Applies powerup effect to the player */
    private void applyPowerup() {
        // If a powerup is already applied, count down and remove it after it expires
        if (powerupApplied) {
            powerupTimer--;
            if (powerupTimer <= 0) {
                switch (activePowerup) {
                    case "health":
                        health.powerup();
                        break;
                    case "speed":
                        setMovementSpeed(getMovementSpeed() - MOVEMENT_BUFF);
                        bulletSpeed -= MOVEMENT_BUFF;
                        break;
                    case "bullet":
                        bulletTimer = new Timer(20);
                        break;
                }
                powerupApplied = false;
                activePowerup = "";
            }
            
            return;
        }
        
        // Apply the powerup effect
        switch (activePowerup) {
            case "health":
                health.powerup();
                powerupTimer = 300;
                powerupApplied = true;
                break;
            case "speed":
                setMovementSpeed(getMovementSpeed() + MOVEMENT_BUFF);
                bulletSpeed += MOVEMENT_BUFF;
                powerupTimer = 300;
                powerupApplied = true;
                break;
            case "bullet":
                bulletTimer = new Timer(3);
                powerupTimer = 300;
                powerupApplied = true;
                break;
        }
    }
    
    /** Check if we're dead, game over */
    private void checkDeath() {
        if (health.getValue() <= 0) {
            UndeadWorld world = (UndeadWorld) getWorld();
            Greenfoot.playSound("player_death.wav");
            world.gameOver();
        }
    }
}
