import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.Arrays;

/**
 * The world of the undead
 * 
 * 
 * Some project notes:
 *  --> Sprites are downloaded from itch.io, credits are as follows:
 *  https://game-endeavor.itch.io/mystic-woods
 *  https://0x72.itch.io/16x16-dungeon-tileset
 *  
 *  --> SFX/Music are from Stardew Valley (specifically the minigame Journey of the Prairie King in it)
 *  --> This game is heavily inspired by / based on Journey of the Prairie King
 *  
 *  
 *  Controls:
 *  WASD to move
 *  Spacebar to shoot
 *  Escape to pause
 *  
 *  Enjoy!
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class UndeadWorld extends World
{
    /** Internal Variables */
    private boolean debug = false;
    private boolean mapEditor = false;
    private Player player; // Reference for the player
    private Score score; // Reference for player score
    private GreenfootSound overworldBg = new GreenfootSound("overworld.wav");
    private int gameState = 0;
    private boolean escDown = false;
    private int currentWave = 1;
    private int remainingZombies = 20;
    private int spawnTimer = 90;
    private int waveComplete = 180;
    private int powerupTimer = Greenfoot.getRandomNumber(120) + 300;
    
    private InternalTile[][] tiles = new InternalTile[16][9];
    private boolean[][] legalPowerupSpawns = new boolean[16][9];
    private List<Fence> fences;

    /**
     * Constructor of the world
     */
    public UndeadWorld()
    {
        // Create a new world with 960x540 (16x9) cells with a cell size of 1x1 pixels.
        super(960, 540, 1);
        setPaintOrder(Health.class, Player.class, Bullet.class, Powerup.class, Undead.class, Fence.class, Grass.class, InternalTile.class);
        setActOrder(Player.class);
        
        // Set background colour
        GreenfootImage background = getBackground();
        Color backgroundColour = new Color(80, 155, 102);
        background.setColor(backgroundColour);
        background.fill();
        
        // Add player
        player = new Player();
        addObject(player, getWidth() / 2, getHeight() / 2);
        player.initHealth();
        
        // Add score
        score = new Score();
        addObject(score, 0, 0);
        
        // Add Internal Path Finding Tiles
        for (int y = 30; y < getHeight(); y += 60) {
            for (int x = 30; x < getWidth(); x += 60) {
                tiles[x/ 60][y / 60] = new InternalTile(debug, x, y);
                addObject(tiles[x / 60][y / 60], x, y);
            }
        }
        
        // Add Fences
        addFences();
        
        // Add Grass (Decorative Only)
        for (int i = 0; i < 400; i++) {
            spawnGrass();
        }

        // Set BG volume
        overworldBg.setVolume(60);
        
        // Setup legal spawns
        // First, set all spawns to true
        for (boolean[] row : legalPowerupSpawns) {
            Arrays.fill(row, true);
        }
        
        // Set all tiles occupied by fences to false
        for (Fence fence : fences) {
            int[] coordinates = getTileCoordinates(getObjectsAt(fence.getX(), fence.getY(), InternalTile.class).get(0));
            legalPowerupSpawns[coordinates[0]][coordinates[1]] = false;
        }
        
        // Set all edge tiles and undead spawn tiles to false
        legalPowerupSpawns[0][0] = false;
        legalPowerupSpawns[15][0] = false;
        legalPowerupSpawns[0][8] = false;
        legalPowerupSpawns[15][8] = false;
        
        for (int i = 3; i < 6; i++) {
            legalPowerupSpawns[0][i] = false;
            legalPowerupSpawns[15][i] = false;
        }
        
        for (int i = 7; i < 9; i++) {
            legalPowerupSpawns[i][0] = false;
            legalPowerupSpawns[i][8] = false;
        }
    }
    
    /**
     * Act method for the world
     */
    public void act() {
        processKeyboard();
        if (gameState == 1) {
            overworldBg.pause();
            drawPaused();
            return;
        } else if (gameState == 2) {
            showText("WAVE COMPLETE", getWidth() / 2, getHeight() / 2);
            waveComplete--;
            
            if (waveComplete <= 0) {
                // Setup the next wave
                setupWave();
                
                waveComplete = 180;
            }
        } else {
            showText("", getWidth() / 2, getHeight() / 2);
        }
        
        overworldBg.playLoop();
        spawnTimer--;
        
        // Try to spawn zombie
        if (spawnTimer <= 0 && !(remainingZombies == 0)) {
            spawnTimer = 0;
            spawnUndead();
            remainingZombies--;
            setSpawnTimer();
        }
        
        // When all zombies are dead, transition to next wave state
        if (remainingZombies <= 0 && getObjects(Undead.class).size() <= 0) {
            gameState = 2;
            return;
        }
        
        // Try to spawn a powerup only if none exist
        if (getObjects(Powerup.class).size() == 0) {
            powerupTimer--;
            if (powerupTimer <= 0) {
                powerupTimer = Greenfoot.getRandomNumber(120) + 300;
                spawnPowerup();
            }
        }
        

        
        // Draw wave text and # of zombies
        showText("Wave: " + currentWave, 150, 10);
        showText("Undead Remaining: " + remainingZombies, 300, 10);
        
        // Debug Map Editor
        if (mapEditor) mapEditor();
    }
    
    /** Spawn a random powerup */
    public void spawnPowerup() {
        // Pick a spawn location
        int[] spawnCoordinates = new int[2];
            
        while (true) {
            spawnCoordinates[0] = Greenfoot.getRandomNumber(16);
            spawnCoordinates[1] = Greenfoot.getRandomNumber(9);
                
            if (legalPowerupSpawns[spawnCoordinates[0]][spawnCoordinates[1]]) break;
        }
        
        // Pick a random powerup to spawn
        int randPowerup = Greenfoot.getRandomNumber(3);
        
        // Spawn the powerup
        switch (randPowerup) {
            case 0:
                addObject(new HealthPotion(), tiles[spawnCoordinates[0]][spawnCoordinates[1]].getX(), tiles[spawnCoordinates[0]][spawnCoordinates[1]].getY());
                break;
            case 1:
                addObject(new SpeedPotion(), tiles[spawnCoordinates[0]][spawnCoordinates[1]].getX(), tiles[spawnCoordinates[0]][spawnCoordinates[1]].getY());
                break;
            case 2:
                addObject(new BulletUp(), tiles[spawnCoordinates[0]][spawnCoordinates[1]].getX(), tiles[spawnCoordinates[0]][spawnCoordinates[1]].getY());
                break;
        }
    }
    
    /** Advance the current wave, setup the game */
    private void setupWave() {
        currentWave++;
        remainingZombies = 15 + (currentWave * 5);
        gameState = 0;
    }
    
    /** Sets the spawn timer based on the current wave */
    private void setSpawnTimer() {
        spawnTimer = 100 - (currentWave * 10);
        
        // Minimum spawn time is every 10 frames
        if (spawnTimer < 10) {
            spawnTimer = 10;
        }
    }
    
    /** Process pausing */
    private void processKeyboard() {
        // No pausing allowed during wave transition (that would break the game with the current game state implementation)
        if (Greenfoot.isKeyDown("escape") && gameState != 2) {
            escDown = true;
        } else if (escDown && gameState != 2) {
            escDown = false;
            gameState = gameState == 0 ? 1 : 0;
        }
    }
    
    /** Show the pause screen */
    private void drawPaused() {
        showText("PAUSED", getWidth() / 2, getHeight() / 2);
    }
        
    /** Return a reference to the player */
    public Player getPlayer() {
        return player;
    }
    
    /** Returns the 3D array that holds all the InternalTiles in the world */
    public InternalTile[][] getTiles() {
        return tiles;
    }
    
    /** Returns the TILE coordinates of the actor requested */
    public int[] getActorCoordinates(ExtendedActor actor) {
        InternalTile tile = actor.getIntersectingTile();
        return getTileCoordinates(tile);
    }
    
    /** Find the coordinates of the tile requested (as stored in the 3D array) */
    public int[] getTileCoordinates(InternalTile tile) {
        // Loop through all existing tiles and find the one the actor is intersecting with
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                if (tile == tiles[x][y]) {
                    // FOUND HIM!
                    int[] location = new int[2];
                    location[0] = x;
                    location[1] = y;
                    return location;
                }
            }
        }
        
        // This should never happen
        return new int[]{-1, -1};
    }
    
    /** Spawns one undead character in a random location (This function is not actually used anymore) */
    private void spawnUndeadRand() {
        int x;
        int y;
        Undead undead;
        
        int numTries = 0;
        while (true) {
            numTries++;
            x = Greenfoot.getRandomNumber(getWidth());
            y = Greenfoot.getRandomNumber(getHeight());
            
            undead = new Undead();
            addObject(undead, x, y);
            undead.updateCoordinates();
            
            if (undead.checkRange()) {
                // Give up after too many tries
                if (numTries >= 10000) {
                    System.out.println("WARNING: Spawning zombie took too many tries! Are there too many zombies on screen?");
                    break;
                }
                
                removeObject(undead);
                
                continue;
            }
            
            break;
        }
    }
    
    /** Spawn an undead at one of the 4 spawn points */
    private void spawnUndead() {
        int x;
        int y;
        int rand;
        
        // Pick a side of the world
        switch (Greenfoot.getRandomNumber(4)) {
            case 0: // Top
                x = Greenfoot.getRandomNumber(2) == 0 ? 450 : 510;
                y = 30;
                break;
            case 1: // Bottom
                x = Greenfoot.getRandomNumber(2) == 0 ? 450 : 510;
                y = 510;
                break;
            case 2: // Left
                x = 30;
                rand = Greenfoot.getRandomNumber(3);
                y = rand == 0 ? 210 : rand == 1 ? 270 : 330;
                break;
            case 3: // Right
                x = 930;
                rand = Greenfoot.getRandomNumber(3);
                y = rand == 0 ? 210 : rand == 1 ? 270 : 330;
                break;
            
            default:
                x = 0;
                y = 0;
        }
        
        addObject(new Undead(), x, y);
    }
    
    /** Spawns one grass decoration */
    private void spawnGrass() {
        int x;
        int y;
        Grass grass;
        
        int numTries = 0;
        
        // Try up to 1000 times to spawn grass
        while (true) {
            numTries++;
            x = Greenfoot.getRandomNumber(getWidth());
            y = Greenfoot.getRandomNumber(getHeight());
            
            grass = new Grass();
            addObject(grass, x, y);
            
            if (grass.checkRange()) {
                // Give up after too many tries
                if (numTries >= 1000) {
                    System.out.println("WARNING: Spawning grass took too many tries! Is there too much grass on screen?");
                    break;
                }
                
                removeObject(grass);
                
                continue;
            }
            
            break;
        }
    }
    
    /** Add all fences to the game world */
    private void addFences() {
        // Left/Right Edge
        for (int i = 90; i < 451; i += 60) {
            if (i == 210 || i == 270 || i == 330) continue;
            
            addObject(new Fence(270), 30, i);
            addObject(new Fence(90), 930, i);
        }
        
        // Top/Bottom Edge
        for (int i = 90; i < 871; i += 60) {
            if (i == 450 || i == 510) continue;
            
            addObject(new Fence(), i, 30);
            addObject(new Fence(), i, 510);
        }
        
        // Left Inner
        addObject(new Fence(270), 330, 210);
        addObject(new Fence(270), 330, 330);
        
        // Top Inner
        addObject(new Fence(), 390, 150);
        addObject(new Fence(), 570, 150);
        
        // Right Inner
        addObject(new Fence(90), 630, 210);
        addObject(new Fence(90), 630, 330);
        
        // Bottom Inner
        addObject(new Fence(), 390, 390);
        addObject(new Fence(), 570, 390);
        
        
        // Retrieve list of all fences and save it
        fences = getObjects(Fence.class);
    }
    
    /** Returns a list containing all the fences that exist in the world */
    public List<Fence> getFences() {
        return fences;
    }
    
    /** Returns a reference to the score object */
    public Score getScore() {
        return score;
    }
    
    /** Process game over condition */
    public void gameOver() {
        overworldBg.stop();
        
        // Draw game over text
        showText("Game Over!", getWidth() / 2, getHeight() / 2 - 10);
        showText("Your final score was: " + score, getWidth() / 2, getHeight() / 2 + 10);
        
        Greenfoot.stop();
    }
    
    /** Return the state of the game */
    public int getGameState() {
        return gameState;
    }
    
    /** DEVELOPMENT ONLY: Map editing function for adding fences to the world at tile locations */
    private void mapEditor() {    
        // Loop through all existing tiles and find the one the mouse is clicked on
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                if (Greenfoot.mouseClicked(tiles[x][y])) {
                    int buttonPressed = Greenfoot.getMouseInfo().getButton();
                    if (buttonPressed == 1) {
                        addObject(new Fence(), tiles[x][y].getX(), tiles[x][y].getY());
                        System.out.println("addObject(new Fence(), " + tiles[x][y].getX() + ", " + tiles[x][y].getY() + ");");
                    }
                    else if (buttonPressed == 2) {
                        try {
                            List<Fence> fenceList = getObjectsAt(tiles[x][y].getX(), tiles[x][y].getY(), Fence.class);
                            fenceList.get(0).turn(90);
                        } catch (Exception e) {
                        }
                    }
                    else if (buttonPressed == 3) removeObjects(getObjectsAt(tiles[x][y].getX(), tiles[x][y].getY(), Fence.class));
                }
            }
        }
    }
}
