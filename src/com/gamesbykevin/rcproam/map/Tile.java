package com.gamesbykevin.rcproam.map;

public class Tile 
{
    //is this tile part of the road
    private boolean road = false;
    
    //the length back to the starting line
    private int cost = 0;
    
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
     * Get the cost
     * @return the length back to the start position
     */
    protected int getCost()
    {
        return this.cost;
    }
    
    /**
     * Set the cost
     * @param cost the length from this tile to the starting position
     */
    protected void setCost(final int cost)
    {
        this.cost = cost;
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