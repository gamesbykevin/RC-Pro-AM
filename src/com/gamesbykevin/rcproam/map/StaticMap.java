package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.car.Cars;
import com.gamesbykevin.rcproam.shared.Shared;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.List;

public final class StaticMap extends Sprite implements Disposable
{
    //the map of the track
    private Track track;
    
    //the size of the mini-map
    private static final int PIXEL_WIDTH_SMALL_MAP = 84;
    private static final int PIXEL_HEIGHT_SMALL_MAP = 50;
    
    //the dimensions of an isometric tile
    private static final double ISOMETRIC_TILE_WIDTH = 32;
    private static final double ISOMETRIC_TILE_HEIGHT = (ISOMETRIC_TILE_WIDTH / 2);
    
    //the color on the  small map that determines the road
    private static final int PIXEL_IN_BOUNDS = -9145228;
    
    //off set the location because parts of the map is missing from the .png image
    private final double offsetCol, offsetRow;
    
    //the starting col, row location for the car(s)
    private List<Cell> startingLocations;
    
    protected StaticMap(final double offsetCol, final double offsetRow, final double startCol, final double startRow, final Image image) throws Exception
    {
        this.offsetCol = offsetCol;
        this.offsetRow = offsetRow;
        
        //create a new list for the starting location
        this.startingLocations = new ArrayList<>();
        
        //add the starting locations for this map
        this.startingLocations.add(new Cell(startCol,       startRow));
        this.startingLocations.add(new Cell(startCol - 1,       startRow - 1.5));
        this.startingLocations.add(new Cell(startCol + 1, startRow - .25));
        this.startingLocations.add(new Cell(startCol + 1, startRow - 1.5));
        
        //assign image
        this.assignImage(image);
        
        //then create track
        this.createTrack();
        
        //the calculate the cost of the road so the AI knows where to drive
        this.calculateCost();
    }
    
    /**
     * Place the cars at the start position
     * @param cars The container for the cars in play
     */
    public void placeCars(final Cars cars)
    {
        //the index of the current starting location we want
        int index = 0;
        
        for (int i = 0; i < cars.getSize(); i++)
        {
            cars.get(i).setCol(startingLocations.get(index));
            cars.get(i).setRow(startingLocations.get(index));
            
            index++;
            
            //if the index is at the end, restart back at 0
            if (index >= startingLocations.size())
                index = 0;
        }
    }
    
    private double getOffsetCol()
    {
        return this.offsetCol;
    }
    
    private double getOffsetRow()
    {
        return this.offsetRow;
    }
    
    /**
     * Assign image to staticMap and create the track
     * @param image 
     */
    private void assignImage(final Image image)
    {
        //store the image reference
        super.setImage(image);
        
        //the size of the map will be the size of the image
        super.setDimensions(super.getImage());
    }
    
