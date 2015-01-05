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
    
    //do we hide mini-map, time, leaderboard
    private boolean enableDetail = false;
    
    //the size of the info screen at the bottom
    private static final int INFO_WIDTH = 256;
    private static final int INFO_HEIGHT = 64;
    
    //different car types
    private static final int CAR_TYPE_TRUCK = 0;
    private static final int CAR_TYPE_SUV = 1;
    
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
        
        //the window available for gameplay
        Rectangle screen = new Rectangle(engine.getMain().getScreen());
        
        //the height will be smaller
        screen.height -= 64;
        
        //set the game window where game play will occur
        setWindow(screen);
        
        //do we hide the detail screen
        setEnableDetailScreen(engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.EnableDetail) == 0);
        
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
            
            switch (engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.CarType))
            {
                case CAR_TYPE_SUV:
                    //add human car first
                    this.cars.addHuman(engine.getResources().getGameImage(GameImages.Keys.SuvRed), Color.RED, "Red");

                    //add cpu car(s)
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.SuvBlue), Color.BLUE, "Blue", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.SuvGreen), Color.GREEN, "Green", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.SuvOrange), Color.ORANGE, "Orange", engine.getRandom());
                    break;
                    
                default:
                case CAR_TYPE_TRUCK:
                    //add human car first
                    this.cars.addHuman(engine.getResources().getGameImage(GameImages.Keys.TruckRed), Color.RED, "Red");

                    //add cpu car(s)
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckBlue), Color.BLUE, "Blue", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckGreen), Color.GREEN, "Green", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckOrange), Color.ORANGE, "Orange", engine.getRandom());
                    break;
            }
            
            //set human in center of screen
            this.cars.getHuman().setLocation(getWindow());
        }
        
        //reset cars
        this.cars.reset();
        
        //create maps if it does not already exist
        if (this.maps == null)
            this.maps = new Maps(engine.getResources());
    }
    
    public void setEnableDetailScreen(final boolean enableDetail)
    {
        this.enableDetail = enableDetail;
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
            //update map first
            maps.update(engine);
            
            //if no longer loading
            if (!maps.isLoading())
            {
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
            
                //now draw the race cars
                cars.render(graphics);
                
                if (enableDetail)
                {
                    //set background color for stats/info screen
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(getWindow().x, getWindow().y + getWindow().height, INFO_WIDTH, INFO_HEIGHT);

                    //where the mini-map will be drawn
                    final int x = getWindow().x + (getWindow().width / 2) - (Maps.MINIMAP_WIDTH / 3);
                    final int y = getWindow().y + getWindow().height + (INFO_HEIGHT / 2) - (Maps.MINIMAP_HEIGHT / 2);

                    //draw the mini-map with the cars on the map
                    maps.renderMiniMap(graphics, cars, x, y);

                    //draw human car info
                    cars.renderTimeInfo(graphics, getWindow().x + 1, getWindow().y + getWindow().height + (INFO_HEIGHT / 5), maps.getMap().getLaps());

                    //draw the leaderboard
                    cars.renderLeaderboard(graphics, x + (int)(Maps.MINIMAP_WIDTH * 1.1), getWindow().y + getWindow().height + (INFO_HEIGHT / 5));
                }
            }
        }
    }
}