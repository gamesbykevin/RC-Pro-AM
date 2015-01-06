package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.base.Cell;

import com.gamesbykevin.rcproam.map.Track;
import com.gamesbykevin.rcproam.shared.Shared;

/**
 * This class will contain a given cars attributes
 * @author GOD
 */
public class Attributes 
{
    //the number of updates needed to turn the car 15 degrees (default to 4)
    private int turnCount = 4;
    
    //speed at which we are moving
    private double speed = 0;
    
    //the current maximum speed allowed
    private double maxSpeed = DEFAULT_MAXIMUM_SPEED_ROAD;
    
    //the maximum speed on and off the road
    private double maxOffRoadSpeed = DEFAULT_MAXIMUM_SPEED_OFF_ROAD;
    private double maxRoadSpeed = DEFAULT_MAXIMUM_SPEED_ROAD;
    
    //starting speed
    protected static final double STARTING_SPEED = 0.0025;
    
    //maximum speed allowed while driving on the road
    public static final double DEFAULT_MAXIMUM_SPEED_ROAD = 0.0125;
    
    //maximum speed allowed while driving off road
    public static final double DEFAULT_MAXIMUM_SPEED_OFF_ROAD = 0.00125;
    
    //the rate at which you accelerate to the maximum speed
    public static final double DEFAULT_ACCELERATE_SPEED = 0.00005;
    
    //speed at which we will accelerate
    private double accelerateRate = DEFAULT_ACCELERATE_SPEED;
    
    //the rate we slow down the velocity while accelerating
    protected static final double VELOCITY_DECREASE_RATE = 0.9;
    
    //the rate we slow down the velocity when not accelerating
    protected static final double VELOCITY_DECREASE_RATE_OTHER = 0.98;
    
    //when not accelerating slow down the momentum so it will take the car longer to accelerate back to max speed
    protected static final double SPEED_DECELERATE = 0.975;
    
    //the increase to apply if handicap mode is enabled
    private static final double HANDICAP_RATE_INCREASE = 1.1;
    
    //the decrease to apply if handicap mode is enabled
    private static final double HANDICAP_RATE_DECREASE = .9;
    
    //do we apply the handicap to this cars attributes
    private boolean handicap = false;
    
    //which handicap do we apply (true = speed up, false = speed down)
    private boolean penalty = false;
    
    /**
     * Get the amount of updates required to turn the car 15 degrees
     * @return The number of updates turning in a given direction to turn 15 degrees
     */
    protected int getTurnCount()
    {
        return this.turnCount;
    }
    
    /**
     * Reset the attributes.<br>
     * Currently this will only turn handicap mode off
     */
    public void reset()
    {
        disableHandicap();
    }
    
    /**
     * Set the amount of updates required to turn the car 15 degrees
     * @param turnCount The number of updates needed to turn 15 degrees in a given direction
     * @throws Exception The number must be at least 2 and a multiple of 2
     */
    public void setTurnCount(final int turnCount) throws Exception
    {
        if (turnCount % 2 != 0)
            throw new Exception("Turn count must be a multiple of 2");
        
        this.turnCount = turnCount;
    }
    
    public void setAccelerateRate(final double accelerateRate)
    {
        this.accelerateRate = accelerateRate;
    }
    
    public double getAccelerateRate()
    {
        return this.accelerateRate;
    }
    
    protected double getMaxRoadSpeed()
    {
        return this.maxRoadSpeed;
    }
    
    /**
     * Set the max speed allowed while driving on road
     * @param maxRoadSpeed 
     */
    public void setMaxRoadSpeed(final double maxRoadSpeed)
    {
        this.maxRoadSpeed = maxRoadSpeed;
    }
    
    private double getMaxOffRoadSpeed()
    {
        return this.maxOffRoadSpeed;
    }
    
    /**
     * Get the current max speed allowed
     * @return 
     */
    protected double getMaxSpeed()
    {
        return this.maxSpeed;
    }

    /**
     * Set the current maximum speed
     * @param maximumSpeed 
     */
    private void setMaxSpeed(final double maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }
    
    protected double getSpeed()
    {
        return this.speed;
    }
    
    protected void setSpeed(final double speed)
    {
        this.speed = speed;
    }
    
    /**
     * Maintain the speed of the car.<br>
     * If accelerating we will manage that to ensure it stays within the maximum limit
     */
    public void checkSpeed()
    {
        //accelerate speed
        setSpeed(getSpeed() + getAccelerateRate());

        //make sure we don't go over the maximum speed, only when accelerating
        if (getSpeed() > getMaxSpeed())
            setSpeed(getMaxSpeed());
    }
    
    private boolean hasHandicap()
    {
        return this.handicap;
    }
    
    /**
     * Enable handicap mode for this car
     * @param penalty If true the max speed will decrease, if false the max speed will increase
     */
    public void applyHandicap(final boolean penalty)
    {
        this.handicap = true;
        
        this.penalty = penalty;
    }
    
    /**
     * Disable handicap mode for this car
     */
    public void disableHandicap()
    {
        this.handicap = false;
    }
    
    /**
     * Set the maximum speed depending on where the car is on the track
     * @param track The track the car is racing on
     */
    public void setMaxSpeed(final Track track, final Cell cell)
    {
        //set the maximum speed depending on the location of the car
        if (track.isRoad(cell))
        {
            setMaxSpeed(getMaxRoadSpeed());
        }
        else
        {
            setMaxSpeed(getMaxOffRoadSpeed());
        }
        
        //if we have handicap we will adjust max speed
        if (hasHandicap())
        {
            if (!penalty)
            {
                //increase max road speed since we are not penalized
                setMaxSpeed(getMaxSpeed() * HANDICAP_RATE_INCREASE);
            }
            else
            {
                //decrease max road speed since we are penalized
                setMaxSpeed(getMaxSpeed() * HANDICAP_RATE_DECREASE);
            }
        }
    }
}