/**
 * A simple counter that has a maximum and a minimum value. The counter is restrained
 * to be within those values. If the counter underflows, it is set to the max. If it
 * overflows, it is set to the minimum. (with behaviour set to true) Else, it caps out
 * at the min and max.
 * 
 * @author Jacky161
 * @version v1.0-public
 */
public class Counter  
{
    private int value; // Default starting value
    private int low; // Lowest value for the counter
    private int max; // Highest value for the counter
    private int incrementTime; // Minimum time between each increment (measured in act cycles)
    private int actCycles; // Amount of act cycles passed since last increment
    private boolean overflowBehaviour;
    private boolean underflowBehaviour;

    /**
     * Create the counter, setting the appropriate instance variables
     */
    public Counter(int value, int low, int max) {
        this(value, low, max, 0, true, true);
    }
    
    public Counter(int value, int low, int max, int incrementTime) {
        this(value, low, max, incrementTime, true, true);
    }
    
    public Counter(int value, int low, int max, int incrementTime, boolean overflowBehaviour, boolean underflowBehaviour) {
        this.value = value;
        this.low = low;
        this.max = max;
        this.incrementTime = incrementTime;
        this.actCycles = incrementTime; // Counter can be incremented immediately
        this.overflowBehaviour = overflowBehaviour;
        this.underflowBehaviour = underflowBehaviour;
    }
    
    /** Increment # of actCycles passed. (NEEDS TO BE CALLED MANUALLY) */
    public void act() {
        actCycles++;
    }
    
    /** Increase by 1 */
    public void increment() {
        increment(1);
    }
    
    /** Increase by specified amount */
    public void increment(int amount) {
        if (actCycles < incrementTime) return;
        else actCycles = 0;
        value += amount;
        constrain();
    }
    
    /** Decrease by 1 */
    public void decrement() {
        decrement(1);
    }
    
    /** Decrease by specified amount */
    public void decrement(int amount) {
        increment(-amount);
    }
    
    /** Retrieve and return the value */
    public int getValue() {
        return value;
    }
    
    /** Set the value */
    public void setValue(int value) {
        this.value = value;
        constrain();
    }
    
    /** Constrain the value */
    private void constrain() {
        if (overflowBehaviour) {
            if (value > max) value = low;
        } else {
            if (value > max) value = max;
        }
        
        if (underflowBehaviour) {
            if (value < low) value = max;
        } else {
            if (value < low) value = low;
        }
    }
}
