package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.resources.GameImages;
import com.gamesbykevin.rcproam.actor.*;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.StaticMap;
import com.gamesbykevin.rcproam.shared.IElement;

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
    
    public Cars()
    {
        this.cars = new ArrayList<>();
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
     * @throws Exception If more than 1 human car is added an exception will be thrown
     */
    public void addHuman(final Image image, final Color color) throws Exception
    {
        //if we already have a human car and are attemtping to add another
        if (hasHuman())
            throw new Exception("Only 1 human car is allowed");
        
        //create human controlled car
        Car car = new Human();
        
        //assign car color for minimap
        car.setCarColor(color);

        //assign spite image
        car.setImage(image);

        //add car to list
        add(car);
    }
    
    /**
     * Add CPU controlled car
     * @param image The sprite sheet for the car
     * @param color The color of the car to be displayed on mini map
     * @param random Object used to make random decisions
     */
    public void addCpu(final Image image, final Color color, final Random random) throws Exception
    {
        //create AI controlled car
        Car car = new Cpu(random);
        
        //assign car color for minimap
        car.setCarColor(color);

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
     */
    private void add(final Car car)
    {
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
        for (int i = 0; i < cars.size(); i++)
        {
            cars.get(i).update(engine);
        }
    }
    
    /**
     * Place the cars on the screen accordingly so they are rendered in perspective to the human driver
     * @param engine Object containing all game elements
     * @throws Exception If there is no human car an exception will be thrown
     */
    public void adjustCarLocation(final Engine engine) throws Exception
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
                
                
                //set car location from perspective to the human
                //car.setX(map.getX() + (screen.width / 2)  - map.getAdjustedX(car, screen));
                //car.setY(map.getY() + (screen.height / 2) - map.getAdjustedY(car, screen));
            }
        }
    }
    
    /**
     * Reset the race progress for all the cars
     */
    public void resetCarProgress()
    {
        for (int i = 0; i < cars.size(); i++)
        {
            cars.get(i).getTracker().reset();
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //first order the cars to be rendered in the appropriate order
        sortCars();

        //then draw the cars
        for (int i = 0; i < cars.size(); i++)
        {
            cars.get(i).render(graphics);
        }
    }
    
    /**
     * Sort the cars based on column, row so they are rendered appropriately in isometric fashion
     */
    private void sortCars()
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
                if (car1.getRow() > car2.getRow() || car1.getRow() >= car2.getRow() && car1.getCol() > car2.getCol())
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
    
    public void renderMiniMapLocations(final Graphics graphics, final int startX, final int startY)
    {
        for (int i = 0; i < cars.size(); i++)
        {
            cars.get(i).renderMapLocation(graphics, startX, startY);
        }
    }
}