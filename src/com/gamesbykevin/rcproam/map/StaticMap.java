package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.map.track.Track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.PixelGrabber;

public final class StaticMap extends Sprite implements Disposable
{
    private Track track;
    
    //the size of the mini-map
    private static final int PIXEL_WIDTH_SMALL_MAP = 84;
    private static final int PIXEL_HEIGHT_SMALL_MAP = 50;
    
    //the color on the  small map that determines out of bounds
    private static final int PIXEL_OUT_OF_BOUNDS = -16739328;
    
    //start location
    private Point start;
    
    @Override
    public void setImage(final Image image)
    {
        //store the image reference
        super.setImage(image);
        
        //the size of the map will be the size of the image
        super.setDimensions(super.getImage());
    }
    
    /**
     * Analyze the Image to create the track
     * @param x x-coordinate where track mini-map starts
     * @param y y-coordinate where track mini-map starts
     */
    public void createTrack(final int x, final int y) throws Exception
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
                
                //if the pixel does not match the out of bounds color, it is part of the road
                track.setRoad(col, row, (pixel != PIXEL_OUT_OF_BOUNDS));
            }
        }
    }
    
    public Track getTrack()
    {
        return this.track;
    }
    
    /**
     * Set the starting location where the race will begin
     * @param x x-coordinate
     * @param y y-coordinate
     * @param r Rectangle where game play will take place
     * @throws Exception if the image is not yet assigned
     */
    public void setStartLocation(final double x, final double y, final Rectangle r) throws Exception
    {
        if (getImage() == null)
            throw new Exception("Must assign image object first before setting the start location.");
        
        if (start == null)
            start = new Point();
        
        //set the starting location
        start.setLocation(-x + (r.width / 2), -y + (r.height / 2));
        
        //move the map for now
        super.setLocation(start);
        
        //System.out.println("x = " + start.getX() + ", y = " + start.getY());
    }
    
    /**
     * Set the location of the map based on the central car's perspective.<br>
     * The velocity of the car will be applied to the map.<br>
     * Then the location of the map will be updated.
     * 
     * @param car The car that is the central perspective of this map
     */
    public void updateLocation(final Car car)
    {
        super.setVelocity(car);
        super.update();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
        
        track.renderTest(graphics);
    }
}
