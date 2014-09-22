package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.shared.IElement;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Car extends Sprite implements Disposable, IElement
{
    //frame of car
    private Polygon frame;
    
    //the angle the object should be facing in radians NOT degrees, the facing direction here is WEST
    private double angle = Math.toRadians(0);
    
    //things a car can do
    private boolean turnLeft = false;
    private boolean turnRight = false;
    private boolean accelerate = false;
    private boolean attack = false;
    
    //how fast we can turn
    protected final double TURN_RATE = .05;
    
    //how fast we can move
    private final double DEFAULT_SPEED_RATE = .7;
    
    //slow down rate
    private final double SPEED_DECELERATE = 0.850;
    
    //size of the car
    private static final int SIZE = 10;
    
    //relative coordinates for the body of car
    private final int[] XPOINTS = {-SIZE, SIZE, SIZE, -SIZE};
    private final int[] YPOINTS = {-SIZE, -SIZE, SIZE, SIZE};
    
    //dimensions of car
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    
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
    
    public Car()
    {
        super();
        
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
        
        //default facing angle
        setAngle(Math.toRadians(45));
    }
    
    protected void addAnimation(final Direction direction, final int col, final int row)
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
    
    /**
     * Determine the speed of car and update the location
     */
    private void calculateVelocity()
    {
        //if we are moving calculate velocity
        if (accelerate)
        {
            setVelocityX(getVelocityX() + (DEFAULT_SPEED_RATE * Math.cos(getAngle())));
            setVelocityY(getVelocityY() + (DEFAULT_SPEED_RATE * Math.sin(getAngle())));
        }
        
        //if we aren't moving slow down the speed
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
     * Assign the appropriate animation based on angle facing
     */
    private void correctAnimation() throws Exception
    {
        //convert to degrees
        double degrees = Math.toDegrees(getAngle());
        
        //determine the animation based on the angle facing
        if (degrees >= 0 && degrees < 15)
        {
            getSpriteSheet().setCurrent(Direction.Facing270);
        }
        else if (degrees >= 15 && degrees < 30)
        {
            getSpriteSheet().setCurrent(Direction.Facing285);
        }
        else if (degrees >= 30 && degrees < 45)
        {
            getSpriteSheet().setCurrent(Direction.Facing300);
        }
        else if (degrees >= 45 && degrees < 60)
        {
            getSpriteSheet().setCurrent(Direction.Facing315);
        }
        else if (degrees >= 60 && degrees < 75)
        {
            getSpriteSheet().setCurrent(Direction.Facing330);
        }
        else if (degrees >= 75 && degrees < 90)
        {
            getSpriteSheet().setCurrent(Direction.Facing345);
        }
        else if (degrees >= 90 && degrees < 105)
        {
            getSpriteSheet().setCurrent(Direction.Facing000);
        }
        else if (degrees >= 105 && degrees < 120)
        {
            getSpriteSheet().setCurrent(Direction.Facing015);
        }
        else if (degrees >= 120 && degrees < 135)
        {
            getSpriteSheet().setCurrent(Direction.Facing030);
        }
        else if (degrees >= 135 && degrees < 150)
        {
            getSpriteSheet().setCurrent(Direction.Facing045);
        }
        else if (degrees >= 150 && degrees < 165)
        {
            getSpriteSheet().setCurrent(Direction.Facing060);
        }
        else if (degrees >= 165 && degrees < 180)
        {
            getSpriteSheet().setCurrent(Direction.Facing075);
        }
        else if (degrees >= 180 && degrees < 195)
        {
            getSpriteSheet().setCurrent(Direction.Facing090);
        }
        else if (degrees >= 195 && degrees < 210)
        {
            getSpriteSheet().setCurrent(Direction.Facing105);
        }
        else if (degrees >= 210 && degrees < 225)
        {
            getSpriteSheet().setCurrent(Direction.Facing120);
        }
        else if (degrees >= 225 && degrees < 240)
        {
            getSpriteSheet().setCurrent(Direction.Facing135);
        }
        else if (degrees >= 240 && degrees < 255)
        {
            getSpriteSheet().setCurrent(Direction.Facing150);
        }
        else if (degrees >= 255 && degrees < 270)
        {
            getSpriteSheet().setCurrent(Direction.Facing165);
        }
        else if (degrees >= 270 && degrees < 285)
        {
            getSpriteSheet().setCurrent(Direction.Facing180);
        }
        else if (degrees >= 285 && degrees < 300)
        {
            getSpriteSheet().setCurrent(Direction.Facing195);
        }
        else if (degrees >= 300 && degrees < 315)
        {
            getSpriteSheet().setCurrent(Direction.Facing210);
        }
        else if (degrees >= 315 && degrees < 330)
        {
            getSpriteSheet().setCurrent(Direction.Facing225);
        }
        else if (degrees >= 330 && degrees < 345)
        {
            getSpriteSheet().setCurrent(Direction.Facing240);
        }
        else if (degrees >= 345 && degrees < 361)
        {
            getSpriteSheet().setCurrent(Direction.Facing255);
        }
        else
        {
            throw new Exception("angle not caught here: " + degrees);
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
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            turnRight = true;
        }
        else if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            turnLeft = true;
        }
        else if (keyboard.hasKeyPressed(KeyEvent.VK_A))
        {
            accelerate = true;
        }
        else if (keyboard.hasKeyPressed(KeyEvent.VK_S))
        {
            attack = true;
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            turnRight = false;
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            turnLeft = false;
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_A))
        {
            accelerate = false;
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_S))
        {
            attack = false;
        }
        
        if (turnRight)
        {
            setAngle(getAngle() + TURN_RATE);
            
            System.out.println("Turning right");
        }
        else if (turnLeft)
        {
            setAngle(getAngle() - TURN_RATE);
            
            System.out.println("Turning left");
        }
        
        //this is to keep the angle within range
        if (getAngle() > (2 * Math.PI))
            setAngle(getAngle() - (2 * Math.PI));
        if (getAngle() < 0)
            setAngle(getAngle() + (2 * Math.PI));
        
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