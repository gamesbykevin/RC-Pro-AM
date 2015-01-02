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
    private List<Cell> wayPoints;
    
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
        this.wayPoints = new ArrayList<>();
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
     * Get the way point at the specified target
     * @param target
     * @return The location of the specified way point
     */
    public Cell getWayPoint(final int target)
    {
        return this.wayPoints.get(target);
    }
    
    /**
     * Is the targeted way point the final for the track? (a.k.a. finish line)<br>
     * We need to know to determine if a lap/race has been completed
     * @param target The way point we want to check
     * @return true if it is the final way point, false otherwise
     */
    public boolean isFinalWayPoint(final int target)
    {
        return (target >= this.wayPoints.size() - 1);
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
                addWayPoint(30, 33.5);
                addWayPoint(26.5, 32);
                addWayPoint(25.5, 30);
                addWayPoint(25.5, 13);
                addWayPoint(27, 10);
                addWayPoint(30, 9.5);
                addWayPoint(38, 9.5);
                addWayPoint(41.5, 13);
                addWayPoint(45, 17.5);
                addWayPoint(54, 17.5);
                addWayPoint(57, 19.5);
                addWayPoint(57.5, 22);
                addWayPoint(57.5, 30);
                addWayPoint(55, 33.5);
                
                //this is the finish line
                addWayPoint(47, 33.5);
                break;

            case Maps.TRACK_02:
                addWayPoint(37, 25);
                addWayPoint(33.5, 29);
                addWayPoint(30, 34);
                addWayPoint(26.5, 32);
                addWayPoint(26.5, 21);
                addWayPoint(28, 17.5);
                addWayPoint(46, 17.5);
                addWayPoint(49.5, 14);
                addWayPoint(53, 9);
                addWayPoint(57, 11);
                addWayPoint(58, 22);
                addWayPoint(56, 25);
                
                //this is the finish line
                addWayPoint(47, 25.5);
                break;

            case Maps.TRACK_03:
                addWayPoint(37, 33.5);
                addWayPoint(33.5, 30);
                addWayPoint(30, 25.5);
                addWayPoint(20, 25.5);
                addWayPoint(17.5, 21);
                addWayPoint(21, 17.5);
                addWayPoint(54, 17.5);
                addWayPoint(57.5, 14);
                addWayPoint(61, 9);
                addWayPoint(65, 11);
                addWayPoint(66, 14);
                addWayPoint(66, 22);
                addWayPoint(63, 25);
                addWayPoint(59, 27);
                addWayPoint(55, 33.5);
                
                //this is the finish line
                addWayPoint(47, 33.5);
                break;

            case Maps.TRACK_04:
                addWayPoint(30, 33.5);
                addWayPoint(25.5, 30);
                addWayPoint(25.5, 21);
                addWayPoint(29, 17.5);
                addWayPoint(38, 17.5);
                addWayPoint(41.5, 21);
                addWayPoint(44, 25);
                addWayPoint(49, 25);
                addWayPoint(49, 13);
                addWayPoint(53, 9);
                addWayPoint(57, 12);
                addWayPoint(57, 30);
                addWayPoint(54, 33.5);
                
                //this is the finish line
                addWayPoint(39, 33.5);
                break;

            case Maps.TRACK_05:
                addWayPoint(13, 26);
                addWayPoint(9, 22);
                addWayPoint(9, 13);
                addWayPoint(13, 9);
                addWayPoint(22, 9);
                addWayPoint(26, 12);
                addWayPoint(28, 17.5);
                addWayPoint(70, 17.5);
                addWayPoint(73.5, 20);
                addWayPoint(73.5, 30);
                addWayPoint(70, 33.5);
                addWayPoint(61, 33.5);
                addWayPoint(57.5, 31);
                addWayPoint(57.5, 26);
                
                //this is the finish line
                addWayPoint(39, 25.5);
                break;

            case Maps.TRACK_06:
                addWayPoint(13, 33.5);
                addWayPoint(9.5, 30);
                addWayPoint(13, 25.5);
                addWayPoint(23, 25.5);
                addWayPoint(26.5, 21);
                addWayPoint(22, 17.5);
                addWayPoint(13, 17.5);
                addWayPoint(9.5, 13);
                addWayPoint(13, 9.5);
                addWayPoint(30, 9.5);
                addWayPoint(34, 13);
                addWayPoint(34, 23);
                addWayPoint(38, 25.5);
                addWayPoint(62, 25.5);
                addWayPoint(66, 22);
                addWayPoint(69, 17);
                addWayPoint(73.5, 20);
                addWayPoint(73.5, 30);
                addWayPoint(70, 33.5);
                
                //this is the finish line
                addWayPoint(23, 33.5);
                break;

            case Maps.TRACK_07:
                addWayPoint(13, 33.5);
                addWayPoint(9.5, 30);
                addWayPoint(9.5, 13);
                addWayPoint(13, 9.5);
                addWayPoint(17, 12);
                addWayPoint(21, 17);
                addWayPoint(26, 14);
                addWayPoint(29, 9.5);
                addWayPoint(71, 9.5);
                addWayPoint(74, 13);
                addWayPoint(70, 18);
                addWayPoint(60, 18);
                addWayPoint(57, 22);
                addWayPoint(54, 26);
                addWayPoint(45, 26);
                addWayPoint(41, 29);
                addWayPoint(38, 33.5);
                
                //this is the finish line
                addWayPoint(23, 33.5);
                break;

            case Maps.TRACK_08:
                addWayPoint(45, 33.5);
                addWayPoint(41.5, 30);
                addWayPoint(41.5, 21);
                addWayPoint(38, 17.5);
                addWayPoint(34, 20);
                addWayPoint(34, 30);
                addWayPoint(30, 33.5);
                addWayPoint(13, 33.5);
                addWayPoint(9.5, 30);
                addWayPoint(9.5, 13);
                addWayPoint(13, 9.5);
                addWayPoint(70, 9.5);
                addWayPoint(74, 13);
                addWayPoint(71, 18);
                addWayPoint(65.5, 21);
                addWayPoint(69, 26);
                addWayPoint(74, 29);
                addWayPoint(70, 33.5);
                
                //this is the finish line
                addWayPoint(55, 33.5);
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
        
        if (wayPoints != null)
        {
            for (int i = 0; i < wayPoints.size(); i++)
            {
                wayPoints.set(i, null);
            }
            
            wayPoints.clear();
            wayPoints = null;
        }
    }
}