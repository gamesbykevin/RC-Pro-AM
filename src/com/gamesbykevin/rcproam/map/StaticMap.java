package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.car.Car;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.PixelGrabber;

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
    
    //the starting col, row location for the car
    private final double startCol, startRow;
    
    protected StaticMap(final double offsetCol, final double offsetRow, final double startCol, final double startRow, final Image image)
    {
        this.offsetCol = offsetCol;
        this.offsetRow = offsetRow;
        
        this.startCol = startCol;
        this.startRow = startRow;
        
        this.assignImage(image);
    }
    
    /**
     * Place the car at the start position
     * @param car The car we want to place
     */
    public void placeCar(final Car car)
    {
        car.setCol(startCol);
        car.setRow(startRow);
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
        
        //now that our image is set, we need to analyze the mini-map to know where the road is
        if ((int)getHeight() == 1024)
        {
            createTrack(421, 801);
        }
        else if ((int)getHeight() == 1536)
        {
            createTrack(421, 1313);
        }
        else
        {
            createTrack(421, 1057);
        }
    }
    
    /**
     * Analyze the Image pixels to create the track
     * @param x x-coordinate where track mini-map starts
     * @param y y-coordinate where track mini-map starts
     */
    private void createTrack(final int x, final int y)
    {
        try
        {
            if (getImage() == null)
                throw new Exception("Image must be set before creating the track");

            //create pixelGrabber object
            PixelGrabber pixelGrabber = new PixelGrabber(getImage(), x, y, PIXEL_WIDTH_SMALL_MAP, PIXEL_HEIGHT_SMALL_MAP, true);

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
    public void updateLocation(final Car car, final Rectangle screen)
    {
        //get the car location
        double col = car.getCol();
        double row = car.getRow();
        
        //offset the (col, row) because the part of the map is missing from each image
        col -= getOffsetCol();//28.5;
        row -= getOffsetRow();//3;
        
        //calculate the location
        final double x = ((PIXEL_WIDTH_SMALL_MAP * ISOMETRIC_TILE_WIDTH) / 2) + (col * ISOMETRIC_TILE_WIDTH) - (row * ISOMETRIC_TILE_WIDTH);
        final double y = (row * ISOMETRIC_TILE_HEIGHT) + (col * ISOMETRIC_TILE_HEIGHT);
        
        //position in the middle of screen
        this.setLocation(-x + (screen.width / 2), -y + (screen.height / 2));
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