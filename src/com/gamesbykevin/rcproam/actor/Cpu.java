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
    
    //the direction the car is headed in, default west
    private Direction direction = Direction.West;
    
    //the angles to face for the appropriate destination
    private static final double ANGLE_WEST = 45;
    private static final double ANGLE_NORTH = 135;
    private static final double ANGLE_EAST = 225;
    private static final double ANGLE_SOUTH = 315;
    
    //no matter the direction the road is always 4 cells wide
    private static final int ROAD_DIMENSIONS = 4;
    
    //how many cells do we check ahead check if a turn is required
    private static final int VIEW_AHEAD = ROAD_DIMENSIONS - 1;
    
    //maximum speed allowed while driving on the road
    private static final double DEFAULT_MAXIMUM_SPEED_ROAD = 0.025;
    
    //the max speed allowed while turning
    private static final double TURN_SPEED = (DEFAULT_MAXIMUM_SPEED_ROAD * .35);
    
    //default error message
    private static final String ERROR_MESSAGE = "Direction not setup here";
    
    public Cpu() throws Exception
    {
        super(false);
        
        //cpu accelerate speed will be faster
        super.setAccelerateRate(Car.DEFAULT_ACCELERATE_SPEED * 2);
        
        //set max driving speed
        super.setMaxRoadSpeed(DEFAULT_MAXIMUM_SPEED_ROAD * 2);
    }
    
    /**
     * Get the direction the car is driving in
     * @return The direction ie: north, south, east, west
     */
    private Direction getDirection()
    {
        return this.direction;
    }
    
    private void setDirection(final Direction direction)
    {
        this.direction = direction;
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update basic elements for car: gravity, speed, etc...
        updateBasicElements(engine.getManager().getMaps().getMap().getTrack());
        
        //always accelerate, for now
        super.setAccelerate(true);
        
        //store track variable to simplify the code
        final Track track = engine.getManager().getMaps().getMap().getTrack();
        
        //make sure we aren't turning
        if (getFacingAngle() == getDestination())
        {
            //check if we are coming to a turn
            checkCorner(track);
        }
        
        //get the facing angle in degrees
        final double degrees = getFacingAngle();
        
        //the angle we should be facing
        final double destination = getDestination();
        
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
    
    /**
     * Look ahead in the direction we are going to determine if a turn is coming up.
     * @param track The current track we are racing on
     */
    private void checkCorner(final Track track)
    {
        //check if we are getting close to the edge
        switch (getDirection())
        {
            case West:
                //if this is not part of road
                if (!track.isRoad(getCol() - VIEW_AHEAD, getRow()))
                {
                    final double north = track.getCost(getCol() - VIEW_AHEAD, getRow() - ROAD_DIMENSIONS);
                    final double south = track.getCost(getCol() - VIEW_AHEAD, getRow() + ROAD_DIMENSIONS);
                    final double east = track.getCost(getCol() + VIEW_AHEAD, getRow());
                    
                    //choose the location with the higher cost
                    setDirection((north > south) ? Direction.North : Direction.South);
                    
                    if (east > north && east > south)
                        setDirection(Direction.East);
                }
                break;

            case East:
                //if this is not part of road
                if (!track.isRoad(getCol() + VIEW_AHEAD, getRow()))
                {
                    final double north = track.getCost(getCol() + VIEW_AHEAD, getRow() - ROAD_DIMENSIONS);
                    final double south = track.getCost(getCol() + VIEW_AHEAD, getRow() + ROAD_DIMENSIONS);
                    final double west = track.getCost(getCol() - VIEW_AHEAD, getRow());
                    
                    //choose the location with the higher cost
                    setDirection((north > south) ? Direction.North : Direction.South);
                    
                    if (west > north && west > south)
                        setDirection(Direction.West);
                }
                break;

            case North:
                //if this is not part of road
                if (!track.isRoad(getCol(), getRow() - VIEW_AHEAD))
                {
                    final double west = track.getCost(getCol() - ROAD_DIMENSIONS, getRow() - VIEW_AHEAD);
                    final double east = track.getCost(getCol() + ROAD_DIMENSIONS, getRow() - VIEW_AHEAD);
                    final double south = track.getCost(getCol(), getRow() + VIEW_AHEAD);
                    
                    //choose the location with the higher cost
                    setDirection((west > east) ? Direction.West : Direction.East);
                    
                    if (south > west && south > east)
                        setDirection(Direction.South);
                }
                break;

            case South:
                //if this is not part of road
                if (!track.isRoad(getCol(), getRow() + VIEW_AHEAD))
                {
                    final double west = track.getCost(getCol() - ROAD_DIMENSIONS, getRow() + VIEW_AHEAD);
                    final double east = track.getCost(getCol() + ROAD_DIMENSIONS, getRow() + VIEW_AHEAD);
                    final double north = track.getCost(getCol(), getRow() - VIEW_AHEAD);
                    
                    //choose the location with the higher cost
                    setDirection((west > east) ? Direction.West : Direction.East);
                    
                    if (north > west && north > east)
                        setDirection(Direction.North);
                }
                break;
        }
    }
    
    /**
     * Get the angle (in degrees) to face based on the current direction
     * @return angle (in degrees) for the currently assigned direction
     * @throws Exception 
     */
    private double getDestination() throws Exception
    {
        //the angle we want to face
        final double destination;
        
        //get the correct angle to face
        switch (getDirection())
        {
            case West:
                destination = ANGLE_WEST;
                break;
                
            case North:
                destination = ANGLE_NORTH;
                break;
                
            case East:
                destination = ANGLE_EAST;
                break;
                
            case South:
                destination = ANGLE_SOUTH;
                break;
                
            default:
                throw new Exception(ERROR_MESSAGE);
        }
        
        return destination;
    }
}