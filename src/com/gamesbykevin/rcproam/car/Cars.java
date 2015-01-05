package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.rcproam.actor.*;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.StaticMap;
import com.gamesbykevin.rcproam.resources.GameAudio;
import com.gamesbykevin.rcproam.shared.IElement;
import com.gamesbykevin.rcproam.shared.Shared;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will be the container for all the cars in the game
 * @author GOD
 */
public class Cars implements Disposable, IElement
{
    //the container for the cars
    private List<Car> cars;
    
    //do we freeze the cars
    private boolean pause = false;
    
    //this timer will prevent the cars from racing for 5 seconds
    private Timer timer;
    
    //the start delay is 5 seconds
    private static final long START_DELAY = Timers.toNanoSeconds(5010L);
    
    //the distance between cars to detect collision
    private static final double COLLISION_DISTANCE = 0.5;
    
    public Cars()
    {
        //create new list to hold the cars
        this.cars = new ArrayList<>();
        
        //create new timer
        this.timer = new Timer(START_DELAY);
    }
    
    /**
     * Reset the race delay timer as well as the track progress for the cars.<br>
     * Call this method when starting a new race
     */
    public void reset()
    {
        //reset race delay timer
        this.timer.reset();
        
        //do not pause
        this.pause = false;
        
        //reset track progress for the cars
        for (int i = 0; i < cars.size(); i++)
        {
            cars.get(i).getTracker().reset();
        }
    }
    
    /**
     * Get the car at the specified index.
     * @param index The car that we want
     * @return The car that is contained in the list
     */
    public Car get(final int index)
    {
        return this.cars.get(index);
    }
    
    /**
     * Get the size of the cars in play
     * @return The total number of cars in play
     */
    public int getSize()
    {
        return this.cars.size();
    }
    
    /**
     * Add human controlled car.<br>Note there can only be 1 human car
     * @param image The sprite sheet for the car
     * @param color The color of the car to be displayed on mini map
     * @param name The name to identify this car to the user
     * @throws Exception If more than 1 human car is added an exception will be thrown
     */
    public void addHuman(final Image image, final Color color, final String name) throws Exception
    {
        //if we already have a human car and are attemtping to add another
        if (hasHuman())
            throw new Exception("Only 1 human car is allowed");
        
        //create human controlled car
        Car car = new Human();
        
        //assign car color for minimap
        car.setCarColor(color);

        //set the car name
        car.setName(name);
        
        //assign spite image
        car.setImage(image);

        //add car to list
        add(car);
    }
    
    /**
     * Add CPU controlled car
     * @param image The sprite sheet for the car
     * @param color The color of the car to be displayed on mini map
     * @param name The name to identify this car to the user
     * @param random Object used to make random decisions
     */
    public void addCpu(final Image image, final Color color, final String name, final Random random) throws Exception
    {
        //create AI controlled car
        Car car = new Cpu(random);
        
        //assign car color for minimap
        car.setCarColor(color);

        //set the car name
        car.setName(name);
        
        //assign spite image
        car.setImage(image);

        //add car to list
        add(car);
    }
    
    /**
     * Is there a human car?
     * @return true if at least 1 car is controlled by a human, false otherwise
     */
    public boolean hasHuman()
    {
        for (int i = 0; i < cars.size(); i++)
        {
            //if this car is controlled by human return true
            if (cars.get(i).isHuman())
                return true;
        }
        
        return false;
    }
    
    /**
     * Get the car controlled by a human
     * @return Car controlled by human, if there are no human cars null will be returned
     * @throws Exception if there are no human cars
     */
    public Car getHuman() throws Exception
    {
        for (int i = 0; i < cars.size(); i++)
        {
            //if this car is controlled by human we found our car
            if (cars.get(i).isHuman())
                return cars.get(i);
        }
        
        //no human cars were found, throw exception
        throw new Exception("There are no human cars");
    }
    
    /**
     * Add car to list
     * @param car The car we want to add
     * @throws Exception if this car does not have a name assigned
     */
    private void add(final Car car) throws Exception
    {
        if (car.getName() == null || car.getName().trim().length() < 1)
            throw new Exception("Car must have a name assigned before adding to the list");
        
        cars.add(car);
    }
    
