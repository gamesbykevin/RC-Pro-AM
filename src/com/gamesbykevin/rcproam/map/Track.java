package com.gamesbykevin.rcproam.map;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.resources.Disposable;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

public final class Track implements Disposable
{
    //the size of the track
    private final int columns, rows;
    
    //the pixel size of each cell
    private final int width, height;
    
    //the track
    private Tile key[][];
    
    //the color for the road and out of bounds
    protected static final Color ROAD_COLOR = Color.GRAY;
    protected static final Color OFF_ROAD_COLOR = Color.GREEN.darker();
    
    //the check points in the track located at each turn
    private List<Cell> checkPoints;
    
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
        
        //create a new list for the way pojnts
        this.checkPoints = new ArrayList<>();
    }
    
    /**
     * Add a way point at the specified location
     * @param col Column
     * @param row Row
     */
    private void addCheckPoint(final double col, final double row)
    {
        this.checkPoints.add(new Cell(col, row));
    }
    
    /**
     * Get the way point at the specified target
     * @param target
     * @return The location of the specified way point
     */
    public Cell getCheckPoint(final int target)
    {
        return this.checkPoints.get(target);
    }
    
    /**
     * Get the number of check points
     * @return The total number of check points for this track
     */
    public int getCheckPointCount()
    {
        return this.checkPoints.size();
    }
    
    /**
     * Is the targeted way point the final for the track? (a.k.a. finish line)<br>
     * We need to know to determine if a lap/race has been completed
     * @param target The way point we want to check
     * @return true if it is the final way point, false otherwise
     */
    public boolean isFinalCheckPoint(final int target)
    {
        return (target >= this.checkPoints.size() - 1);
    }
    
    /**
     * Create check points at every turn
     * @param trackNo The track we need to create way points for
     * @throws Exception An exception will be thrown if the track is not setup
     */
    protected void createWaypoints(final int trackNo) throws Exception
    {
        //add the way points in the specified order
        switch (trackNo)
        {
            case Maps.TRACK_01:
                addCheckPoint(30, 33.5);
                addCheckPoint(26.5, 32);
                addCheckPoint(25.5, 30);
                addCheckPoint(25.5, 13);
                addCheckPoint(27, 10);
                addCheckPoint(30, 9.5);
                addCheckPoint(38, 9.5);
                addCheckPoint(41.5, 13);
                addCheckPoint(45, 17.5);
                addCheckPoint(54, 17.5);
                addCheckPoint(57, 19.5);
                addCheckPoint(57.5, 22);
                addCheckPoint(57.5, 30);
                addCheckPoint(55, 33.5);
                
                //this is the finish line
                addCheckPoint(47, 33.5);
                break;

            case Maps.TRACK_02:
                addCheckPoint(37, 25);
                addCheckPoint(33.5, 29);
                addCheckPoint(30, 34);
                addCheckPoint(26.5, 32);
                addCheckPoint(26.5, 21);
                addCheckPoint(28, 17.5);
                addCheckPoint(46, 17.5);
                addCheckPoint(49.5, 14);
                addCheckPoint(53, 9);
                addCheckPoint(57, 11);
                addCheckPoint(58, 22);
                addCheckPoint(56, 25);
                
                //this is the finish line
                addCheckPoint(47, 25.5);
                break;

            case Maps.TRACK_03:
                addCheckPoint(37, 33.5);
                addCheckPoint(33.5, 30);
                addCheckPoint(30, 25.5);
                addCheckPoint(20, 25.5);
                addCheckPoint(17.5, 21);
                addCheckPoint(21, 17.5);
                addCheckPoint(54, 17.5);
                addCheckPoint(57.5, 14);
                addCheckPoint(61, 9);
                addCheckPoint(65, 11);
                addCheckPoint(66, 14);
                addCheckPoint(66, 22);
                addCheckPoint(63, 25);
                addCheckPoint(59, 27);
                addCheckPoint(55, 33.5);
                
                //this is the finish line
                addCheckPoint(47, 33.5);
                break;

            case Maps.TRACK_04:
                addCheckPoint(30, 33.5);
                addCheckPoint(25.5, 30);
                addCheckPoint(25.5, 21);
                addCheckPoint(29, 17.5);
                addCheckPoint(38, 17.5);
                addCheckPoint(41.5, 21);
                addCheckPoint(44, 25);
                addCheckPoint(49, 25);
                addCheckPoint(49, 13);
                addCheckPoint(53, 9);
                addCheckPoint(57, 12);
                addCheckPoint(57, 30);
                addCheckPoint(54, 33.5);
                
                //this is the finish line
                addCheckPoint(39, 33.5);
                break;

            case Maps.TRACK_05:
                addCheckPoint(13, 26);
                addCheckPoint(9, 22);
                addCheckPoint(9, 13);
                addCheckPoint(13, 9);
                addCheckPoint(22, 9);
                addCheckPoint(26, 12);
                addCheckPoint(28, 17.5);
                addCheckPoint(70, 17.5);
                addCheckPoint(73.5, 20);
                addCheckPoint(73.5, 30);
                addCheckPoint(70, 33.5);
                addCheckPoint(61, 33.5);
                addCheckPoint(57.5, 31);
                addCheckPoint(57.5, 26);
                
                //this is the finish line
                addCheckPoint(39, 25.5);
                break;

            case Maps.TRACK_06:
                addCheckPoint(13, 33.5);
                addCheckPoint(9.5, 30);
                addCheckPoint(13, 25.5);
                addCheckPoint(23, 25.5);
                addCheckPoint(26.5, 21);
                addCheckPoint(22, 17.5);
                addCheckPoint(13, 17.5);
                addCheckPoint(9.5, 13);
                addCheckPoint(13, 9.5);
                addCheckPoint(30, 9.5);
                addCheckPoint(34, 13);
                addCheckPoint(34, 23);
                addCheckPoint(38, 25.5);
                addCheckPoint(62, 25.5);
                addCheckPoint(66, 22);
                addCheckPoint(69, 17);
                addCheckPoint(73.5, 20);
                addCheckPoint(73.5, 30);
                addCheckPoint(70, 33.5);
                
                //this is the finish line
                addCheckPoint(23, 33.5);
                break;

            case Maps.TRACK_07:
                addCheckPoint(13, 33.5);
                addCheckPoint(9.5, 30);
                addCheckPoint(9.5, 13);
                addCheckPoint(13, 9.5);
                addCheckPoint(17, 12);
                addCheckPoint(21, 17);
                addCheckPoint(26, 14);
                addCheckPoint(29, 9.5);
                addCheckPoint(71, 9.5);
                addCheckPoint(74, 13);
                addCheckPoint(70, 18);
                addCheckPoint(60, 18);
                addCheckPoint(57, 22);
                addCheckPoint(54, 26);
                addCheckPoint(45, 26);
                addCheckPoint(41, 29);
                addCheckPoint(41, 33.5);
                addCheckPoint(38, 33.5);
                
                //this is the finish line
                addCheckPoint(23, 33.5);
                break;

            case Maps.TRACK_08:
                addCheckPoint(45, 33.5);
                addCheckPoint(41.5, 30);
                addCheckPoint(41.5, 21);
                addCheckPoint(38, 17.5);
                addCheckPoint(34, 20);
                addCheckPoint(34, 30);
                addCheckPoint(30, 33.5);
                addCheckPoint(13, 33.5);
                addCheckPoint(9.5, 30);
                addCheckPoint(9.5, 13);
                addCheckPoint(13, 9.5);
                addCheckPoint(70, 9.5);
                addCheckPoint(74, 13);
                addCheckPoint(71, 18);
                addCheckPoint(65.5, 21);
                addCheckPoint(69, 26);
                addCheckPoint(74, 29);
                addCheckPoint(70, 33.5);
                
                //this is the finish line
                addCheckPoint(55, 33.5);
                break;

            default:
                throw new Exception("way points are not setup: " + trackNo);
        }
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
     * Is the specified location part of the road?
     * @param column
     * @param row
     * @return true if part of the road, false otherwise
     */
    public boolean isRoad(final double column, final double row)
    {
        return isRoad((int)column, (int)row);
    }
    
    /**
     * Is the specified location part of the road?
     * @param cell Location containing column, row
     * @return true if part of the road, false otherwise
     */
    public boolean isRoad(final Cell cell)
    {
        return isRoad(cell.getCol(), cell.getRow());
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
        
        if (checkPoints != null)
        {
            for (int i = 0; i < checkPoints.size(); i++)
            {
                checkPoints.set(i, null);
            }
            
            checkPoints.clear();
            checkPoints = null;
        }
    }
}