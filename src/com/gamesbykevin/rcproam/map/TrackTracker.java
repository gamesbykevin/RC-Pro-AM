package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.rcproam.car.Car;

import com.gamesbykevin.framework.base.Cell;

/**
 * This class will keep track of a cars progress for a given track.<br>
 * This class will be used to determine who wins the race, and the car rank while the race is in progress
 * @author GOD
 */
public class TrackTracker 
{
    //the maximum allowed distance from a way point to determine if we reached the way point for the cpu
    private static final double WAYPOINT_DISTANCE_CPU = 1.5;
    
    //the maximum allowed distance from a way point to determine if we reached the way point for the human
    private static final double WAYPOINT_DISTANCE_HUMAN = 5;
    
    //how many laps have been completed
    private int laps = 0;

    //the current way point a car are targeting
    private int target = 0;

    public TrackTracker()
    {
        reset();
    }
    
    /**
     * Update the race progress
     * @param track The track currently racing on
     * @param car The car we want to update progress for
     * @param goal The location of the next way point
     */
    public void updateProgress(final Track track, final Car car)
    {
        //get the location of the current targeted way point
        final Cell goal = getWayPointLocation(track);
        
        //get the distance from the way point
        double distance = Cell.getDistance(goal, car);
        
        //always make the distance a positive number
        if (distance < 0)
            distance = -distance;
        
        //are we close enough to the way point
        boolean hasWayPoint;
        
        //check if we are close enough to the way point
        if (car.isHuman())
        {
            hasWayPoint = (distance <= WAYPOINT_DISTANCE_HUMAN);
        }
        else
        {
            hasWayPoint = (distance <= WAYPOINT_DISTANCE_CPU);
        }
        
        //if we are close enough to the way point
        if (hasWayPoint)
        {
            //check if this is the way point for the finish line
            if (track.isFinalWayPoint(getWayPointTarget()))
            {
                //if the car has past the way point goal (west is the default direction so car needs to be west of goal)
                if (car.getCol() <= goal.getCol())
                {
                    //reset the way point target back to the beginning
                    resetWayPointTarget();

                    //increase laps completed
                    addLap();
                }
            }
            else
            {
                //progress to the next way point
                setWayPointTarget(getWayPointTarget() + 1);
            }
        }
    }
    
    /**
     * Get the way point location.<br>
     * @param track The current track we are racing on
     * @return The location of the current way point
     */
    public Cell getWayPointLocation(final Track track)
    {
        return track.getWayPoint(getWayPointTarget());
    }
    
    /**
     * Reset the progress
     */
    public final void reset()
    {
        //reset the current way point targeted
        resetWayPointTarget();
        
        //reset the number of laps completed
        resetLaps();
    }
    
    /**
     * Get the target
     * @return The current way point we are targeting
     */
    public int getWayPointTarget()
    {
        return this.target;
    }
    
    /**
     * Set the target
     * @param target The way point we want to target
     */
    public void setWayPointTarget(final int target)
    {
        this.target = target;
    }
    
    /**
     * Start back at the first target
     */
    public void resetWayPointTarget()
    {
        setWayPointTarget(0);
    }
    
    /**
     * Get the number of laps completed
     * @return The number of laps completed
     */
    public int getLaps()
    {
        return this.laps;
    }
    
    /**
     * Set the number of laps completed to 0
     */
    public void resetLaps()
    {
        this.laps = 0;
    }
    
    /**
     * Add 1 lap completed to the total
     */
    public void addLap()
    {
        this.laps++;
    }
}