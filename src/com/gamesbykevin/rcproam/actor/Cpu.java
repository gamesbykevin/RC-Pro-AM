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
    
    //no matter the direction the road is always 4 pixles wide
    private static final int ROAD_DIMENSIONS = 4;
    
    public Cpu() throws Exception
    {
        super(false);
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
        super.updateBasic(engine.getManager().getMaps().getMap().getTrack());
        
        //always accelerate, for now
        super.setAccelerate(true);
        
        //store track variable to simplify the code
        final Track track = engine.getManager().getMaps().getMap().getTrack();
        
        //make sure we aren't turning
        if (getFacingAngle() == getDestination())
        {
            //did we change direction
            boolean flag = false;
            
            //check if we are getting close to the edge
            for (int i = 1; i <= ROAD_DIMENSIONS; i++)
            {
                switch (getDirection())
                {
                    case West:
                        //if this is not part of road
                        if (!track.isRoad(getCol() - i, getRow()))
                        {
                            //choose the location with the higher cost
                            if (track.getCost(getCol() - i, getRow() - ROAD_DIMENSIONS) > track.getCost(getCol() - i, getRow() + ROAD_DIMENSIONS))
                            {
                                setDirection(Direction.North);
                            }
                            else
                            {
                                setDirection(Direction.South);
                            }
                            
                            //flag change
                            flag = true;
                            
                            //determine if either need to go north or south
                            //setDirection(track.isRoad(getCol() - i, getRow() - ROAD_DIMENSIONS) ? Direction.North : Direction.South);
                        }
                        break;
                        
                    case East:
                        //if this is not part of road
                        if (!track.isRoad(getCol() + i, getRow()))
                        {
                            //choose the location with the higher cost
                            if (track.getCost(getCol() + i, getRow() - ROAD_DIMENSIONS) > track.getCost(getCol() + i, getRow() + ROAD_DIMENSIONS))
                            {
                                setDirection(Direction.North);
                            }
                            else
                            {
                                setDirection(Direction.South);
                            }
                            
                            //flag change
                            flag = true;
                            
                            //determine if either need to go north or south
                            //setDirection(track.isRoad(getCol() + i, getRow() - ROAD_DIMENSIONS) ? Direction.North : Direction.South);
                        }
                        break;

                    case North:
                        //if this is not part of road
                        if (!track.isRoad(getCol(), getRow() - i))
                        {
                            //choose the location with the higher cost
                            if (track.getCost(getCol() - ROAD_DIMENSIONS, getRow() - i) > track.getCost(getCol() + ROAD_DIMENSIONS, getRow() - i))
                            {
                                setDirection(Direction.West);
                            }
                            else
                            {
                                setDirection(Direction.East);
                            }
                            
                            //flag change
                            flag = true;
                            
                            //determine if either need to go east or west
                            //setDirection(track.isRoad(getCol() - ROAD_DIMENSIONS, getRow() - i) ? Direction.West : Direction.East);
                        }
                        break;

                    case South:
                        //if this is not part of road
                        if (!track.isRoad(getCol(), getRow() + i))
                        {
                            //choose the location with the higher cost
                            if (track.getCost(getCol() - ROAD_DIMENSIONS, getRow() + i) > track.getCost(getCol() + ROAD_DIMENSIONS, getRow() + i))
                            {
                                setDirection(Direction.West);
                            }
                            else
                            {
                                setDirection(Direction.East);
                            }
                            
                            //flag change
                            flag = true;
                            
                            //determine if either need to go east or west
                            //setDirection(track.isRoad(getCol() - ROAD_DIMENSIONS, getRow() + i) ? Direction.West : Direction.East);
                        }
                        break;
                }
                
                //if change, exit loop
                if (flag)
                    break;
            }
        }
        
        //get the facing angle in degrees
        final double degrees = getFacingAngle();
        
        //the angle we should be facing
        final double destination = getDestination();
        
        //determine if we are facing the correct angle
        if (degrees != destination)
        {
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
                throw new Exception("Direction not setup here");
        }
        
        return destination;
    }
}