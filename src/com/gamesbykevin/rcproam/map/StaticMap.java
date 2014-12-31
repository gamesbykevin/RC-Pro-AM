package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.car.Cars;

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
    
    //the check points in the map located at each turn
    private List<Cell> wayPoints;
    
    protected StaticMap(final double offsetCol, final double offsetRow, final double startCol, final double startRow, final Image image, final int trackNo) throws Exception
    {
        this.offsetCol = offsetCol;
        this.offsetRow = offsetRow;
        
        //create a new list for the way pojnts
        this.wayPoints = new ArrayList<>();
        
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
        
        //create a checkpoint at each turn for the ai to know where to drive
        this.createWaypoints(trackNo);
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
     * Add a way point at the specified location
     * @param col Column
     * @param row Row
     */
    private void addWayPoint(final double col, final double row)
    {
        this.wayPoints.add(new Cell(col, row));
    }
    
    /**
     * Create check points at every turn
     * @param trackNo The track we need to create way points for
     * @throws Exception An exception will be thrown if the track is not setup
     */
    private void createWaypoints(final int trackNo) throws Exception
    {
        //add the way points in the specified order
        switch (trackNo)
        {
            case Maps.TRACK_01:
                addWayPoint(30, 32.5);
                addWayPoint(25.5, 30);
                addWayPoint(25.5, 14);
                addWayPoint(30, 9.5);
                addWayPoint(38.5, 9.5);
                addWayPoint(45, 17.5);
                addWayPoint(55, 17.5);
                addWayPoint(57.5, 22);
                addWayPoint(57.5, 30);
                addWayPoint(55, 33.5);
                break;

            case Maps.TRACK_02:
                addWayPoint(39, 25.5);
                addWayPoint(31, 32.5);
                addWayPoint(28, 32.5);
                addWayPoint(25.5, 29);
                addWayPoint(25.5, 21);
                addWayPoint(30, 17.5);
                addWayPoint(46, 17.5);
                addWayPoint(52, 10);
                addWayPoint(56, 10.5);
                addWayPoint(57.5, 14);
                addWayPoint(57.5, 22);
                addWayPoint(55, 25.5);
                break;

            case Maps.TRACK_03:
                addWayPoint(38, 33.5);
                addWayPoint(31, 25.5);
                addWayPoint(21, 25.5);
                addWayPoint(18.5, 21);
                addWayPoint(21, 17.5);
                addWayPoint(55, 17.5);
                addWayPoint(61, 10);
                addWayPoint(65.5, 13);
                addWayPoint(65.5, 22.5);
                addWayPoint(61, 25.5);
                addWayPoint(55, 33.5);
                break;

            case Maps.TRACK_04:
                addWayPoint(30, 33.5);
                addWayPoint(25.5, 31);
                addWayPoint(25.5, 21);
                addWayPoint(29, 17.5);
                addWayPoint(40, 17.5);
                addWayPoint(46, 25.5);
                addWayPoint(49.5, 22);
                addWayPoint(49.5, 13.5);
                addWayPoint(54, 9.5);
                addWayPoint(58.5, 13);
                addWayPoint(58.5, 30);
                addWayPoint(55, 33.5);
                break;

            case Maps.TRACK_05:
                addWayPoint(14, 25.5);
                addWayPoint(10.5, 22);
                addWayPoint(10.5, 13);
                addWayPoint(13, 9.5);
                addWayPoint(22, 9.5);
                addWayPoint(29, 17.5);
                addWayPoint(70, 17.5);
                addWayPoint(73.5, 22);
                addWayPoint(73.5, 31);
                addWayPoint(71, 33.5);
                addWayPoint(60, 33.5);
                addWayPoint(54, 26);
                break;

            case Maps.TRACK_06:
                addWayPoint(13, 33.5);
                addWayPoint(10.5, 29);
                addWayPoint(14, 25.5);
                addWayPoint(23, 25.5);
                addWayPoint(25.5, 22);
                addWayPoint(23, 17.5);
                addWayPoint(13, 17.5);
                addWayPoint(10.5, 13);
                addWayPoint(13, 9.5);
                addWayPoint(31, 9.5);
                addWayPoint(34.5, 13);
                addWayPoint(34.5, 23);
                addWayPoint(37, 25.5);
                addWayPoint(63, 25.5);
                addWayPoint(69, 17.5);
                addWayPoint(73.5, 21);
                addWayPoint(73.5, 31);
                addWayPoint(71, 33.5);
                break;

            case Maps.TRACK_07:
                addWayPoint(14, 33.5);
                addWayPoint(10.5, 30);
                addWayPoint(10.5, 14);
                addWayPoint(13, 10);
                addWayPoint(21, 17);
                addWayPoint(26, 13);
                addWayPoint(30, 9.5);
                addWayPoint(71, 9.5);
                addWayPoint(73.5, 13);
                addWayPoint(71, 17.5);
                addWayPoint(61, 17.5);
                addWayPoint(55, 25.5);
                addWayPoint(45, 25.5);
                addWayPoint(39, 33.5);
                break;

            case Maps.TRACK_08:
                addWayPoint(45, 33.5);
                addWayPoint(41.5, 30);
                addWayPoint(41.5, 21);
                addWayPoint(38, 18);
                addWayPoint(33.5, 21);
                addWayPoint(33.5, 30);
                addWayPoint(30, 33.5);
                addWayPoint(13, 33.5);
                addWayPoint(9.5, 30);
                addWayPoint(9.5, 13);
                addWayPoint(13, 9.5);
                addWayPoint(70, 9.5);
                addWayPoint(73.5, 13);
                addWayPoint(69, 17.5);
                addWayPoint(66.5, 21);
                addWayPoint(68, 25.5);
                addWayPoint(73.5, 30);
                addWayPoint(70, 33.5);
                break;

            default:
                throw new Exception("way points are not setup: " + trackNo);
        }
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
        
        if (wayPoints != null)
        {
            for (int i = 0; i < wayPoints.size(); i++)
            {
                wayPoints.set(i, null);
            }
            
            wayPoints.clear();
            wayPoints = null;
        }
        
        if (track != null)
        {
            track.dispose();
            track = null;
        }
        
        super.dispose();
    }
    
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
    }
}