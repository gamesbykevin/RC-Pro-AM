package com.gamesbykevin.rcproam.actor;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.Track;

public final class Cpu extends Car
{
    private enum Direction
    {
        North, South, East, West
    }
    
    //the angles to face for the appropriate destination
    private static final double ANGLE_WEST = 45;
    private static final double ANGLE_NORTH = 135;
    private static final double ANGLE_EAST = 225;
    private static final double ANGLE_SOUTH = 315;
    
    //maximum speed allowed while driving on the road
    private static final double DEFAULT_MAXIMUM_SPEED_ROAD = 0.025;
    
    //the max speed allowed while turning
    private static final double TURN_SPEED = (DEFAULT_MAXIMUM_SPEED_ROAD * .35);
    
    public Cpu() throws Exception
    {
        super(false);
        
        //cpu accelerate speed will be faster
        super.setAccelerateRate(Car.DEFAULT_ACCELERATE_SPEED * 2);
        
        //set max driving speed
        super.setMaxRoadSpeed(DEFAULT_MAXIMUM_SPEED_ROAD * 2);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update basic elements for car: gravity, speed, etc...
        updateBasicElements(engine.getManager().getMaps().getMap().getTrack());
        
        //always accelerate, for now
        super.setAccelerate(true);
        
        //create track reference variable to simplify the code
        final Track track = engine.getManager().getMaps().getMap().getTrack();
        
        //get the facing angle in degrees
        final double degrees = getFacingAngle();
        
        //the angle we should be facing
        final double destination = 0;//getDestination();
        
        //determine if we are facing the correct angle
        if (degrees != destination)
        {
            //set max speed while we are turning
            setMaxRoadSpeed(TURN_SPEED);
            
            //now determine the most efficient way to turn
            if (degrees > destination)
            {
                if (degrees - destination > 180)
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
                if (destination - degrees > 180)
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
        else
        {
            //if facing destination, no need to turn
            setTurnRight(false);
            setTurnLeft(false);
            
            //set max speed
            setMaxRoadSpeed(DEFAULT_MAXIMUM_SPEED_ROAD);
        }
    }
}