package com.gamesbykevin.rcproam.manager;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.StaticMap;
import com.gamesbykevin.rcproam.map.track.*;
import com.gamesbykevin.rcproam.menu.CustomMenu;
import com.gamesbykevin.rcproam.menu.CustomMenu.*;
import com.gamesbykevin.rcproam.resources.*;
import com.gamesbykevin.rcproam.shared.Shared;

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
    
    //our race car
    private Car car;
    
    //the map of the current track
    private StaticMap staticMap;
    
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
        if (car == null)
        {
            car = new Car();
            car.setLocation(engine.getManager().getWindow());
            car.setImage(engine.getResources().getGameImage(GameImages.Keys.Truck));
        }
        
        if (staticMap == null)
        {
            //create new map
            staticMap = new StaticMap();
            
            //set the image map
            staticMap.setImage(engine.getResources().getGameImage(GameImages.Keys.Track01));
            
            //analyze the mini-map to create the track
            staticMap.createTrack(421, 801);
            
            //set the starting point
            staticMap.setStartLocation(970, 819, engine.getManager().getWindow());
            
            car.setCol(47);
            car.setRow(34);
            //car.setCol(0);
            //car.setRow(0);
            
        }
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
        if (car != null)
        {
            car.update(engine);
        }
        
        if (staticMap != null)
        {
            //update map perspective based on where our car is located
            //staticMap.updateLocation(car);
            if (staticMap.getImage() != null)
            {
                //get the velocity
                final double vx = car.getVelocityX();
                final double vy = car.getVelocityY();
                
                final double vx2 = (staticMap.getImage().getWidth(null) / staticMap.getTrack().getColumns()) * vx;
                final double vy2 = (staticMap.getImage().getHeight(null) / staticMap.getTrack().getRows()) * vy;
                
                car.setVelocityX(vx2);
                car.setVelocityY(vy2);
                
                //update map location
                staticMap.updateLocation(car);
                
                //set the velocity back
                car.setVelocity(vx, vy);
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
        if (staticMap != null)
        {
            //draw the map
            staticMap.render(graphics);
        }
        
        if (car != null)
        {
            //draw the race car
            car.render(graphics);
        }
    }
}