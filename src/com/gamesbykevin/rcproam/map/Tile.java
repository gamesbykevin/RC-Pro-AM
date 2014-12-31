package com.gamesbykevin.rcproam.map;

public class Tile 
{
    //is this tile part of the road
    private boolean road = false;
    
    //has this tile been visited
    private boolean visited = false;
    
    protected Tile(final boolean road)
    {
        this.road = road;
    }
    
    /**
     * Mark this tile are part of the road or not
     * @param road true if part of road, false otherwise
     */
    protected void setRoad(final boolean road)
    {
        this.road = road;
    }
    
    /**
     * Is this tile part of the road
     * @return true if part of road, false otherwise
     */
    protected boolean isRoad()
    {
        return this.road;
    }
    
    /**
     * Mark this tile as visited
     * @param visited true if visited, false otherwise
     */
    protected void setVisited(final boolean visited)
    {
        this.visited = visited;
    }
    
    /**
     * Has this tile been visited
     * @return true if visited, false otherwise
     */
    protected boolean hasVisited()
    {
        return this.visited;
    }
}