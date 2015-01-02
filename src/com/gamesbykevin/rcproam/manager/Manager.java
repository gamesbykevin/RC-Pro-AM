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
    
    //this timer will prevent the cars from racing for 5 seconds
    private Timer timer;
    
    //the start delay is 5 seconds
    private static final long START_DELAY = Timers.toNanoSeconds(5000L);
    
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
        
        //create new timer
        this.timer = new Timer(START_DELAY);
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        if (this.cars == null)
        {
            //create new container for cars
            this.cars = new Cars();
            
            //add human car first
            this.cars.addHuman(engine.getResources().getGameImage(GameImages.Keys.TruckRed), Color.RED);
            //this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckRed), Color.RED,    engine.getRandom());
            
            //set human in center of screen
            this.cars.getHuman().setLocation(engine.getMain().getScreen());
            
            //add cpu car(s)
            this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckBlue), Color.BLUE,    engine.getRandom());
            this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckGreen), Color.GREEN,  engine.getRandom());
            this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckOrange), Color.ORANGE,engine.getRandom());
        }
        
        if (this.maps == null)
        {
            this.maps = new Maps(engine.getResources());
        }
        
        //reset the timer
        this.timer.reset();
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
                    
                    //place the cars at their starting location
                    maps.placeCars(cars);
                    
                    //update map
                    maps.update(engine);
                    
                    //reset the race progress of the cars
                    cars.resetCarProgress();
                }
            }
            else
            {
                //check if the time delay has passed for the race to start
                if (timer.hasTimePassed())
                {
                    //update map first
                    maps.update(engine);
                    
                    //now update the cars
                    if (cars != null)
                    {
                        cars.update(engine);
                    }
                }
                else
                {
                    //update the timer
                    timer.update(engine.getMain().getTime());
                }
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