    @Override
    public void dispose()
    {
        if (cars != null)
        {
            for (int i = 0; i < cars.size(); i++)
            {
                cars.get(i).dispose();
                cars.set(i, null);
            }
            
            cars.clear();
            cars = null;
        }
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        if (!timer.hasStarted())
        {
            //reset the cars
            reset();

            //play race start sound
            engine.getResources().playGameAudio(GameAudio.Keys.RaceStart);
            
            //update timer
            timer.update(engine.getMain().getTime());
        }
        else
        {
            //place the cars in perspective to the human racer
            adjustCarLocation(engine);
            
            if (!timer.hasTimePassed())
            {
                //update timer
                timer.update(engine.getMain().getTime());
                
                //do not continue yet
                return;
            }
            
            //if paused do not update any cars
            if (pause)
                return;

            //did at least 1 car complete a lap
            boolean lapCompleted = false;
            
            //has at least 1 car collided with another
            boolean collision = false;
            
            for (int i = 0; i < cars.size(); i++)
            {
                Car car = cars.get(i);

                //get the amount of laps the car has completed
                final int laps = car.getTracker().getLaps();

                //store the previous location of the car
                final double col = car.getCol();
                final double row = car.getRow();
                
                //update the car
                car.update(engine);

                //if we have collision
                if (hasCollision(car))
                {
                    //only flag collisions that are rendered on screen
                    if (car.hasRender())
                        collision = true;
                    
                    //move the car back to the previous place
                    car.setCol(col);
                    car.setRow(row);
                }
                
                //if the current number of laps has increased we have completed a lap
                if (car.getTracker().getLaps() > laps)
                {
                    //get the number of laps required for the current map we are racing
                    final int required = engine.getManager().getMaps().getMap().getLaps();

                    //if this car has completed the required amoun of laps for the race
                    if (car.getTracker().getLaps() >= required)
                    {
                        //stop all sound
                        engine.getResources().stopAllSound();

                        //play race complete sound
                        engine.getResources().playGameAudio(GameAudio.Keys.RaceFinish);

                        //freeze cars
                        pause = true;
                    }
                    else
                    {
                        //verify the car is drawn on screen before we play the lap completed sound effect
                        if (car.hasRender())
                        {
                            //a car has completed a lap
                            lapCompleted = true;
                        }
                    }

                    if (Shared.DEBUG)
                        System.out.println("Lap " + car.getTracker().getLaps() + " of " + required + " (Car - " + car.getName() + ")");

                    //we have a winner exit method
                    if (pause)
                        return;
                }
            }
            
            //if a car completed a lap play sound effect
            if (lapCompleted)
                engine.getResources().playGameAudio(GameAudio.Keys.Lap);
            
            //if collision honk car horn
            if (collision)
                engine.getResources().playGameAudio(GameAudio.Keys.Horn);
        }
    }
    
    /**
     * Do any of the cars collide with the current car?
     * 
     * @param car Car we want to check for collision
     * @return true if a car is close enough to the car, false otherwise
     */
    private boolean hasCollision(final Car tmp)
    {
        for (int i = 0; i < cars.size(); i++)
        {
            //get the current car
            Car car = cars.get(i);
            
            //don't check self
            if (car.getId() == tmp.getId())
                continue;
            
            //if the car is close enough we have collision
            if (Cell.getDistance(car, tmp) <= COLLISION_DISTANCE)
                return true;
        }
        
        //no collision was found
        return false;
    }
    
