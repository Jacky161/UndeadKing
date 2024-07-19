import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Some extended methods that are useful for actors.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class ExtendedActor extends Actor
{
    /** ExtendedActor Variables **/
    private int facingDirection; // 0=F, 1=R, 2=L, 3=B
    private double movementSpeed;
    private boolean isMoving = false;
    private int[] currentCoordinates = new int[2];
    
    /** Create an ExtendedActor with the specified direction and speed */
    public ExtendedActor(int facingDirection, double movementSpeed) {
        this.facingDirection = facingDirection;
        this.movementSpeed = movementSpeed;
    }
    
    /** Updates the tile coordinates that the actor is at */
    public void updateCoordinates() {
        UndeadWorld world = (UndeadWorld) getWorld();
        currentCoordinates = world.getActorCoordinates(this);
        
        //System.out.println("Updated Coordinates To: " + debugPrintInt2(currentCoordinates));
    }
    
    /** Returns the InternalTile that the player is colliding with */
    public InternalTile getIntersectingTile() {
        InternalTile tile = (InternalTile) getOneIntersectingObject(InternalTile.class);
        return tile;
    }
    
    /** Sets the facingDirection based on character */
    public void setFacingDirection(char facingDirection) {
        this.facingDirection = parseFacingString(facingDirection);
    }
    
    /** Sets the facingDirection based on integer */
    public void setFacingDirection(int facingDirection) {
        this.facingDirection = facingDirection;
    }
    
    /** Parse the character into an integer */
    private int parseFacingString(char facingDirection) {
        switch (facingDirection) {
            case 'f':
                return 0;
            case 'r':
                return 1;
            case 'l':
                return 2;
            case 'b':
                return 3;
        }
        
        return 0;
    }
    
    /** Returns the current facingDirection */
    public int getFacingDirection() {
        return facingDirection;
    }
    
    /** Returns the current tile coordinates of the actor */
    public int[] getCurrentCoordinates() {
        return currentCoordinates;
    }
    
    /** Sets whether we are moving */
    public void setIsMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }
    
    /** Returns whether we are moving */
    public boolean getIsMoving() {
        return isMoving;
    }
    
    /** Gets the current movement speed */
    public double getMovementSpeed() {
        return movementSpeed;
    }
    
    /** Sets the current movement speed */
    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
    
    /** Move the actor in the facingDirection */
    public void move() {
        move(facingDirection);
    }
    
    /** Move in the specified direction */
    public void move(int direction) {
        switch (direction) {
            case 0:
                setLocation(getX(), (int)(getY() + (movementSpeed)));
                break;
            case 1:
                setLocation((int)(getX() + movementSpeed), getY());
                break;
            case 2:
                setLocation((int)(getX() - movementSpeed), getY());
                break;
            case 3:
                setLocation(getX(), (int)(getY() - movementSpeed));
                break;
        }
    }
    
    /** Invert the movement of the character */
    public void invertMove() {
        switch (facingDirection) {
            case 0:
                move(3);
                break;
            case 1:
                move(2);
                break;
            case 2:
                move(1);
                break;
            case 3:
                move(0);
                break;
        }
    }
    
    /** Scales up image by the specified scaleFactor */
    public static GreenfootImage scaleImage(GreenfootImage image, double scaleFactor) {
        image.scale((int)(image.getWidth() * scaleFactor), (int)(image.getHeight() * scaleFactor));
        return image;
    }
    
    /** Returns an int array with length 2 in a human readable format (FOR DEBUG PURPOSES) **/
    public static String debugPrintInt2(int[] arr) {
        return "[" + arr[0] + ", " + arr[1] + "]";
    }
}
