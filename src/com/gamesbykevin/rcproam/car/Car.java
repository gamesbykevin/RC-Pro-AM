package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.Track;
import com.gamesbykevin.rcproam.map.TrackTracker;
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
    
    //the number of turns needed to the next 15 degrees
    private int turnCount = (int)(TURN_INTERVAL / TURN_STEP);
    
    //the current count
    private int count = 0;
    
    //things a car can do
    private boolean turnLeft = false;
    private boolean turnRight = false;
    private boolean accelerate = false;
    private boolean attack = false;
    
    //is the car controlled by a human
    private final boolean human;
    
    //how many degrees is each turn between each 15 degree interval
    private static final double TURN_STEP = 3.75;
    
    //each turn will be 15 degrees
    protected static final double TURN_INTERVAL = 15;
    
    //the AI needs to be within a certain angle when moving to the next way point
    protected static final double AI_INTERVAL = (TURN_INTERVAL / 2);
    
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
    protected static final double DEFAULT_MAXIMUM_SPEED_ROAD = 0.0125;
    
    //maximum speed allowed while driving off road
    private static final double DEFAULT_MAXIMUM_SPEED_OFF_ROAD = 0.00125;
    
    //the rate at which you accelerate to the maximum speed
    protected static final double DEFAULT_ACCELERATE_SPEED = 0.00005;
    
    //speed at which we will accelerate
    private double accelerateRate = DEFAULT_ACCELERATE_SPEED;
    
    //velocity slow down rate
    private final double VELOCITY_DECELERATE = 0.9;
    
    //speed slow down rate
    private final double SPEED_DECELERATE = 0.975;
    
    //dimensions of car
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    
    //the velocity of the car moving on the mini-map
    private double mapVelocityX = 0;
    private double mapVelocityY = 0;
    
    //object used to measure progress for a given race
    private TrackTracker tracker;
    
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
    protected Car(final boolean human) throws Exception
    {
        super();
        
        //is the car controlled by a human
        this.human = human;
                
        //the turn step must be a multiple of the turn interval
        if (TURN_INTERVAL % TURN_STEP != 0)
            throw new Exception("The turn step must be a multiple of the turn interval");
        
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
        
        //create new track tracker
        this.tracker = new TrackTracker();
    }
    
    /**
     * Get the track tracker
     * @return The object representing the progress for this car in a race
     */
    public TrackTracker getTracker()
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
        if (count++ == turnCount)
        {
            turn(-Math.toRadians(TURN_INTERVAL));
            count = 0;
        }
    }
    
    protected void turnRight()
    {
        if (count++ == turnCount)
        {
            turn(Math.toRadians(TURN_INTERVAL));
            count = 0;
        }
    }
    
    protected void setAttack(final boolean attack)
    {
        this.attack = attack;
    }
    
    protected boolean isAttacking()
    {
        return this.attack;
    }
    
    /**
     * Assign car color which will be shown on mini map
     * @param carColor The desired car color
     */
    protected void setCarColor(final Color carColor)
    {
        this.carColor = carColor;
    }
    
    private Color getCarColor()
    {
        return this.carColor;
    }
    
    protected void setAccelerateRate(final double accelerateRate)
    {
        this.accelerateRate = accelerateRate;
    }
    
    protected double getAccelerateRate()
    {
        return this.accelerateRate;
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
    
    protected double getMaxRoadSpeed()
    {
        return this.maxRoadSpeed;
    }
    
    /**
     * Set the max speed allowed while driving on road
     * @param maxRoadSpeed 
     */
    protected void setMaxRoadSpeed(final double maxRoadSpeed)
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
     * Determine the direction based on the existing velocity, facing angle and speed of car.
     */
    private void calculateVelocity()
    {
        //if we are moving calculate velocity
        if (hasAccelerate())
        {
            //set the direction to head in
            setVelocityX(getVelocityX() + (getSpeed() * Math.cos(getAngle())));
            setVelocityY(getVelocityY() + (getSpeed() * Math.sin(getAngle())));
        }
        else
        {
            //if not accelerating slow down speed
            setSpeed(getSpeed() * SPEED_DECELERATE);
        }
    }
    
    /**
     * Slow down the speed of the car
     */
    private void applyGravity()
    {
        //slow down speed
        setVelocityX(getVelocityX() * VELOCITY_DECELERATE);
        setVelocityY(getVelocityY() * VELOCITY_DECELERATE);
        
        //also slow down speed on mini-map
        this.mapVelocityX = (mapVelocityX * VELOCITY_DECELERATE);
        this.mapVelocityY = (mapVelocityY * VELOCITY_DECELERATE);
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
     * Update the column, row for the mini-map
     */
    private void updateMiniMapLocation()
    {
        //store the angle
        final double tmpAngle = getAngle();
        
        //assign the temp angle
        setAngle(tmpAngle + Math.toRadians(135));
        
        //store the existing velocity
        final double vx = this.getVelocityX();
        final double vy = this.getVelocityY();
        
        //set the mini-map velocity
        this.setVelocityX(this.mapVelocityX);
        this.setVelocityY(this.mapVelocityY);
        
        //calculate velocity
        calculateVelocity();
        
        //assign the new calculated map velocity
        this.mapVelocityX = this.getVelocityX();
        this.mapVelocityY = this.getVelocityY();
        
        //update location on mini-map
        super.setCol(getCol() + mapVelocityX);
        super.setRow(getRow() + mapVelocityY);
        
        //restore the velocity
        this.setVelocity(vx, vy);
        
        //restore facing angle
        this.setAngle(tmpAngle);
    }
    
    /**
     * Set the maximum speed depending on where the car is on the track
     * @param track The track the car is racing on
     */
    private void setMaxSpeed(final Track track)
    {
        //set the maximum speed depending on where the car is
        if (track.isRoad(getCol(), getRow()))
        {
            setMaxSpeed(getMaxRoadSpeed());
        }
        else
        {
            setMaxSpeed(getMaxOffRoadSpeed());
        }
    }
    
    /**
     * Update basic elements of the car.<br>
     * 1. Mini-map location<br>
     * 2. Maximum Speed depending on mini-map location<br>
     * 3. Basic turning functions<br>
     * 4. Manage track progress for this car in a race
     * @param track The current track in play
     */
    protected void updateBasicElements(final Track track)
    {
        //update location on mini-map
        updateMiniMapLocation();
        
        //set maximum speed based on car location
        setMaxSpeed(track);
        
        //calculate velocity
        calculateVelocity();
        
        //apply gravity
        applyGravity();
        
        //manage the speed of the car
        manageSpeed();
        
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
        getTracker().updateProgress(track, this);
    }
    
    /**
     * Get the way point location
     * @param track The current track we are racing on
     * @return The location of the current way point
     */
    protected Cell getWayPointLocation(final Track track)
    {
        return getTracker().getWayPointLocation(track);
    }
    
    /**
     * Manage the speed of the car.<br>
     * If accelerating we will manage that to ensure it stays within the maximum limit
     */
    protected void manageSpeed()
    {
        if (hasAccelerate())
        {
            //accelerate speed
            setSpeed(getSpeed() + getAccelerateRate());
            
            //make sure we don't go over the maximum speed, only when accelerating
            if (getSpeed() > getMaxSpeed())
                setSpeed(getMaxSpeed());
        }
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
        
        //make sure appropriate animation is displayed
        correctAnimation();
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw car animation
        super.draw(graphics);
    }
    
    protected void renderMapLocation(final Graphics graphics, final int startX, final int startY)
    {
        if (getCarColor() != null)
        {
            graphics.setColor(getCarColor());
            graphics.drawRect(startX + (int)(getCol()), startY + (int)(getRow()), 1, 1);
        }
    }
}