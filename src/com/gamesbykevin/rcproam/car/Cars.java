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
    
    //the amount of checkpoints allowed before applying handicap 
    private static final int HANDICAP_LIMIT = 2;
    
    //do we have cpu assistance enabled
    private boolean handicap = false;
    
    //do we check collision of cars
    private boolean checkCollision = false;
    
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
        
        //reset track progress for the cars, etc...
        for (int i = 0; i < cars.size(); i++)
        {
            cars.get(i).getTracker().reset();
            cars.get(i).getAttributes().reset();
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
     * Set check collision
     * @param checkCollision if true, the cars will stop if they run into another, false no collision is checked
     */
    public void setCheckCollision(final boolean checkCollision)
    {
        this.checkCollision = checkCollision;
    }
    
    /**
     * Should we check cars for collision
     * @return true - yes, false - no
     */
    private boolean doCheckCollision()
    {
        return this.checkCollision;
    }
    
    /**
     * Set the handicap setting.<br>
     * If the handicap is enabled the following logic will take place:<br>
     * 1. The cpu cars will drive faster for each car the human is beating by a large margin
     * 2. The cpu cars will drive slower for each car that is beating the human by a large margin
     * @param handicap True if cpu assistance is enabled, false otherwise
     */
    public void setHandicap(final boolean handicap)
    {
        this.handicap = handicap;
    }
    
    /**
     * Is handicap mode enabled
     * @return true if enabled, false otherwise
     */
    public boolean hasHandicap()
    {
        return this.handicap;
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
            adjustCars(engine);
            
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

                //are we checking for collision
                if (doCheckCollision())
                {
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
     * Adjust the cars.<br>
     * Place the cpu cars in perspective to the human car<br><br>
     * Apply handicap (if enabled)<br>
     * The handicap will slow the cpu cars that are at least 2 check points ahead of the human.<br>
     * The handicap will speed up the cpu cars that are at least 2 check points behind the human.
     * @param engine Object containing all game elements
     * @throws Exception If there is no human car an exception will be thrown
     */
    private void adjustCars(final Engine engine) throws Exception
    {
        //the current map used
        final StaticMap map = engine.getManager().getMaps().getMap();

        //the screen where gameplay will take place
        final Rectangle screen = engine.getManager().getWindow();
        
        //get the human controlled car
        final Car human = getHuman();
        
        //get the coordinates of the human car so we can determine where the cpu should be rendered
        final double humanX = map.getAdjustedX(human, screen);
        final double humanY = map.getAdjustedY(human, screen);
        
        for (int i = 0; i < cars.size(); i++)
        {
            Car car = cars.get(i);
            
            if (!car.isHuman())
            {
                if (Shared.DEBUG)
                {
                    //if not on road display notification
                    if (!map.getTrack().isRoad(car))
                        System.out.println("Car went off road: " + car.getName());
                }
                
                //get the coordinates of the specified cpu car
                final double x = map.getAdjustedX(car, screen);
                final double y = map.getAdjustedY(car, screen);
                
                //temporary place the car where the human is
                car.setLocation(human);
                
                //now set the x,y based on the difference from the human
                car.setX(car.getX() + (humanX - x));
                car.setY(car.getY() + (humanY - y));
                
                //if handicap is enabled
                if (handicap)
                {
                    //if the car is ahead of the human
                    if (car.getTracker().getRaceProgress() > human.getTracker().getRaceProgress())
                    {
                        //if the cpu car is further from the human than the handicap limit, slow down the cpu car
                        if (car.getTracker().getRaceProgress() - human.getTracker().getRaceProgress() > HANDICAP_LIMIT)
                        {
                            car.getAttributes().applyHandicap(true);
                        }
                        else
                        {
                            //if not too far away disable handicap mode
                            car.getAttributes().disableHandicap();
                        }
                    }
                    else
                    {
                        //if the human is further from the cpu than the handicap limit, speed up the cpu
                        if (human.getTracker().getRaceProgress() - car.getTracker().getRaceProgress() > HANDICAP_LIMIT)
                        {
                            car.getAttributes().applyHandicap(false);
                        }
                        else
                        {
                            //if not too far away disable handicap mode
                            car.getAttributes().disableHandicap();
                        }
                    }
                }
            }
            
            //we only want to draw the car if it is on the screen
            car.setRender(screen.contains(car.getX(), car.getY()));
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //first order the cars to be rendered in the appropriate order
        sortCars(true);

        //then draw the cars
        for (int i = 0; i < cars.size(); i++)
        {
            //only draw the cars that are on the screen, otherwise it is pointless
            if (cars.get(i).hasRender())
                cars.get(i).render(graphics);
        }
    }
    
    /**
     * Sort the cars.<br>
     * @param byLocation If true, the cars will be sorted by location, else sort by rank
     */
    private void sortCars(final boolean byLocation)
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
            
            //check each car
            for (int i = 0; i < cars.size() - j; i++) 
            {
                //check the 2 cars next to each other in list
                Car car1 = cars.get(i);
                Car car2 = cars.get(i + 1);
                
                //do we switch the cars
                boolean swap;
                
                if (byLocation)
                {
                    //sort the car by the location
                    swap = car1.getRow() > car2.getRow() || (int)car1.getRow() >= (int)car2.getRow() && car1.getCol() > car2.getCol();
                }
                else
                {
                    //sort the cars by the race progress
                    swap = car2.getTracker().getRaceProgress() > car1.getTracker().getRaceProgress();
                }
                
                //if we are to swap cars
                if (swap)
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
        sortCars(false);
        
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