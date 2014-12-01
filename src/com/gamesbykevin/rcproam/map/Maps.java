package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.resources.Progress;

import com.gamesbykevin.rcproam.car.Cars;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.resources.GameImages;
import com.gamesbykevin.rcproam.resources.Resources;
import com.gamesbykevin.rcproam.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will be the container for all maps
 * @author GOD
 */
public class Maps implements IElement, Disposable
{
    //all of the tracks that will be in play
    private List<StaticMap> maps;
    
    //the current map
    private int index = 0;
    
    //the total number of maps
    private static final int TOTAL_MAPS = 8;
    
    //the different tracks
    private static final int TRACK_01 = 0;
    private static final int TRACK_02 = 1;
    private static final int TRACK_03 = 2;
    private static final int TRACK_04 = 3;
    private static final int TRACK_05 = 4;
    private static final int TRACK_06 = 5;
    private static final int TRACK_07 = 6;
    private static final int TRACK_08 = 7;
    
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
     * @throws Exception if there are no cars to place
     */
    public void placeCars(final Cars cars) throws Exception
    {
        if (cars.getSize() < 1)
            throw new Exception("There are no cars to place");
        
        getMap().placeCars(cars);
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
     * @throws exception if the maps have not finished loading or if no maps exist
     */
    public void setIndex(final int index) throws Exception
    {
        this.index = index;
        
        //if we are still loading we will not be able to assign the current map yet
        if (isLoading())
            throw new Exception("Can assign the current map once the maps have finished loading.");
        
        //if there are no maps, something else went wrong here
        if (maps.isEmpty())
            throw new Exception("Maps have not been created yet unable to assign.");
        
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
            progress.setDescription("Analyzing/Creating Maps: ");
        
        //if we are not complete continue loading maps
        if (isLoading())
        {
            //corret offset value to place car at start
            final double offsetCol;
            final double offsetRow;
            
            //where the race starts on the mini-map
            final double startCol;
            final double startRow;
            
            //image of the track
            final Image image;
            
            switch (progress.getCount())
            {
                case TRACK_01:
                    offsetCol = 28.5;
                    offsetRow = 3;
                    startCol = 48.25;
                    startRow = 34.5;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track01);
                    break;
                    
                case TRACK_02:
                    offsetCol = 31;
                    offsetRow = 0.5;
                    startCol = 48.75;
                    startRow = 26;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track02);
                    break;
                    
                case TRACK_03:
                    offsetCol = 31;
                    offsetRow = 0.5;
                    startCol = 48.75;
                    startRow = 34;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track03);
                    break;
                    
                case TRACK_04:
                    offsetCol = 33;
                    offsetRow = 2.5;
                    startCol = 40.75;
                    startRow = 34;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track04);
                    break;
                    
                case TRACK_05:
                    offsetCol = 18;
                    offsetRow = -3.25;
                    startCol = 39.75;
                    startRow = 26.25;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track05);
                    break;
                    
                case TRACK_06:
                    offsetCol = 14;
                    offsetRow = 0.75;
                    startCol = 23.75;
                    startRow = 34.25;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track06);
                    break;
                    
                case TRACK_07:
                    offsetCol = 15;
                    offsetRow = 0.75;
                    startCol = 24.75;
                    startRow = 34.15;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track07);
                    break;
                    
                case TRACK_08:
                    offsetCol = 14;
                    offsetRow = 0.75;
                    startCol = 55.75;
                    startRow = 34.15;
                    image = engine.getResources().getGameImage(GameImages.Keys.Track08);
                    break;
                    
                default:
                    throw new Exception("staticMap is not setup: " + progress.getCount());
            }
            
            //add map to list
            maps.add(new StaticMap(offsetCol, offsetRow, startCol, startRow, image));
            
            //increase the progress
            progress.increase();
        }
        else
        {
            //update the map location based on the human location
            getMap().updateLocation(engine.getManager().getCars().getHuman(), engine.getManager().getWindow());
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