    /**
     * Place the cars on the screen accordingly so they are rendered in perspective to the human driver
     * @param engine Object containing all game elements
     * @throws Exception If there is no human car an exception will be thrown
     */
    private void adjustCarLocation(final Engine engine) throws Exception
    {
        //the current map used
        final StaticMap map = engine.getManager().getMaps().getMap();

        //the screen where gameplay will take place
        final Rectangle screen = engine.getManager().getWindow();
        
        //get the coordinates of the human car so we can determine where the cpu should be rendered
        final double humanX = map.getAdjustedX(getHuman(), screen);
        final double humanY = map.getAdjustedY(getHuman(), screen);
        
        for (int i = 0; i < cars.size(); i++)
        {
            Car car = cars.get(i);
            
            if (!car.isHuman())
            {
                //get the coordinates of the specified cpu car
                final double x = map.getAdjustedX(car, screen);
                final double y = map.getAdjustedY(car, screen);
                
                //place the car in the center of the screen first
                car.setLocation(screen);
                
                //now set the x,y based on the difference from the human
                car.setX(car.getX() + (humanX - x));
                car.setY(car.getY() + (humanY - y));
            }
            
            //we only want to draw the car if it is on the screen
            car.setRender(screen.contains(car.getX(), car.getY()));
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //first order the cars to be rendered in the appropriate order
        sortCarLocation();

        //then draw the cars
        for (int i = 0; i < cars.size(); i++)
        {
            //only draw the cars that are on the screen, otherwise it is pointless
            if (cars.get(i).hasRender())
                cars.get(i).render(graphics);
        }
    }
    
    /**
     * Sort the cars based on column, row so they are rendered appropriately in isometric fashion
     */
    private void sortCarLocation()
    {
        //were objects swapped
        boolean swapped = true;
        int j = 0;
        Car tmp;
        
        //continue as long as objects have been swapped
        while (swapped) 
        {
            swapped = false;
            j++;
            
            for (int i = 0; i < cars.size() - j; i++) 
            {
                Car car1 = cars.get(i);
                Car car2 = cars.get(i + 1);
                if ((int)car1.getRow() > (int)car2.getRow() || (int)car1.getRow() >= (int)car2.getRow() && car1.getCol() > car2.getCol())
                {
                    //get temp car object
                    tmp = cars.get(i);
                    
                    //swap objects
                    cars.set(i, cars.get(i + 1));
                    cars.set(i + 1, tmp);
                    
                    //flag that objects are swapped
                    swapped = true;
                }
            }
        }
    }
    
    /**
     * Sort the cars so we know what place they are in 1st, 2nd, 3rd, etc.....
     */
    private void sortCarRank()
    {
        //were objects swapped
        boolean swapped = true;
        int j = 0;
        Car tmp;
        
        //continue as long as objects have been swapped
        while (swapped) 
        {
            swapped = false;
            j++;
            
            for (int i = 0; i < cars.size() - j; i++) 
            {
                Car car1 = cars.get(i);
                Car car2 = cars.get(i + 1);
                if (car2.getTracker().getRaceProgress() > car1.getTracker().getRaceProgress())
                {
                    //get temp car object
                    tmp = cars.get(i);
                    
                    //swap objects
                    cars.set(i, cars.get(i + 1));
                    cars.set(i + 1, tmp);
                    
                    //flag that objects are swapped
                    swapped = true;
                }
            }
        }
    }
    
    /**
     * Draw the cars on the mini-map
     * @param graphics 
     */
    public void renderMiniMapLocations(final Graphics graphics)
    {
        for (int i = 0; i < cars.size(); i++)
        {
            //get the current car
            Car car = cars.get(i);
            
            //set the color based on the car color
            graphics.setColor(car.getCarColor());
            
            //draw the color as a 1 x 1 pixel
            graphics.drawRect((int)car.getCol(), (int)car.getRow(), 1, 1);
        }
    }
    
    /**
     * Draw the human time as well as the rank of the cars 1st, 2nd, etc...
     * @param graphics
     * @param x starting x-coordinate
     * @param y starting y-coordinate
     */
    public void renderTimeInfo(final Graphics graphics, final int x, final int y, final int laps)
    {
        try
        {
            getHuman().getTracker().renderLapDescription(graphics, x, y, laps);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void renderLeaderboard(final Graphics graphics, final int x, final int y)
    {
        //sort the cars by rank
        sortCarRank();
        
        //the color of the text is white
        graphics.setColor(Color.WHITE);
        
        //get the font height
        final int fontHeight = graphics.getFontMetrics().getHeight();
        
        graphics.drawString("Leaderboard:", x, y);
        
        for (int i = 0; i < cars.size(); i++)
        {
            graphics.drawString((i+1) + " - " + cars.get(i).getName(), x, y + (fontHeight * (i+1)));
        }
    }
}