    /**
     * Analyze the Image pixels to create the track
     */
    private void createTrack()
    {
        try
        {
            if (getImage() == null)
                throw new Exception("Image must be set before creating the track");
            
            //make sure the starting locations have been set
            if (startingLocations == null || startingLocations.isEmpty())
                throw new Exception("Starting locations have not been set yet");

            final int startX = 421;
            final int startY;
            
            if ((int)getHeight() == 1024)
            {
                startY = 801;
            }
            else if ((int)getHeight() == 1536)
            {
                startY = 1313;
            }
            else
            {
                startY = 1057;
            }
            
            //create pixelGrabber object
            PixelGrabber pixelGrabber = new PixelGrabber(getImage(), startX, startY, PIXEL_WIDTH_SMALL_MAP, PIXEL_HEIGHT_SMALL_MAP, true);

            //array of our pixels
            final int[] pixels;

            //grab the pixels of the specified area
            if (!pixelGrabber.grabPixels())
            {
                throw new Exception("Failed to grab all pixels");
            }
            else
            {
                //get pixels from object
                pixels = (int[])pixelGrabber.getPixels();
            }

            //create the track
            track = new Track(PIXEL_WIDTH_SMALL_MAP, PIXEL_HEIGHT_SMALL_MAP, 1, 1);

            //assign what is part of the road
            for (int row = 0; row < track.getRows(); row++)
            {
                for (int col = 0; col < track.getColumns(); col++)
                {
                    //get the pixel value of the current pixel
                    final int pixel = pixels[(row * PIXEL_WIDTH_SMALL_MAP) + col];

                    //if the pixel matches the in bounds color, then this is part of the road
                    track.setRoad(col, row, (pixel == PIXEL_IN_BOUNDS));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Calculate the cost of the tiles on the track from the start position.
     * This is for the AI to know which direction to drive towards
     */
    private void calculateCost()
    {
        //the starting point will have a cost of 0
        track.setCost(startingLocations.get(0), 0);

        //list of cells to calculate cost
        List<Cell> check = new ArrayList<>();

        int count = 0;

        //check north locations to add to list
        while (true)
        {
            final int col = (int)startingLocations.get(0).getCol();
            final int row = (int)startingLocations.get(0).getRow() - count;

            if (track.isRoad(col, row))
            {
                if (!track.hasVisited(col, row))
                {
                    //these are all at the beginning so the cost will be 0
                    track.setCost(col, row, 0);

                    //mark spot as visited
                    track.setVisited(col, row, true);

                    //since this is the beginning add the west location to list
                    check.add(new Cell(col - 1, row));
                }
            }
            else
            {
                //if not part of road we covered the north
                break;
            }

            count++;
        }

        //reset to 0
        count = 0;

        //check south locations to add to list
        while (true)
        {
            final int col = (int)startingLocations.get(0).getCol();
            final int row = (int)startingLocations.get(0).getRow() + count;

            if (track.isRoad(col, row))
            {
                if (!track.hasVisited(col, row))
                {
                    //these are all at the beginning so the cost will be 0
                    track.setCost(col, row, 0);

                    //mark spot as visited
                    track.setVisited(col, row, true);

                    //since this is the beginning add the west location to list
                    check.add(new Cell(col - 1, row));
                }
            }
            else
            {
                //if not part of road we covered the south
                break;
            }

            count++;
        }

        //check all locations until we are unable
        while(!check.isEmpty())
        {
            final int col = (int)check.get(0).getCol();
            final int row = (int)check.get(0).getRow();

            //get the cost of the current location
            final int cost = track.getCost(check.get(0));

            //check west
            if (checkLocation(col - 1, row, cost))
            {
                //add new location to list
                check.add(new Cell(col - 1, row));
            }

            //check east
            if (checkLocation(col + 1, row, cost))
            {
                //add new location to list
                check.add(new Cell(col + 1, row));
            }

            //check north
            if (checkLocation(col, row - 1, cost))
            {
                //add new location to list
                check.add(new Cell(col, row - 1));
            }

            //check south
            if (checkLocation(col, row + 1, cost))
            {
                //add new location to list
                check.add(new Cell(col, row + 1));
            }
            
            //remove current location from list
            check.remove(0);
        }
    }
    
    /**
     * Check the location on the track.<br>
     * We won't consider this location if it is not part of the road, or we have already visited<br>
     * 1. Set the increased cost of the new location<br>
     * 2. Mark new location as visited<br>
     * @param col Column of new location
     * @param row Row of new location
     * @param cost Cost of the previous location
     * @return true if we haven't visited the location and it is part of the road, false otherwise
     */
    private boolean checkLocation(final int col, final int row, final int cost)
    {
        //don't worry if not part of road
        if (!track.isRoad(col, row))
            return false;
        
        //don't check if we already visited
        if (track.hasVisited(col, row))
            return false;
        
        //add to the cost of the new location
        track.setCost(col, row, cost + 1);

        //mark new location as visited
        track.setVisited(col, row, true);

        //display for debugging purposes
        //if (Shared.DEBUG)
        //    System.out.println("Column " + col + ", Row = " + row + " - Cost " + cost);

        //return true
        return true;
    }
    
    /**
     * Get the track
     * @return The track containing the road for this map
     */
    public Track getTrack()
    {
        return this.track;
    }
    
    /**
     * Update the location of the map based on the car's location.<br>
     * We want to display where the car is on the map
     * @param car The car that is in the center of the map
     * @param screen The window in which this will be displayed
     */
    protected void updateLocation(final Car car, final Rectangle screen)
    {
        //get offset coordinates based on car location
        final double x = this.getAdjustedX(car, screen);
        final double y = this.getAdjustedY(car, screen);
        
        //set location
        this.setLocation(x, y);
    }
    
    /**
     * Get adjusted x-coordinate.<br>
     * This will determine where the isometric coordinate is to be placed based on the car location
     * @param car The car we want to calculate the coordinate
     * @param screen Screen where game play takes place
     * @return x-coordinate
     */
    public double getAdjustedX(final Car car, final Rectangle screen)
    {
        //get the car location
        double col = car.getCol();
        double row = car.getRow();
        
        //offset the (col, row) because the part of the map is missing from each image
        col -= getOffsetCol();
        row -= getOffsetRow();
        
        //calculate the location
        final double x = ((PIXEL_WIDTH_SMALL_MAP * ISOMETRIC_TILE_WIDTH) / 2) + (col * ISOMETRIC_TILE_WIDTH) - (row * ISOMETRIC_TILE_WIDTH);
        
        //position in middle of screen
        return (-x + (screen.width / 2));
    }
            
    /**
     * Get adjusted y-coordinate.<br>
     * This will determine where the isometric coordinate is to be placed based on the car location
     * @param car The car we want to calculate the coordinate
     * @param screen Screen where game play takes place
     * @return y-coordinate
     */
    public double getAdjustedY(final Car car, final Rectangle screen)
    {
        //get the car location
        double col = car.getCol();
        double row = car.getRow();
        
        //offset the (col, row) because the part of the map is missing from each image
        col -= getOffsetCol();
        row -= getOffsetRow();
        
        //calculate the location
        final double y = (row * ISOMETRIC_TILE_HEIGHT) + (col * ISOMETRIC_TILE_HEIGHT);
        
        //position in middle of screen
        return (-y + (screen.height / 2));
    }
    
    @Override
    public void dispose()
    {
        if (startingLocations != null)
        {
            for (int i = 0; i < startingLocations.size(); i++)
            {
                startingLocations.set(i, null);
            }
            
            startingLocations.clear();
            startingLocations = null;
        }
        
        super.dispose();
    }
    
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
    }
}