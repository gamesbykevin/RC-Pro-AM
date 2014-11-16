package com.gamesbykevin.rcproam.car;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.actor.*;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.shared.IElement;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

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
     * Add default car to list that will be controlled by human
     * @param window The area where the game will be played
     * @param image The sprite sheet for the car
     */
    public void add(final Rectangle window, final Image image)
    {
        if (cars.isEmpty())
        {
            try
            {
                Cpu car = new Cpu();
                car.setCarColor(Color.RED);
                car.setLocation(window);
                car.setImage(image);

                //add car to list
                add(car);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get the car controlled by a human
     * @return Car controlled by human, if there are no cars null will be returned
     */
    public Car getHuman()
    {
        if (1==1)
            return cars.get(0);
        
        for (int i = 0; i < cars.size(); i++)
        {
            //if this car is controlled by human we found our car
            if (cars.get(i).isHuman())
                return cars.get(i);
        }
        
        //no human cars were found, return null
        return null;
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
    
    @Override
    public void render(final Graphics graphics)
    {
        for (int i = 0; i < cars.size(); i++)
        {
            cars.get(i).render(graphics);
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