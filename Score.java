import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Score object keeps track of the player's score
 * 
 * @author Jacky161
 * @version v1.0-public
 */

public class Score extends Actor
{
    /** Internal Variables */
    private int score = 0;
    
    /**
     * Act - Draw score text on the screen
     */
    public void act() {
        drawScore();
    }
    
    /** Returns the current score */
    public int getScore() {
        return score;
    }
    
    /** Increments the score by an amount */
    public void increment(int value) {
        score += value;
    }
    
    /** Decrements the score by an amount */
    public void decrement(int value) {
        increment(-value);
    }
    
    /** Draws the current score on the screen */
    private void drawScore() {
        getWorld().showText("Score: " + score, 900, 10);
    }
    
    /** Returns the current score as a string */
    public String toString() {
        return score + "";
    }
}
