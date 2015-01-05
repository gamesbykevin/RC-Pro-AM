package com.gamesbykevin.rcproam.actor;

import com.gamesbykevin.rcproam.car.Attributes;
import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.Track;

import com.gamesbykevin.framework.base.Cell;

import java.util.Random;

public final class Cpu extends Car
{
    //the angles to face for the appropriate destination
    private static final double ANGLE_WEST = 45;
    
    //the range of speed for a car, each cpu will have a different top speed
    private static final double DEFAULT_SPEED_ROAD_MAX = Attributes.DEFAULT_MAXIMUM_SPEED_ROAD * 1.25;
    private static final double DEFAULT_SPEED_ROAD_MIN = Attributes.DEFAULT_MAXIMUM_SPEED_ROAD * .75;
    
    //the range of acceleration for a car, each cpu will have a different top speed
    private static final double DEFAULT_ACCELERATE_SPEED_MAX = Attributes.DEFAULT_ACCELERATE_SPEED * 1.25;
    private static final double DEFAULT_ACCELERATE_SPEED_MIN = Attributes.DEFAULT_ACCELERATE_SPEED * .75;
    
    //set the randomly picked max road speed
    private double defaultMaxRoadSpeed;
    
    //set the max speed allowed while turning
    private double defaultMaxTurnSpeed;
    
    //the speed while turning will be a fraction of the max road speed
    private static final double TURN_SPEED_RATIO = .75;
    
    //the optional amount of updates required to perform a 15 degree turn (must be a multiple of 2)
    private static int[] TURN_COUNT_OPTIONS = {2, 4, 6, 8};
    
    public Cpu(final Random random) throws Exception
    {
        super(false);
        
        //make sure the minimum speed does not exceed the max
        if (DEFAULT_SPEED_ROAD_MIN > DEFAULT_SPEED_ROAD_MAX)
            throw new Exception("The minimum road speed can't be greater than the maximum");
        
        //set the amount of updates required to turn the car 15 degrees
        getAttributes().setTurnCount(TURN_COUNT_OPTIONS[random.nextInt(TURN_COUNT_OPTIONS.length)]);
        
        //pick a random accelerate rate for each cpu
        getAttributes().setAccelerateRate(((DEFAULT_ACCELERATE_SPEED_MAX - DEFAULT_ACCELERATE_SPEED_MIN) * random.nextDouble()) + DEFAULT_ACCELERATE_SPEED_MIN);
        
        //set the random max road speed
        setDefaultMaxRoadSpeed(((DEFAULT_SPEED_ROAD_MAX - DEFAULT_SPEED_ROAD_MIN) * random.nextDouble()) + DEFAULT_SPEED_ROAD_MIN);
        
        //set the max speed allowed while turning
        setDefaultMaxTurnSpeed(getDefaultMaxRoadSpeed() * TURN_SPEED_RATIO);
        
        //set max driving speed since the cpu starts on a road
        getAttributes().setMaxRoadSpeed(getDefaultMaxRoadSpeed());
    }
    
    /**
     * Get the default max turn speed
     * @return the max speed allowed while turning
     */
    private double getDefaultMaxTurnSpeed()
    {
        return this.defaultMaxTurnSpeed;
    }
    
    /**
     * Set the default max turn speed
     * @param defaultMaxTurnSpeed the max speed allowed while turning
     */
    private void setDefaultMaxTurnSpeed(final double defaultMaxTurnSpeed)
    {
        this.defaultMaxTurnSpeed = defaultMaxTurnSpeed;
    }
    
    /**
     * Get the default max road speed set
     * @return the max speed allowed while driving on a road
     */
    private double getDefaultMaxRoadSpeed()
    {
        return this.defaultMaxRoadSpeed;
    }
    
    /**
     * Set the default max road speed
     * @param defaultMaxRoadSpeed the max speed allowed while driving on a road
     */
    private void setDefaultMaxRoadSpeed(final double defaultMaxRoadSpeed)
    {
        this.defaultMaxRoadSpeed = defaultMaxRoadSpeed;
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //create track reference variable to simplify the code
        final Track track = engine.getManager().getMaps().getMap().getTrack();
        
        //update basic elements for car: gravity, speed, race progress, etc...
        updateBasicElements(track, engine.getMain().getTime());
        
        //always accelerate, for now
        super.setAccelerate(true);
        
        //get the facing angle in degrees
        final double degrees = getFacingAngle();
        
        //the location of the waypoint we want to head towards
        final Cell goal = super.getWayPointLocation(track);
        
        //the angle we should be facing
        final double destination = getDestination(goal);
        
        //how far away from our destination are we
        final double difference = (degrees > destination) ? degrees - destination : destination - degrees;
        
        //make sure the cpu is turning towards the way point within a certain amount
        if (difference >= Car.AI_INTERVAL)
        {
            //set max speed while we are turning
            getAttributes().setMaxRoadSpeed(getDefaultMaxTurnSpeed());
            
            //now perform turn
            performTurn(degrees, destination);
        }
        else
        {
            //if facing destination, no need to turn
            setTurnRight(false);
            setTurnLeft(false);
            
            //set max speed
            getAttributes().setMaxRoadSpeed(getDefaultMaxRoadSpeed());
        }
    }
    
    /**
     * Perform turn in the most efficient way.
     * @param facing The angle we are facing (in degrees)
     * @param destination The angle we want to face (in degrees)
     */
    private void performTurn(final double facing, final double destination)
    {
        //now determine the most efficient way to turn
        if (facing > destination)
        {
            if (facing - destination > 180)
            {
                setTurnRight(true);
                setTurnLeft(false);
            }
            else
            {
                setTurnRight(false);
                setTurnLeft(true);
            }
        }
        else
        {
            if (destination - facing > 180)
            {
                setTurnRight(false);
                setTurnLeft(true);
            }
            else
            {
                setTurnRight(true);
                setTurnLeft(false);
            }
        }
    }
    
    /**
     * Get the required destination to face
     * @param cell The location of the next way point
     * @return The facing angle to get to the next way point (in degrees)
     */
    private double getDestination(final Cell cell)
    {
        //calculate the slope
        final double slope = (cell.getRow() - getRow()) / (cell.getCol() - getCol());
        
        //get the facing angle
        double angle = Math.atan(slope);
        
        //if the difference is negative adjust
        if (cell.getCol() - getCol() < 0)
            angle += Math.PI;
        
        //flip the direction because we start driving west by default
        angle += Math.toRadians(180);
        
        //make sure radians stay within range
        if (angle > (2 * Math.PI))
            angle -= (2 * Math.PI);
        if (angle < 0)
            angle += (2 * Math.PI);
        
        //convert radians to degrees, adding the extra angle to offset isometric angle since default direction is west
        angle = Math.toDegrees(angle) + ANGLE_WEST;

        //return result
        return angle;
    }
}