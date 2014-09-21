package com.gamesbykevin.rcproam.resources;

import com.gamesbykevin.framework.resources.*;

/**
 * All game images
 * @author GOD
 */
public final class GameImages extends ImageManager
{
    //description for progress bar
    private static final String DESCRIPTION = "Loading Image Resources";
    
    /**
     * These are the keys used to access the resources and need to match the id in the xml file
     */
    public enum Keys
    {
        Track01, Track02, Track03, Track04, Track05, 
        Track06, Track07, Track08, Track09, Track10, 
        Track11, Track12, Track13, Track14, Track15, 
        Track16, Track17, Track18, Track19, Track20, 
        Track21, Track22, Track23, Track24, 
    }
    
    public GameImages() throws Exception
    {
        super(Resources.XML_CONFIG_GAME_IMAGE);
        
        //the description that will be displayed for the progress bar
        super.setProgressDescription(DESCRIPTION);
        
        if (Keys.values().length < 1)
            super.increase();
    }
}