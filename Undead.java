import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Arrays;
import java.util.ArrayList;

/**
 * An undead zombie that wants to kill you (the player). Try not to let them do that.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Undead extends ExtendedActor
{
    /** Internal Variables */
    private final double SCALE_FACTOR = 2.5;
    private GreenfootImage[] leftAnim = new GreenfootImage[2];
    private GreenfootImage[] rightAnim = new GreenfootImage[2];
    private Counter animIndex = new Counter(0, 0, leftAnim.length - 1, 5);
    private int[] lastFramePos = new int[2];
    
    /** Undead Variables */
    private int[] pathFindingTarget = new int[2]; // Where we want to end up (TILE COORDINATES)
    private int[] currentWaypoint = new int[2]; // Where we are currently going (TILE COORDINATES)
    private int[] lastWaypoint = new int[2];
    private boolean movingToWaypoint = false;
    
    /** Initialize undead character */
    public Undead() {
        super(1, 2.5);
        setIsMoving(true); // Undead characters never stop moving
        currentWaypoint = new int[]{-1, -1};
        prepareImages();
        setImage(leftAnim[0]);
    }
    
    /** Prepare all sprite data for the undead */
    private void prepareImages() {
        // Import all undead sprites
        leftAnim[0] = new GreenfootImage("undead.png");
        leftAnim[1] = new GreenfootImage("undead2.png");
        rightAnim[0] = new GreenfootImage("undead.png");
        rightAnim[1] = new GreenfootImage("undead2.png");
        
        // Scale up all undead sprites
        scaleImage(leftAnim[0], SCALE_FACTOR);
        scaleImage(leftAnim[1], SCALE_FACTOR);
        scaleImage(rightAnim[0], SCALE_FACTOR);
        scaleImage(rightAnim[1], SCALE_FACTOR);
        
        // Flip images for moving right
        rightAnim[0].mirrorHorizontally();
        rightAnim[1].mirrorHorizontally();
    }
    
    /**
     * Act - Handle all undead actions, mostly related to moving
     */
    public void act() {
        // Handle pausing
        UndeadWorld world = (UndeadWorld) getWorld();
        if (world.getGameState() == 1) return;
        
        animIndex.act();
        updateCoordinates();
        updateDirection();
        updateImage();
        lastFramePos[0] = getX();
        lastFramePos[1] = getY();
        move();
        checkCollision();
        
        
        // System.out.println("[" + getCurrentCoordinates()[0] + ", " + getCurrentCoordinates()[1] + "]");
    }
    
    /** Move in the direction of the player */
    private void updateDirection() {
        UndeadWorld world = (UndeadWorld) getWorld();
        Player player = world.getPlayer();
        
        // Update the current target tile
        pathFindingTarget = player.getCurrentCoordinates();
        //System.out.println("Pathfinding Target: " + debugPrintInt2(pathFindingTarget));

        /** Rudimentary AI */
        // If we have a waypoint and haven't started moving to it, move towards it
        if (((currentWaypoint[0] != getCurrentCoordinates()[0] || currentWaypoint[1] != getCurrentCoordinates()[1]) || movingToWaypoint) && currentWaypoint[0] != -1) {
            //System.out.println("moving to: " + debugPrintInt2(currentWaypoint));
            
            movingToWaypoint = true;
            int[] currentCoordinates = {getX(), getY()};
            InternalTile destinationTile = world.getTiles()[currentWaypoint[0]][currentWaypoint[1]];
            int[] destinationCoordinates = {destinationTile.getX(), destinationTile.getY()};
            
            // Compute the direction we need to go in
            if (destinationCoordinates[0] - currentCoordinates[0] > 2) setFacingDirection('r');
            else if (currentCoordinates[0] - destinationCoordinates[0] > 2) setFacingDirection('l');
            else if (destinationCoordinates[1] - currentCoordinates[1] > 2) setFacingDirection('f');
            else if (currentCoordinates[1] - destinationCoordinates[1] > 2) setFacingDirection('b');
            else {
                movingToWaypoint = false;
            }
            
            return;
        }
        
        // If we are at the player's location already, we don't need to move
        if (pathFindingTarget[0] == getCurrentCoordinates()[0] && pathFindingTarget[1] == getCurrentCoordinates()[1]) return;

        // Project all possible directions the undead can move in
        InternalTile[][] tiles = world.getTiles();
        int[] projectedCoordinatesF = new int[2];
        int[] projectedCoordinatesR = new int[2];
        int[] projectedCoordinatesL = new int[2];
        int[] projectedCoordinatesB = new int[2];
            
        System.arraycopy(getCurrentCoordinates(), 0, projectedCoordinatesF, 0, 2);
        System.arraycopy(getCurrentCoordinates(), 0, projectedCoordinatesR, 0, 2);
        System.arraycopy(getCurrentCoordinates(), 0, projectedCoordinatesL, 0, 2);
        System.arraycopy(getCurrentCoordinates(), 0, projectedCoordinatesB, 0, 2);
        //System.out.println("Current Coordinates: "+ debugPrintInt2(getCurrentCoordinates()));
            
        // Process all possible directions
        projectedCoordinatesF[1]++;
        projectedCoordinatesR[0]++;
        projectedCoordinatesL[0]--;
        projectedCoordinatesB[1]--;
            
        // Check for illegal directions
        projectedCoordinatesF = checkIllegal(projectedCoordinatesF) ? null : projectedCoordinatesF;
        projectedCoordinatesR = checkIllegal(projectedCoordinatesR) ? null : projectedCoordinatesR;
        projectedCoordinatesL = checkIllegal(projectedCoordinatesL) ? null : projectedCoordinatesL;
        projectedCoordinatesB = checkIllegal(projectedCoordinatesB) ? null : projectedCoordinatesB;
            
        // Check which coordinates are closer
        int[] closerF = findCloserCoordinates(getCurrentCoordinates(), projectedCoordinatesF, pathFindingTarget);
        int[] closerR = findCloserCoordinates(getCurrentCoordinates(), projectedCoordinatesR, pathFindingTarget);
        int[] closerL = findCloserCoordinates(getCurrentCoordinates(), projectedCoordinatesL, pathFindingTarget);
        int[] closerB = findCloserCoordinates(getCurrentCoordinates(), projectedCoordinatesB, pathFindingTarget);
        ArrayList<int[]> suboptimalCoordinates = new ArrayList<int[]>();
        ArrayList<int[]> goodCoordinates = new ArrayList<int[]>();
        
        // Find out which of these movements are good
        if (closerF == projectedCoordinatesF) goodCoordinates.add(closerF);  
        else if (projectedCoordinatesF != null) suboptimalCoordinates.add(projectedCoordinatesF);
        if (closerR == projectedCoordinatesR) goodCoordinates.add(closerR);
        else if (projectedCoordinatesR != null) suboptimalCoordinates.add(projectedCoordinatesR);
        if (closerL == projectedCoordinatesL) goodCoordinates.add(closerL);
        else if (projectedCoordinatesL != null) suboptimalCoordinates.add(projectedCoordinatesL);
        if (closerB == projectedCoordinatesB) goodCoordinates.add(closerB);
        else if (projectedCoordinatesB != null) suboptimalCoordinates.add(projectedCoordinatesB);
        
        int rand;
        
        try {
            rand = Greenfoot.getRandomNumber(goodCoordinates.size());
            System.arraycopy(currentWaypoint, 0, lastWaypoint, 0, 2);
            currentWaypoint = goodCoordinates.get(rand);
        } catch (Exception e) {
            rand = Greenfoot.getRandomNumber(suboptimalCoordinates.size());
            System.arraycopy(currentWaypoint, 0, lastWaypoint, 0, 2);
            currentWaypoint = suboptimalCoordinates.get(rand);
        }
        
        
    }
    
    /** Returns true if the given tile coordinates are not legal moves (ex. outside the world or colliding with fence) */
    private boolean checkIllegal(int[] tileCoordinates) {
        UndeadWorld world = (UndeadWorld) getWorld();
        
        // Check if we're outside the world
        if (tileCoordinates[0] < 0 || tileCoordinates[0] > 15 || tileCoordinates[1] < 0 || tileCoordinates[1] > 8) return true;
        
        // Make sure these coordinates are not occupied by a fence
        for (Fence fence : world.getFences()) {
            int[] fenceCoords = world.getTileCoordinates(fence.getIntersectingTile());
            if (tileCoordinates[0] == fenceCoords[0] && tileCoordinates[1] == fenceCoords[1]) {
                return true;
            }
        }
        
        // Check if coordinates are where we just were
        if (tileCoordinates[0] == lastWaypoint[0] && tileCoordinates[1] == lastWaypoint[1]) {
            return true;
        }
        
        return false;
    }
    
    private int[] findCloserCoordinates(int[] coordinate1, int[] coordinate2, int[] targetLocation) {
        if (coordinate1 == null) return coordinate2;
        if (coordinate2 == null) return coordinate1;
        
        int diffX1 = Math.abs(targetLocation[0] - coordinate1[0]);
        int diffX2 = Math.abs(targetLocation[0] - coordinate2[0]);
        if (diffX2 < diffX1) {
            return coordinate2;
        }
        
        int diffY1 = Math.abs(targetLocation[1] - coordinate1[1]);
        int diffY2 = Math.abs(targetLocation[1] - coordinate2[1]);
        if (diffY2 < diffY1) {
            return coordinate2;
        }
        
        return coordinate1;
    }
    
    /** Update the zombies animated movement images */
    private void updateImage() {
        switch (getFacingDirection()) {
            case 0:
                break;
            case 1:
                setImage(rightAnim[animIndex.getValue()]);
                break;
            case 2:
                setImage(leftAnim[animIndex.getValue()]);
                break;
            case 3:
                break;
            
        }
        
        animIndex.increment();
    }
    
    /** Check if we're too close to other undead */
    public boolean checkRange() {
        // Try not to spawn too close to each other
        if (getObjectsInRange(150, Undead.class).size() > 0) {
            return true;
        }
        
        // Can't spawn too close to fences
        if (getObjectsInRange(50, Fence.class).size() > 0) {
            return true;
        }
        
        // Can't spawn in corner tiles
        int[] coordinates = getCurrentCoordinates();
        int[][] illegalCoordinates = new int[4][2];
        illegalCoordinates[0] = new int[]{0, 0};
        illegalCoordinates[1] = new int[]{15, 0};
        illegalCoordinates[2] = new int[]{0, 8};
        illegalCoordinates[3] = new int[]{15, 8};
        for (int i = 0; i < 4; i++) {
            if (Arrays.equals(coordinates, illegalCoordinates[i])) return true;
        }
        
        UndeadWorld world = (UndeadWorld) getWorld();
        Player player = world.getPlayer();
        
        // Don't spawn too close to the player
        if (Math.abs(player.getX() - getX()) < 75 || Math.abs(player.getY() - getY()) < 75) {
            return true;
        }
        
        return false;
    }
    
    /** Checks for collision */
    private void checkCollision() {    
        if (isTouching(Fence.class)) {
            setLocation(lastFramePos[0], lastFramePos[1]);
        }
    }
}
