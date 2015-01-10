package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.rcproam.map.Track;
import java.awt.Color;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will keep track of a cars progress for a given track.<br>
 * This class will be used to determine who wins the race, and the car rank while the race is in progress
 * @author GOD
 */
public class TrackProgress implements Disposable
{
    //the maximum allowed distance from a way point to determine if we reached the way point for the cpu
    private static final double WAYPOINT_DISTANCE_CPU = 1.5;
    
    //the maximum allowed distance from a way point to determine if we reached the way point for the human
    private static final double WAYPOINT_DISTANCE_HUMAN = 5;
    
    //how many laps have been completed
    private int laps = 0;

    //how many checkpoints have been completed (used to determine rank 1st, 2nd, 3rd, etc..)
    private int checkPoints = 0;
    
    //how far we have traveled in the race
    private double raceProgress = 0;
    
    //the current way point a car are targeting
    private int target = 0;

    //timer used to track the time of each lap
    private Timer timer;
    
    //timer user to track entire race time
    private Timer stopWatch;
    
    //the text description of each lap
    private List<String> lapDescription;
    
    public TrackProgress()
    {
        //create new timer
        this.timer = new Timer();
        
        //create new timer
        this.stopWatch = new Timer();
        
        //create list which will contain each lap time
        this.lapDescription = new ArrayList<>();
        
        //reset 
        reset();
    }
    
    @Override
    public void dispose()
    {
        timer = null;
        
        if (lapDescription != null)
        {
            lapDescription.clear();
            lapDescription = null;
        }
    }
    
    /**
     * Update the tracking progress
     * @param track The track currently racing on
     * @param car The car we want to update progress for
     * @param time The number of nanoseconds per update
     */
    public void updateProgress(final Track track, final Car car, final long time)
    {
        //update timer
        timer.update(time);
        
        //update timer
        stopWatch.update(time);
        
        //get the location of the current targeted way point
        final Cell goal = getCheckPointLocation(track);
        
        //get the distance from the way point
        double distance = Cell.getDistance(goal, car);
        
        //always make the distance a positive number
        if (distance < 0)
            distance = -distance;
        
        //are we close enough to the way point
        boolean hasCheckPoint;
        
        //check if we are close enough to the way point
        if (car.isHuman())
        {
            hasCheckPoint = (distance <= WAYPOINT_DISTANCE_HUMAN);
        }
        else
        {
            hasCheckPoint = (distance <= WAYPOINT_DISTANCE_CPU);
        }
        
        //if we are close enough to the way point
        if (hasCheckPoint)
        {
            //check if this is the way point for the finish line
            if (track.isFinalCheckPoint(getCheckPointTarget()))
            {
                //if the car has past the way point goal (west is the default direction so car needs to be west of goal)
                if (car.getCol() <= goal.getCol())
                {
                    //reset the way point target back to the beginning
                    resetCheckPointTarget();

                    //increase laps completed
                    addLap();
                    
                    //add lap description to list
                    lapDescription.add("Lap " + getLaps() + " - " + timer.getDescPassed(Timers.FORMAT_6));
                    
                    //reset timer
                    timer.reset();
                    
                    //check point has been completed, add to total
                    this.checkPoints++;
                }
            }
            else
            {
                //progress to the next way point
                setCheckPointTarget(getCheckPointTarget() + 1);
                
                //check point has been completed, add to total
                this.checkPoints++;
            }
        }
        
        //update the race progress
        updateRaceProgress(track, car);
    }
    
    /**
     * Get the way point location.<br>
     * @param track The current track we are racing on
     * @return The location of the current way point
     */
    public Cell getCheckPointLocation(final Track track)
    {
        return track.getCheckPoint(getCheckPointTarget());
    }
    
    /**
     * Reset the progress
     */
    public final void reset()
    {
        //reset the current way point targeted
        resetCheckPointTarget();
        
        //reset the number of laps completed
        resetLaps();
        
        //reset timer
        timer.reset();
        
        //reset timer
        stopWatch.reset();
        
        //clear list of lap descriptions
        lapDescription.clear();
        
        //reset checkpoints completed back to 0
        checkPoints = 0;
        
        //reset race completion progress
        raceProgress = 0;
    }
    
    /**
     * Get the target
     * @return The current way point we are targeting
     */
    public int getCheckPointTarget()
    {
        return this.target;
    }
    
    /**
     * Set the target
     * @param target The way point we want to target
     */
    public void setCheckPointTarget(final int target)
    {
        this.target = target;
    }
    
    /**
     * Start back at the first target
     */
    public void resetCheckPointTarget()
    {
        setCheckPointTarget(0);
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
    
    /**
     * Set the current progress of the race.<br>
     * This method will be used to determine the rank of each car 1st, 2nd, 3rd, etc...
     * @param track The current track we are racing on
     * @param cell The current location of the car
     */
    private void updateRaceProgress(final Track track, final Cell carLocation)
    {
        //progress is the total # of completed checkpoints + the progress towards next checkpoint
        this.raceProgress = (double)this.checkPoints + getCurrentCheckPointProgress(track, carLocation);
    }
    
    /**
     * Get the progress from the previous checkpoint to the next one.<br>
     * Progress will range from 0.0 - 1.0
     * @param track The current track we are racing on
     * @param carLocation The current location of the car
     * @return 
     */
    public double getCurrentCheckPointProgress(final Track track, final Cell carLocation)
    {
        //location of previous checkpoint
        Cell previous = (getCheckPointTarget() <= 0) ? track.getCheckPoint(track.getCheckPointCount() - 1) : track.getCheckPoint(getCheckPointTarget() - 1);
        
        //location of current checkpoint
        Cell current = track.getCheckPoint(getCheckPointTarget());
        
        //full distance between previous and current checkpoint
        final double fullDistance = Cell.getDistance(previous, current);
        
        //the progress completed towards next check point
        final double progressDistance = fullDistance - Cell.getDistance(carLocation, current);
        
        //return the progress completed towards the next checkpoint
        return (double)(progressDistance / fullDistance);
    }
    
    /**
     * Get the race progress
     * @return The total number of checkpoints completed for this race
     */
    public double getRaceProgress()
    {
        return this.raceProgress;
    }
    
    public void renderLapDescription(final Graphics graphics, final int x, final int y, final int laps)
    {
        //text will be white
        graphics.setColor(Color.WHITE);
        
        //get the font height
        final int fontHeight = graphics.getFontMetrics().getHeight();
        
        //draw all lap times
        for (int i = 0; i <= laps; i++)
        {
            //empty description
            final String emptyDesc = "Lap " + (i+1);
            
            //current time description
            final String timedDesc = emptyDesc + " - " + timer.getDescPassed(Timers.FORMAT_6);
            
            //y-ccordinate to display info
            final int drawY = y + (fontHeight * i);
            
            //if there are no descriptions we can only show current progress
            if (lapDescription.isEmpty())
            {
                if (i == 0)
                {
                    graphics.drawString(timedDesc, x, drawY);
                }
                else if (i < laps)
                {
                    graphics.drawString(emptyDesc, x, drawY);
                }
            }
            else
            {
                if (i < lapDescription.size())
                {
                    graphics.drawString(lapDescription.get(i), x, drawY);
                }
                else
                {
                    if (i == lapDescription.size() && i < laps)
                    {
                        graphics.drawString(timedDesc, x, drawY);
                    }
                    else if (i < laps)
                    {
                        graphics.drawString(emptyDesc, x, drawY);
                    }
                }
            }
            
            if (i == laps)
                graphics.drawString("Total - " + stopWatch.getDescPassed(Timers.FORMAT_6), x, drawY);
        }
    }
}