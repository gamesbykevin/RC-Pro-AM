package com.gamesbykevin.rcproam.manager;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.rcproam.car.Cars;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.Maps;
import com.gamesbykevin.rcproam.menu.CustomMenu;
import com.gamesbykevin.rcproam.menu.CustomMenu.*;
import com.gamesbykevin.rcproam.resources.*;
import com.gamesbykevin.rcproam.shared.Shared;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //where gameplay occurs
    private Rectangle window;
    
    //the container for our race cars
    private Cars cars;
    
    //the maps of the tracks
    private Maps maps;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //determine if sound is enabled
        final boolean enabled = (Toggle.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.OptionsInGame, OptionKey.Sound)] == Toggle.Off);

        
        //set the audio depending on menu setting
        engine.getResources().setAudioEnabled(enabled);
        
        //set the game window where game play will occur
        setWindow(engine.getMain().getScreen());
        
        //maps = new Maps(engine.getResources().getGameImage(GameImages.Keys.Maps), getWindow());
        //hero.setImage(engine.getResources().getGameImage(GameImages.Keys.Heroes));
        //enemies = new Enemies(engine.getResources().getGameImage(GameImages.Keys.Enemies));
        
        //are we playing random Mode or Campaign
        //random = CustomMenu.Toggle.values()[engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Mode)];
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        if (this.cars == null)
        {
            //create new container for cars
            this.cars = new Cars();
            
            //add human car first
            this.cars.add(getWindow(), engine.getResources().getGameImage(GameImages.Keys.TruckRed), Color.RED, false);
            
            //add cpu car(s)
            this.cars.add(getWindow(), engine.getResources().getGameImage(GameImages.Keys.TruckBlue), Color.BLUE, false);
            this.cars.add(getWindow(), engine.getResources().getGameImage(GameImages.Keys.TruckGreen), Color.GREEN, false);
            this.cars.add(getWindow(), engine.getResources().getGameImage(GameImages.Keys.TruckOrange), Color.ORANGE, false);
        }
        
        if (this.maps == null)
        {
            this.maps = new Maps(engine.getResources());
        }
    }
    
    public Cars getCars()
    {
        return this.cars;
    }
    
    public Maps getMaps()
    {
        return this.maps;
    }
    
    @Override
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    @Override
    public void setWindow(final Rectangle window)
    {
        this.window = new Rectangle(window);
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (window != null)
            window = null;
        
        if (cars != null)
        {
            cars.dispose();
            cars = null;
        }
        
        if (maps != null)
        {
            maps.dispose();
            maps = null;
        }
        
        try
        {
            //recycle objects
            super.finalize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Update all elements
     * @param engine Our game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        if (maps != null)
        {
            if (maps.isLoading())
            {
                maps.update(engine);
                
                //if no longer loading, place car(s)
                if (!maps.isLoading())
                {
                    //assign the track based on the option selection
                    maps.setIndex(engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Track));
                    
                    //place the cars
                    maps.placeCars(cars);
                }
            }
            else
            {
                //update map first
                maps.update(engine);
                
                //now update the cars
                if (cars != null)
                    cars.update(engine);
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        if (maps != null)
        {
            if (maps.isLoading())
            {
                //draw the map
                maps.render(graphics);
            }
            else
            {
                //draw the map
                maps.render(graphics);
            
                //where the mini-map will be drawn
                final int x = 0;
                final int y = 0;
                
                //draw the mini-map
                maps.renderMiniMap(graphics, x, y);

                if (cars != null)
                {
                    //draw where the cars are on the mini-map
                    cars.renderMiniMapLocations(graphics, x, y);
                    
                    //draw the race car
                    cars.render(graphics);
                }
            }
        }
    }
}