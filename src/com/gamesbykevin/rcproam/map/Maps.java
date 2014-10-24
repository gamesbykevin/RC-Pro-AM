package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.resources.Progress;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.resources.GameImages;
import com.gamesbykevin.rcproam.resources.Resources;
import com.gamesbykevin.rcproam.shared.IElement;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will be the container for all maps
 * @author GOD
 */
public final class Maps implements IElement, Disposable
{
    //all of the tracks that will be in play
    private List<StaticMap> maps;
    
    //the current map
    private int index = 0;
    
    //the total number of maps
    private static final int TOTAL_MAPS = 24;
    
    //we will track the progress of the map creation
    private Progress progress;
    
    public Maps(final Resources resources)
    {
        //create a new list of maps
        this.maps = new ArrayList<>();
        
        //create progress tracker
        this.progress = new Progress(TOTAL_MAPS);
    }
    
    /**
     * Get the current map
     * @return The current assigned map
     */
    public StaticMap getMap()
    {
        return maps.get(getIndex());
    }
    
    /**
     * Place the car at the starting position of the current map
     * @param car The car we want to place
     */
    public void placeCar(final Car car)
    {
        getMap().placeCar(car);
    }
    
    /**
     * Assign the next map
     */
    public void nextMap()
    {
        this.index++;
        
        //if at the end, restart back at 0
        if (this.index >= maps.size())
            this.index = 0;
    }
    
    /**
     * Assign the current map
     * @param index The index of the desired map, if out of bounds 0 will be assigned
     */
    public void setIndex(final int index)
    {
        this.index = index;
        
        //prevent out of bounds exception
        if (getIndex() < 0 || getIndex() >= maps.size())
            this.index = 0;
    }
    
    /**
     * Get the index of the current assigned map
     * @return The index of the current assigned map
     */
    public int getIndex()
    {
        return this.index;
    }
    
    @Override
    public void dispose()
    {
        for (int i = 0; i < maps.size(); i++)
        {
            maps.get(i).dispose();
            maps.set(i, null);
        }
        
        maps.clear();
        maps = null;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isLoading()
    {
        return (!progress.isComplete());
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //assign the area where we will draw the progress
        if (progress.getScreen() == null)
            progress.setScreen(engine.getManager().getWindow());
        
        //assign the description if not yet set
        if (progress.getDescription() == null)
            progress.setDescription("Loading Maps: ");
        
        //if we are not complete continue loading maps
        if (isLoading())
        {
            //object representing a map
            StaticMap staticMap;
            
            switch (progress.getCount())
            {
                case 0:
                    staticMap = new StaticMap(28.5, 3, 48.25, 34.5, engine.getResources().getGameImage(GameImages.Keys.Track01));
                    break;
                    
                case 1:
                    staticMap = new StaticMap(31, 0.5, 48.75, 26, engine.getResources().getGameImage(GameImages.Keys.Track02));
                    break;
                    
                case 2:
                    staticMap = new StaticMap(29, 2.5, 48.75, 34, engine.getResources().getGameImage(GameImages.Keys.Track03));
                    break;
                    
                case 3:
                    staticMap = new StaticMap(31, 0.5, 48.75, 34, engine.getResources().getGameImage(GameImages.Keys.Track04));
                    break;
                    
                case 4:
                    staticMap = new StaticMap(33, 2.5, 40.75, 34, engine.getResources().getGameImage(GameImages.Keys.Track05));
                    break;
                    
                case 5:
                    staticMap = new StaticMap(31, 0.55, 48.75, 34, engine.getResources().getGameImage(GameImages.Keys.Track06));
                    break;
                    
                case 6:
                    staticMap = new StaticMap(18, -3.25, 39.75, 26.25, engine.getResources().getGameImage(GameImages.Keys.Track07));
                    break;
                    
                case 7:
                    staticMap = new StaticMap(14, 0.75, 23.75, 34.25, engine.getResources().getGameImage(GameImages.Keys.Track08));
                    break;
                    
                case 8:
                    staticMap = new StaticMap(28, 2.75, 47.75, 34, engine.getResources().getGameImage(GameImages.Keys.Track09));
                    break;
                    
                case 9:
                    staticMap = new StaticMap(30, 1.05, 47.75, 26.5, engine.getResources().getGameImage(GameImages.Keys.Track10));
                    break;
                    
                case 10:
                    staticMap = new StaticMap(30, 1.05, 47.75, 26.5, engine.getResources().getGameImage(GameImages.Keys.Track11));
                    break;
                    
                case 11:
                    staticMap = new StaticMap(31, 1.05, 48.75, 34.5, engine.getResources().getGameImage(GameImages.Keys.Track12));
                    break;
                    
                case 12:
                    staticMap = new StaticMap(19, -3.25, 40.75, 26.15, engine.getResources().getGameImage(GameImages.Keys.Track13));
                    break;
                    
                case 13:
                    staticMap = new StaticMap(18, -4, 39.75, 25.5, engine.getResources().getGameImage(GameImages.Keys.Track14));
                    break;
                    
                case 14:
                    staticMap = new StaticMap(33, 2.75, 40.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track15));
                    break;
                    
                case 15:
                    staticMap = new StaticMap(15, 0.75, 24.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track16));
                    break;
                    
                case 16:
                    staticMap = new StaticMap(30, 0.75, 47.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track17));
                    break;
                    
                case 17:
                    staticMap = new StaticMap(32, 2.75, 39.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track18));
                    break;
                    
                case 18:
                    staticMap = new StaticMap(18, -3.25, 39.75, 26.15, engine.getResources().getGameImage(GameImages.Keys.Track19));
                    break;
                    
                case 19:
                    staticMap = new StaticMap(29, 2.75, 48.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track20));
                    break;
                    
                case 20:
                    staticMap = new StaticMap(32, 2.75, 39.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track21));
                    break;
                    
                case 21:
                    staticMap = new StaticMap(30, 0.75, 47.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track22));
                    break;
                    
                case 22:
                    staticMap = new StaticMap(28, 2.75, 47.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track23));
                    break;
                    
                case 23:
                    staticMap = new StaticMap(14, 0.75, 55.75, 34.15, engine.getResources().getGameImage(GameImages.Keys.Track24));
                    break;
                    
                default:
                    throw new Exception("staticMap is not setup: " + progress.getCount());
            }
                    
            //add map to list
            maps.add(staticMap);
            
            //increase the progress
            progress.increase();
        }
        else
        {
            getMap().updateLocation(engine.getManager().getCar(), engine.getManager().getWindow());
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //if loading draw the progress
        if (isLoading())
        {
            //draw the progress
            progress.render(graphics);
        }
        else
        {
            //draw the map
            getMap().render(graphics);
        }
    }
    
    /**
     * Render the mini-map of the current assigned map
     * @param graphics Graphics object
     * @param startX x-coordinate where we want to draw the mini-map
     * @param startY y-coordinate where we want to draw the mini-map
     */
    public void renderMiniMap(final Graphics graphics, final int startX, final int startY)
    {
        //don't continue if maps aren't loaded
        if (isLoading())
            return;
        
        //draw the mini-map
        getMap().getTrack().renderMiniMap(graphics, startX, startY);
    }
}
