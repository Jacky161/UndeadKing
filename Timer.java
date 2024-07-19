/**
 * Simple timer class that will return true once the minimum amount of cycles has been reached.
 * Then reset the cycles that have passed.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Timer  
{
    private int cyclesPassed;
    private int minCycles;

    /**
     * Sets the minimum time that needs to pass
     */
    public Timer(int minCycles) {
        this.minCycles = minCycles;
        cyclesPassed = minCycles; // Always return true at first
    }
    
    /** Returns true if enough cycles have passed */
    public boolean timePassed() {
        if (cyclesPassed > minCycles) {
            cyclesPassed = 0;
            return true;
        }
        return false;
    }
    
    /** Increment the number of cycles that have passed (should be called each frame) */
    public void incrementCycles() {
        cyclesPassed++;
    }
}
