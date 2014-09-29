package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.car.Car;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public final class Map extends Sprite implements Disposable
{
    @Override
    public void setImage(final Image image)
    {
        super.setImage(image);
        super.setDimensions(super.getImage());
    }
    
    /**
     * Set the starting location where the race will begin
     * @param x x-coordinate
     * @param y y-coordinate
     * @param r Rectangle where game play will take place
     * @throws Exception if the image is not yet set to object
     */
    public void setStartLocation(final double x, final double y, final Rectangle r) throws Exception
    {
        if (getImage() == null)
            throw new Exception("Must assign image object first before setting the start location.");
        
        super.setLocation(-x + (r.width / 2), -y + (r.height / 2));
        
        System.out.println("x = " + getX() + ", y = " + getY());
    }
    
    /**
     * Set the location of the map based on the central car's perspective.<br>
     * The velocity of the car will be applied to the map.<br>
     * Then the location of the map will be updated.
     * 
     * @param car The car that is the central perspective of this map
     */
    public void updateLocation(final Car car)
    {
        super.setVelocity(car);
        super.update();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
    }
}
