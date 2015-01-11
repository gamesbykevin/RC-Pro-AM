package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.Track;
import com.gamesbykevin.rcproam.shared.IElement;
import com.gamesbykevin.rcproam.shared.Shared;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Car extends Sprite implements Disposable, IElement
{
    //starting angle when race begins
    private static final int START_ANGLE = 45;
    
    //the angle the object should be facing in radians NOT degrees, the facing direction here is WEST
    private double angle = Math.toRadians(START_ANGLE);
    
    //each turn will be 15 degrees
    protected static final double TURN_INTERVAL = 15;
    
    //the current count
    private int count = 0;
    
    //the car rank in a race 1st, 2nd, 3rd, etc...
    private int rank = 0;
    
    //things a car can do
    private boolean turnLeft = false;
    private boolean turnRight = false;
    private boolean accelerate = false;
    
    //do we render this car (if a car is not within the window there is no reason to render)
    private boolean render = true;
    
    //is the car controlled by a human
    private final boolean human;
    
    //dimensions of car
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    
    //object used to measure progress for a given race
    private TrackProgress tracker;
    
    //the name of this car
    private String name;
    
    //the attributes of the car speed, accelerate, etc...
    private Attributes attributes;
    
    public enum Direction
    {
        Facing000, 
        Facing015, Facing030, Facing045, Facing060, Facing075, Facing090, 
        Facing105, Facing120, Facing135, Facing150, Facing165, Facing180, 
        Facing195, Facing210, Facing225, Facing240, Facing255, Facing270, 
        Facing285, Facing300, Facing315, Facing330, Facing345, Crash
    }
    
    //the color of the car to be displayed on the mini map
    private Color carColor;
    
    /**
     * Create a new car
     * @param human Is the car human
     * @throws Exception Exception will be thrown if the turn interval is not a multiple of the turn step
     */
    protected Car(final boolean human)
    {
        super();
        
        //is the car controlled by a human
        this.human = human;
        
        //the cars attributes
        this.attributes = new Attributes();
        
        //create new track tracker
        this.tracker = new TrackProgress();
        
        //create sprite sheet
        super.createSpriteSheet();
        
        addAnimation(Direction.Facing000, 0, 0);
        addAnimation(Direction.Facing015, 1, 0);
        addAnimation(Direction.Facing030, 2, 0);
        addAnimation(Direction.Facing045, 3, 0);
        addAnimation(Direction.Facing060, 4, 0);
        addAnimation(Direction.Facing075, 5, 0);
        
        addAnimation(Direction.Facing090, 0, 1);
        addAnimation(Direction.Facing105, 1, 1);
        addAnimation(Direction.Facing120, 2, 1);
        addAnimation(Direction.Facing135, 3, 1);
        addAnimation(Direction.Facing150, 4, 1);
        addAnimation(Direction.Facing165, 5, 1);
        
        addAnimation(Direction.Facing180, 0, 2);
        addAnimation(Direction.Facing195, 1, 2);
        addAnimation(Direction.Facing210, 2, 2);
        addAnimation(Direction.Facing225, 3, 2);
        addAnimation(Direction.Facing240, 4, 2);
        addAnimation(Direction.Facing255, 5, 2);
        
        addAnimation(Direction.Facing270, 0, 3);
        addAnimation(Direction.Facing285, 1, 3);
        addAnimation(Direction.Facing300, 2, 3);
        addAnimation(Direction.Facing315, 3, 3);
        addAnimation(Direction.Facing330, 4, 3);
        addAnimation(Direction.Facing345, 5, 3);
        
        addAnimation(Direction.Crash, 1, 4);
        
        //make sure appropriate animation is displayed
        correctAnimation();
        
        //reset
        reset();
    }
    
    public final void reset()
    {
        //reset tracker
        getTracker().reset();
        
        //reset handicap
        getAttributes().reset();
        
        //reset facing angle as well
        setAngle(Math.toRadians(START_ANGLE));
        
        //stop turning and accelerating
        setTurnLeft(false);
        setTurnRight(false);
        setAccelerate(false);
        
        //then correct the animtation
        correctAnimation();
        
        //reset velocity
        resetVelocity();
        
        //reset turn count
        this.count = 0;
    }
    
    /**
     * Set the car rank
     * @param rank The position the car is in
     */
    public void setRank(final int rank)
    {
        this.rank = rank;
    }
    
    /**
     * Get the car rank
     * @return The position the car is in 1st, 2nd, 3rd represented as an Integer
     */
    public int getRank()
    {
        return this.rank;
    }
    
    /**
     * Get the cars attributes
     * @return The attributes object containing speed/accelerate/etc...
     */
    public Attributes getAttributes()
    {
        return this.attributes;
    }
    
    public void setName(final String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Get the track tracker
     * @return The object representing the progress for this car in a race
     */
    public TrackProgress getTracker()
    {
        return this.tracker;
    }
    
    /**
     * Is this car controlled by a human
     * @return true if this car is controlled by a human, false otherwise
     */
    public boolean isHuman()
    {
        return this.human;
    }
    
    /**
     * Do we render this car?
     * @return true if yes, false otherwise
     */
    protected boolean hasRender()
    {
        return this.render;
    }
    
    /**
     * Set this car to be drawn on screen.
     * @param render true if yes, false otherwise
     */
    protected void setRender(final boolean render)
    {
        this.render = render;
    }
    
    protected void setTurnLeft(final boolean turnLeft)
    {
        this.turnLeft = turnLeft;
    }
    
    protected boolean isTurningLeft()
    {
        return this.turnLeft;
    }
    
    protected void setTurnRight(final boolean turnRight)
    {
        this.turnRight = turnRight;
    }
    
    protected boolean isTurningRight()
    {
        return this.turnRight;
    }
    
    protected void turnLeft()
    {
        if (count++ == getAttributes().getTurnCount())
        {
            turn(-Math.toRadians(TURN_INTERVAL));
            count = 0;
        }
    }
    
    protected void turnRight()
    {
        if (count++ == getAttributes().getTurnCount())
        {
            turn(Math.toRadians(TURN_INTERVAL));
            count = 0;
        }
    }
    
    /**
     * Assign car color which will be shown on mini map
     * @param carColor The desired car color
     */
    protected void setCarColor(final Color carColor)
    {
        this.carColor = carColor;
    }
    
    protected Color getCarColor()
    {
        return this.carColor;
    }
    
    protected final void addAnimation(final Direction direction, final int col, final int row)
    {
        try
        {
            if (getSpriteSheet().hasAnimation(direction))
                throw new Exception("Animation has already been added: " + direction.toString());
            
            getSpriteSheet().add(new Animation(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT, 0), direction);
            
            //default an animation if one is not set
            if (getSpriteSheet().getCurrent() == null)
            {
                getSpriteSheet().setCurrent(direction);
                setDimensions();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    /**
     * Get the angle the car is facing
     * @return Angle in Radians
     */
    protected double getAngle()
    {
        return this.angle;
    }
    
    /**
     * Assign the angle
     * @param angle Angle in Radians
     */
    private void setAngle(final double angle)
    {
        this.angle = angle;
    }
    
    protected boolean hasAccelerate()
    {
        return this.accelerate;
    }
    
    protected void setAccelerate(final boolean accelerate)
    {
        this.accelerate = accelerate;
    }
    
    /**
     * Determine the direction based on the existing velocity, facing angle and speed of car.
     */
    private void calculateVelocity()
    {
        //if we are moving calculate velocity
        if (hasAccelerate())
        {
            //set the direction to head in
            setVelocityX(getVelocityX() + (getAttributes().getSpeed() * Math.cos(getAngle())));
            setVelocityY(getVelocityY() + (getAttributes().getSpeed() * Math.sin(getAngle())));
        }
        else
        {
            //if not accelerating slow down speed
            getAttributes().setSpeed(getAttributes().getSpeed() * Attributes.SPEED_DECELERATE);
        }
    }
    
    @Override
    public void setLocation(final Rectangle screen)
    {
        //set location in center of screen
        super.setLocation(screen);
        
        //offset from center depending on object dimensions
        super.setX(getX() - (getWidth() / 2));
        super.setY(getY() - (getHeight() / 2));
    }
    
    /**
     * Make sure the angle stays between the 0 - 360 degree range, and here is doing the same for radians
     */
    private void correctAngle()
    {
        //this is to keep the angle within range
        if (getAngle() > (2 * Math.PI))
            setAngle(getAngle() - (2 * Math.PI));
        if (getAngle() < 0)
            setAngle(getAngle() + (2 * Math.PI));
    }
    
    /**
     * Get the current facing angle
     * @return The facing angle in degrees rounded to the nearest degree.
     */
    protected double getFacingAngle()
    {
        return Math.round(Math.toDegrees(getAngle()));
    }
    
    /**
     * Assign the appropriate animation based on the angle
     */
    private void correctAnimation()
    {
        //round to nearest degree
        final double degrees = getFacingAngle();
        
        try
        {
            if (degrees == 0)
            {
                getSpriteSheet().setCurrent(Direction.Facing270);
            }
            else if (degrees == 15)
            {
                getSpriteSheet().setCurrent(Direction.Facing285);
            }
            else if (degrees == 30)
            {
                getSpriteSheet().setCurrent(Direction.Facing300);
            }
            else if (degrees == 45)
            {
                getSpriteSheet().setCurrent(Direction.Facing315);
            }
            else if (degrees == 60)
            {
                getSpriteSheet().setCurrent(Direction.Facing330);
            }
            else if (degrees == 75)
            {
                getSpriteSheet().setCurrent(Direction.Facing345);
            }
            else if (degrees == 90)
            {
                getSpriteSheet().setCurrent(Direction.Facing000);
            }
            else if (degrees == 105)
            {
                getSpriteSheet().setCurrent(Direction.Facing015);
            }
            else if (degrees == 120)
            {
                getSpriteSheet().setCurrent(Direction.Facing030);
            }
            else if (degrees == 135)
            {
                getSpriteSheet().setCurrent(Direction.Facing045);
            }
            else if (degrees == 150)
            {
                getSpriteSheet().setCurrent(Direction.Facing060);
            }
            else if (degrees == 165)
            {
                getSpriteSheet().setCurrent(Direction.Facing075);
            }
            else if (degrees == 180)
            {
                getSpriteSheet().setCurrent(Direction.Facing090);
            }
            else if (degrees == 195)
            {
                getSpriteSheet().setCurrent(Direction.Facing105);
            }
            else if (degrees == 210)
            {
                getSpriteSheet().setCurrent(Direction.Facing120);
            }
            else if (degrees == 225)
            {
                getSpriteSheet().setCurrent(Direction.Facing135);
            }
            else if (degrees == 240)
            {
                getSpriteSheet().setCurrent(Direction.Facing150);
            }
            else if (degrees == 255)
            {
                getSpriteSheet().setCurrent(Direction.Facing165);
            }
            else if (degrees == 270)
            {
                getSpriteSheet().setCurrent(Direction.Facing180);
            }
            else if (degrees == 285)
            {
                getSpriteSheet().setCurrent(Direction.Facing195);
            }
            else if (degrees == 300)
            {
                getSpriteSheet().setCurrent(Direction.Facing210);
            }
            else if (degrees == 315)
            {
                getSpriteSheet().setCurrent(Direction.Facing225);
            }
            else if (degrees == 330)
            {
                getSpriteSheet().setCurrent(Direction.Facing240);
            }
            else if (degrees == 345)
            {
                getSpriteSheet().setCurrent(Direction.Facing255);
            }
            else if (degrees == 360)
            {
                getSpriteSheet().setCurrent(Direction.Facing270);
            }
            else
            {
                //if the angle isn't caught and we are debgging, we will throw an exception
                if (Shared.DEBUG)
                    throw new Exception("angle not caught here: " + degrees);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Update the location of the car
     * @param columns The number of columns in the current track, make sure we stay within bounds
     * @param rows The number of columns in the current track, make sure we stay within bounds
     */
    private void updateLocation(final int columns, final int rows)
    {
        //store the angle which is used to display the correct animation on screen
        final double tmpAngle = getAngle();
    
        //update the angle as it should be on mini-map
        setAngle(getAngle() + Math.toRadians(135));
        
        //calculate velocity
        calculateVelocity();
        
        //update location on mini-map
        super.setCol(getCol() + getVelocityX());
        super.setRow(getRow() + getVelocityY());
        
        //make sure the car stays within the track boundary
        if (getCol() < 0)
            setCol(0);
        if (getCol() >= columns)
            setCol(columns - 1);
        if (getRow() < 0)
            setRow(0);
        if (getRow() >= rows)
            setRow(rows - 1);
        
        //restore facing angle
        this.setAngle(tmpAngle);
    }
    
    
    /**
     * Update basic elements of the car.<br>
     * 1. Mini-map location<br>
     * 2. Maximum Speed depending on mini-map location<br>
     * 3. Basic turning functions<br>
     * 4. Manage track progress for this car in a race
     * @param track The current track in play
     * @param time The number of nanoseconds per update
     */
    protected void updateBasicElements(final Track track, final long time)
    {
        //update location of the car
        updateLocation(track.getColumns(), track.getRows());
        
        if (hasAccelerate())
        {
            //apply gravity to slow down the velocity
            setVelocityX(getVelocityX() * Attributes.VELOCITY_DECREASE_RATE);
            setVelocityY(getVelocityY() * Attributes.VELOCITY_DECREASE_RATE);
        }
        else
        {
            //apply less gravity when not accelerating
            setVelocityX(getVelocityX() * Attributes.VELOCITY_DECREASE_RATE_OTHER);
            setVelocityY(getVelocityY() * Attributes.VELOCITY_DECREASE_RATE_OTHER);
        }
        
        //set maximum speed based on car location
        getAttributes().setMaxSpeed(track, this);
        
        //manage the speed of the car if accelerating
        if (hasAccelerate())
            getAttributes().checkSpeed();
        
        //manage direction turning
        if (isTurningRight())
        {
            turnRight();
        }
        else if (isTurningLeft())
        {
            turnLeft();
        }
        
        //manage the race progress for this car
        getTracker().updateProgress(track, this, time);
    }
    
    /**
     * Get the way point location
     * @param track The current track we are racing on
     * @return The location of the current way point
     */
    protected Cell getWayPointLocation(final Track track)
    {
        return getTracker().getCheckPointLocation(track);
    }
    
    /**
     * All cars will need logic to update
     * @param engine Object containing all game elements
     */
    @Override
    public abstract void update(final Engine engine) throws Exception;
    
    /**
     * Turn the car and update the correct animation
     * @param angle The angle in radians
     */
    private void turn(final double angle)
    {
        //set the new angle
        setAngle(getAngle() + angle);
        
        //make sure the angle stays within range
        correctAngle();
        
        //make sure appropriate animation is displayed for the new angle
        correctAnimation();
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw car animation
        super.draw(graphics);
    }
}