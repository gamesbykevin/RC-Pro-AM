package com.gamesbykevin.rcproam.map.track;

import com.gamesbykevin.framework.resources.Disposable;
import java.awt.Color;

import java.awt.Graphics;

public final class Track implements Disposable
{
    //the size of the track
    private final int columns, rows;
    
    //the pixel size of each cell
    private final int width, height;
    
    //the track
    private boolean key[][];
    
    /**
     * Create a new track of the specified dimensions
     * @param columns Total number of columns in the track
     * @param rows Total number of rows in the track
     * @param width The pixel width of 1 cell
     * @param height The pixel height of 1 cell
     */
    public Track(final int columns, final int rows, final int width, final int height)
    {
        this.columns = columns;
        this.rows = rows;
        this.width = width;
        this.height = height;
        
        //create new track
        this.key = new boolean[rows][columns];
    }
    
    public int getColumns()
    {
        return this.columns;
    }
    
    public int getRows()
    {
        return this.rows;
    }
    
    public int getWidth()
    {
        return this.width;
    }
    
    public int getHeight()
    {
        return this.height;
    }
    
    /**
     * Assign a cell of the track as road or not road
     * @param column Column
     * @param row Row
     * @param result true if this is part of the road, false otherwise
     */
    public void setRoad(final int column, final int row, final boolean result)
    {
        this.key[row][column] = result;
    }
    
    @Override
    public void dispose()
    {
        this.key = null;
    }
    
    public void renderTest(final Graphics graphics)
    {
        final int startX = 0;
        final int startY = 0;
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getColumns(); col++)
            {
                graphics.setColor((key[row][col]) ? Color.WHITE : Color.BLACK);
                graphics.drawRect(startX + (col * getWidth()), startY + (row * getHeight()), getWidth(), getHeight());
            }
        }
    }
}