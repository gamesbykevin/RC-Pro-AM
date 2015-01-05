package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.resources.Progress;

import com.gamesbykevin.rcproam.car.Cars;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.menu.CustomMenu;
import com.gamesbykevin.rcproam.resources.GameImages;
import com.gamesbykevin.rcproam.resources.Resources;
import com.gamesbykevin.rcproam.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
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
    protected static final int TRACK_01 = 0;
    protected static final int TRACK_02 = 1;
    protected static final int TRACK_03 = 2;
    protected static final int TRACK_04 = 3;
    protected static final int TRACK_05 = 4;
    protected static final int TRACK_06 = 5;
    protected static final int TRACK_07 = 6;
    protected static final int TRACK_08 = 7;
    
    //we will track the progress of the map creation
    private Progress progress;
    
    //the range for the most/least amount of laps required to complete a race for a given map
    private static final int MAX_LAPS = 4;
    private static final int MIN_LAPS = 2;
    
    //image for the minimap
    private BufferedImage minimap;
    
    //graphics object for drawing the minimap
    private Graphics2D minimapGraphics;
    
    //the size of the minimap
    public static final int MINIMAP_WIDTH = StaticMap.PIXEL_WIDTH_SMALL_MAP;
    public static final int MINIMAP_HEIGHT = StaticMap.PIXEL_HEIGHT_SMALL_MAP;
    
    public Maps(final Resources resources)
    {
        //create a new list of maps
        this.maps = new ArrayList<>();
        
        //create progress tracker
        this.progress = new Progress(TOTAL_MAPS);
        
        //create new image
        this.minimap = new BufferedImage(StaticMap.PIXEL_WIDTH_SMALL_MAP, StaticMap.PIXEL_HEIGHT_SMALL_MAP, BufferedImage.TYPE_INT_RGB);
        
        //get graphics object to be able to write to this image
        this.minimapGraphics = minimap.createGraphics();
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
        
        if (minimap != null)
        {
            minimap.flush();
            minimap = null;
        }
        
        if (minimapGraphics != null)
        {
            minimapGraphics.dispose();
            minimapGraphics = null;
        }
    }
    
    /**
     * Are the maps loading
     * @return true if so, false otherwise
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
            
            //pick a random number of laps required to complete the track
            final int count = engine.getRandom().nextInt(MAX_LAPS - MIN_LAPS) + MIN_LAPS;
            
            //create a new static map
            StaticMap map = new StaticMap(offsetCol, offsetRow, startCol, startRow, image, progress.getCount(), count);
            
            //add map to list
            maps.add(map);
            
            //increase the progress
            progress.increase();
            
            //if the maps have now been created, set the map according to the menu setting
            if (!isLoading())
                setMap(engine, engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Track));
        }
        else
        {
            //update the map location based on the human location
            getMap().updateLocation(engine.getManager().getCars().getHuman(), engine.getManager().getWindow());
        }
    }
    
    private void setMap(final Engine engine, final int index) throws Exception
    {
        //assign the current map
        setIndex(index);
        
        //now place the cars at their starting location defined by the track
        getMap().placeCars(engine.getManager().getCars());
                    
        //update the map location based on the human location
        getMap().updateLocation(engine.getManager().getCars().getHuman(), engine.getManager().getWindow());
        
        //reset the cars as well
        engine.getManager().getCars().reset();
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
     * @param cars The object containing cars in play
     * @param startX x-coordinate where we want to draw the mini-map
     * @param startY y-coordinate where we want to draw the mini-map
     */
    public void renderMiniMap(final Graphics graphics, final Cars cars, final int startX, final int startY)
    {
        //don't continue if maps aren't loaded
        if (isLoading())
            return;
        
        //render the minimap to the image
        getMap().renderMiniMap(minimapGraphics);
        
        //now draw the cars on top of the mini map to the image
        cars.renderMiniMapLocations(minimapGraphics);
        
        //now draw the final image
        graphics.drawImage(minimap, startX, startY, MINIMAP_WIDTH, MINIMAP_HEIGHT, null);
    }
}