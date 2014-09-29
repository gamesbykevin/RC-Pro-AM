package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.shared.IElement;
import com.gamesbykevin.rcproam.shared.Shared;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Car extends Sprite implements Disposable, IElement
{
    //frame of car
    private Polygon frame;
    
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
    
    //how many degrees is each turn between each 15 degree interval
    private static final double TURN_STEP = 3.75;//1.875;
    
    //each turn will be 15 degrees
    private static final double TURN_INTERVAL = 15;
    
    //speed at which we are moving
    private double speed = 0;
    
    //starting speed
    private static final double STARTING_SPEED = 0.25;
    
    //maximum speed allowed
    private static final double MAXIMUM_SPEED = 0.475;
    
    //the rate at which you accelerate to the maximum speed
    private static final double ACCELERATE_SPEED = 0.001;
    
    //slow down rate while accelerating
    private final double SPEED_DECELERATE = 0.9;
    
    //dimensions of car
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    
    //size of the car
    private static final int SIZE = (WIDTH / 2);
    
    //relative coordinates for the body of car
    private final int[] XPOINTS = {-SIZE, SIZE, SIZE, -SIZE};
    private final int[] YPOINTS = {-SIZE, -SIZE, SIZE, SIZE};
    
    //store the center location
    private double centerX, centerY;
    
    public enum Direction
    {
        Facing000, 
        Facing015, Facing030, Facing045, Facing060, Facing075, Facing090, 
        Facing105, Facing120, Facing135, Facing150, Facing165, Facing180, 
        Facing195, Facing210, Facing225, Facing240, Facing255, Facing270, 
        Facing285, Facing300, Facing315, Facing330, Facing345, Crash
    }
    
    public Car() throws Exception
    {
        super();
        
        //the turn step must be a multiple of the turn interval
        if (TURN_INTERVAL % TURN_STEP != 0)
            throw new Exception("The turn step must be a multiple of the turn interval");
        
        //create frame of car
        this.frame = new Polygon(XPOINTS, YPOINTS, XPOINTS.length);
        
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
    
    private double getAngle()
    {
        return this.angle;
    }
    
    private void setAngle(final double angle)
    {
        this.angle = angle;
    }
    
    private boolean hasAccelerate()
    {
        return this.accelerate;
    }
    
    private void setAccelerate(final boolean accelerate)
    {
        this.accelerate = accelerate;
    }

    private double getSpeed()
    {
        return this.speed;
    }
    
    private void setSpeed(final double speed)
    {
        this.speed = speed;
    }
    
    /**
     * Determine the speed of car and update the location.<br>
     * We will take the facing angle into consideration
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
        
        //keep the velocity from getting too high
        setVelocityX(getVelocityX() * SPEED_DECELERATE);
        setVelocityY(getVelocityY() * SPEED_DECELERATE);
    }
    
    /**
     * Is the (x, y) point inside the polygon
     * @param x x-point
     * @param y y-point
     * @return true if the point is inside the polygon, false otherwise
     */
    public boolean contains(final int x, final int y)
    {
        //is the point inside
        boolean inside = false;
        
        for ( int i = 0, j = frame.xpoints.length - 1 ; i < frame.xpoints.length ; j = i++ )
        {
            if ((frame.ypoints[i] > y) != (frame.ypoints[j] > y) &&
                 x < (frame.xpoints[j] - frame.xpoints[i]) * (y - frame.ypoints[i]) / (frame.ypoints[j] - frame.ypoints[i]) + frame.xpoints[i])
            {
                //odd is inside, even is out
                inside = !inside;
            }
        }

        return inside;
    }
    
    @Override
    public void setLocation(final Rectangle screen)
    {
        super.setLocation(screen);
        
        //store the center location
        this.centerX = getX();
        this.centerY = getY();
        
        updatePolygon();
    }
    
    /**
     * Calculate every point in the polygon based on the angle facing
     */
    public void updatePolygon()
    {
        //calculate every point in the polygon
        for (int i=0; i < frame.xpoints.length; i++)
        {
            final int tmpX = XPOINTS[i];
            final int tmpY = YPOINTS[i];

            //take original (x,y) and determine new (x,y) based on the current angle
            final double newX = (tmpX * Math.cos(getAngle())) - (tmpY * Math.sin(getAngle()));
            final double newY = (tmpX * Math.sin(getAngle())) + (tmpY * Math.cos(getAngle()));

            frame.xpoints[i] = (int)(getX() + newX);
            frame.ypoints[i] = (int)(getY() + newY);
        }
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
     * Assign the appropriate animation based on the angle
     */
    private void correctAnimation()
    {
        //convert to degrees
        final double facingAngle = Math.toDegrees(getAngle());
        
        //round to nearest degree
        final double degrees = Math.round(facingAngle);
        
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
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //calculate velocity
        calculateVelocity();
        
        //update location
        super.update();
        
        //update polygon coordinates
        updatePolygon();
        
        final Keyboard keyboard = engine.getKeyboard();
        
        //can only do one or the other
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            turnRight = true;
            turnLeft = false;
        }
        else if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            turnRight = false;
            turnLeft = true;
        }
        
        //are we just starting to accelerate
        boolean start = (!hasAccelerate());
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_A))
        {
            setAccelerate(true);
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_S))
            attack = true;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            turnRight = false;
            count = 0;
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            turnLeft = false;
            count = 0;
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_A))
            setAccelerate(false);
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_S))
            attack = false;
        
        if (turnRight)
        {
            if (count++ == turnCount)
            {
                turn(Math.toRadians(TURN_INTERVAL));
                count = 0;
            }
        }
        else if (turnLeft)
        {
            if (count++ == turnCount)
            {
                turn(-Math.toRadians(TURN_INTERVAL));
                count = 0;
            }
        }
        
        if (hasAccelerate())
        {
            //if just starting, set starting speed
            if (start)
                setSpeed(STARTING_SPEED);
            
            setSpeed(getSpeed() + ACCELERATE_SPEED);
            
            if (getSpeed() > MAXIMUM_SPEED)
                setSpeed(MAXIMUM_SPEED);
        }
    }
    
    /**
     * Turn the car.
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
        //store original location
        double tmpX = getX();
        double tmpY = getY();
        
        //offset location
        setX(centerX - (getWidth()  / 2));
        setY(centerY - (getHeight() / 2));
        
        //draw animation
        super.draw(graphics);
        
        //restore original location
        setX(tmpX);
        setY(tmpY);
    }
}