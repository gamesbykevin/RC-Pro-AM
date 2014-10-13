package com.gamesbykevin.rcproam.map.track;

public final class TrackHelper 
{
    /**
     * Create a track
     * @return Track
     */
    public final static Track create()
    {
        Track track = new Track(84, 50, 1, 1);
        
        //assign what is part of the road
        for (int row = 0; row < track.getRows(); row++)
        {
            for (int col = 0; col < track.getColumns(); col++)
            {
                if (col < 23 || col > 62)
                    continue;
                if (row < 7 || row > 38)
                    continue;
                
                //mark as road
                track.setRoad(col, row, true);
            }
        }
        
        
        return track;
    }
}
