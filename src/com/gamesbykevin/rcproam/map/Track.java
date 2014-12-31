package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Cell;
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
    private Tile key[][];
    
    //the color for the road and out of bounds
    private static final Color ROAD_COLOR = Color.GRAY;
    private static final Color OFF_ROAD_COLOR = Color.GREEN.darker();
    
    /**
     * Create a new track of the specified dimensions
     * @param columns Total number of columns in the track
     * @param rows Total number of rows in the track
     * @param width The pixel width of 1 cell
     * @param height The pixel height of 1 cell
     */
    protected Track(final int columns, final int rows, final int width, final int height)
    {
        this.columns = columns;
        this.rows = rows;
        this.width = width;
        this.height = height;
        
        //create new track
        this.key = new Tile[rows][columns];
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
     * @param cell Location containing the column, row
     * @param result true if this is part of the road, false otherwise
     */
    protected void setRoad(final Cell cell, final boolean result)
    {
        setRoad(cell.getCol(), cell.getRow(), result);
    }
    
    /**
     * Assign a cell of the track as road or not road
     * @param column Column
     * @param row Row
     * @param result true if this is part of the road, false otherwise
     */
    protected void setRoad(final double column, final double row, final boolean result)
    {
        setRoad((int)column, (int)row, result);
    }
    
    /**
     * Assign a cell of the track as road or not road
     * @param column Column
     * @param row Row
     * @param result true if this is part of the road, false otherwise
     */
    protected void setRoad(final int column, final int row, final boolean result)
    {
        if (this.key[row][column] == null)
        {
            this.key[row][column] = new Tile(result);
        }
        else
        {
            this.key[row][column].setRoad(result);
        }
    }
    
    /**
     * Is the specified location part of the road
     * @param column
     * @param row
     * @return true if part of the road, false otherwise
     */
    public boolean isRoad(final double column, final double row)
    {
        return isRoad((int)column, (int)row);
    }
    
    /**
     * Is the specified location part of the road
     * @param column
     * @param row
     * @return true if part of the road, false otherwise
     */
    public boolean isRoad(final int column, final int row)
    {
        return key[row][column].isRoad();
    }
    
    /**
     * Has this tile in the track already been visited
     * @param column
     * @param row
     * @return true if so, false otherwise
     */
    protected boolean hasVisited(final int column, final int row)
    {
        return key[row][column].hasVisited();
    }
    
    /**
     * Mark a tile as visited/not-visited
     * @param column
     * @param row
     * @param result 
     */
    protected void setVisited(final int column, final int row, final boolean result)
    {
        key[row][column].setVisited(result);
    }
    
    @Override
    public void dispose()
    {
        this.key = null;
    }
    
    /**
     * Render the mini-map of this track
     * @param graphics Graphics object
     * @param startX x-coordinate where we want to draw the mini-map
     * @param startY y-coordinate where we want to draw the mini-map
     */
    protected void renderMiniMap(final Graphics graphics, final int startX, final int startY)
    {
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getColumns(); col++)
            {
                if (isRoad(col, row))
                {
                    //if true, then this is part of the road
                    graphics.setColor(ROAD_COLOR);
                    graphics.drawRect(startX + (col * getWidth()), startY + (row * getHeight()), getWidth(), getHeight());
                }
                else
                {
                    //anything else is off the road
                    graphics.setColor(OFF_ROAD_COLOR);
                    graphics.drawRect(startX + (col * getWidth()), startY + (row * getHeight()), getWidth(), getHeight());
                }
            }
        }
    }
}