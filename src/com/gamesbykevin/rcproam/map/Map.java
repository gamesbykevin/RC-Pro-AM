package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.car.Car;

import java.awt.Graphics;
import java.awt.Image;

public final class Map extends Sprite implements Disposable
{
    @Override
    public void setImage(final Image image)
    {
        super.setImage(image);
        super.setDimensions(super.getImage());
    }
    
    /**
     * Set the location of the map based on the central car's perspective.<br>
     * The velocity of the car will be applied to the map.<br>
     * Then the location of the map will be updated.
     * 
     * @param car The car that is the central perspective of this map
     */
    public void setLocation(final Car car)
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
