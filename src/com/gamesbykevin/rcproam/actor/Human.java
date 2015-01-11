package com.gamesbykevin.rcproam.actor;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.Track;
import com.gamesbykevin.rcproam.resources.GameAudio;

import java.awt.event.KeyEvent;

public final class Human extends Car
{
    //timer used to determine which engine sound to play
    private Timer timer;
    
    //how long the initial accelerate will be for the sound effect
    private static final long ACCELERATE_DELAY = Timers.toNanoSeconds(9750L);
    
    //is the player honking the horn
    private boolean horn = false;
    
    //the facing angle when we start turning (used to determine when to play "turn" sound effect)
    private double facingAngle;
    
    //flag to determine if we started turning (used to determine when to play "turn" sound effect)
    private boolean turn = false;
    
    public Human() throws Exception
    {
        super(true);
        
        //create timer
        this.timer = new Timer(ACCELERATE_DELAY);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //object representing the current track
        final Track track = engine.getManager().getMaps().getMap().getTrack();
        
        final boolean hasRoad = track.isRoad(this);
        
        //update basic elements for car: gravity, speed, etc...
        updateBasicElements(track, engine.getMain().getTime());
        
        //if we were on the road and are no longer
        if (hasRoad && !track.isRoad(this))
        {
            //stop any engine accelerating sound
            engine.getResources().stopGameAudio(GameAudio.Keys.Engine);
            engine.getResources().stopGameAudio(GameAudio.Keys.EngineLoop);
            
            //stop turn sound effect
            engine.getResources().stopGameAudio(GameAudio.Keys.Turn);
                
            //play engine slow down effect
            engine.getResources().playGameAudio(GameAudio.Keys.EngineSlow);
        }
        
        //get keyboard input object
        final Keyboard keyboard = engine.getKeyboard();
        
        //can only do one or the other
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            //if we arent turning yet, store the facing angle
            if (!super.isTurningRight())
                this.facingAngle = super.getFacingAngle();
            
            setTurnRight(true);
            setTurnLeft(false);
        }
        else if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            //if we arent turning yet, store the facing angle
            if (!super.isTurningLeft())
                this.facingAngle = super.getFacingAngle();
            
            setTurnRight(false);
            setTurnLeft(true);
        }
        
        //make sure we are turning
        if (isTurningLeft() || isTurningRight())
        {
            //check if the difference is enough to play the turn car angle
            if (facingAngle - getFacingAngle() > TURN_INTERVAL ||
                getFacingAngle() - facingAngle > TURN_INTERVAL)
            {
                //make sure we are on the road as well
                if (track.isRoad(this))
                {
                    //if we haven't flagged turn
                    if (!turn)
                    {
                        //flag turn
                        turn = true;

                        //play turn sound effect
                        engine.getResources().playGameAudio(GameAudio.Keys.Turn, true);
                    }
                }
            }
        }
        else
        {
            //if we aren't turning and turn is flagged, stop playing sound
            if (turn)
            {
                //flag no longer turning
                turn = false;
                
                //stop sound effect
                engine.getResources().stopGameAudio(GameAudio.Keys.Turn);
            }
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_A))
        {
            /*
             * 1. If we previously were not accelerating or
             * 2. If we were previously not on the road and now are
             * 
             * Then play sound effect!!!!!!!!
             */
            if (!hasAccelerate() || !hasRoad && track.isRoad(this))
            {
                //play engine accelerate sound effect
                engine.getResources().playGameAudio(GameAudio.Keys.Engine);
                
                //reset timer
                timer.reset();
            }
            else
            {
                //check if the time has passed
                boolean passed = timer.hasTimePassed();
                
                //update timer
                timer.update(engine.getMain().getTime());
                
                //if enough time has passed it is now time to play the engine loop
                if (!passed && timer.hasTimePassed())
                {
                    //play engine loop
                    engine.getResources().playGameAudio(GameAudio.Keys.EngineLoop, true);
                }
            }
            
            //flag accelerate true
            setAccelerate(true);
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_S))
        {
            //if not already honking horn
            if (!horn)
            {
                //play sound effect
                engine.getResources().playGameAudio(GameAudio.Keys.Horn);

                //flag effect is played
                horn = true;
            }
        }
        
        //if we previously were turning and released the turn key
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT) || keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            setTurnLeft(false);
            setTurnRight(false);
            
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_A))
        {
            if (hasAccelerate())
            {
                //stop any engine accelerate noise
                engine.getResources().stopGameAudio(GameAudio.Keys.Engine);
                engine.getResources().stopGameAudio(GameAudio.Keys.EngineLoop);
                
                //play engine slow down effect
                engine.getResources().playGameAudio(GameAudio.Keys.EngineSlow);
                
                //reset timer
                timer.reset();
            }
            
            setAccelerate(false);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_S))
        {
            //no longer playing sound effect
            horn = false;
        }
    }
}