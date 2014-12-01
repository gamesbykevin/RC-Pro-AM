package com.gamesbykevin.rcproam.actor;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.engine.Engine;
import java.awt.event.KeyEvent;

public final class Human extends Car
{
    public Human() throws Exception
    {
        super(true);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update basic elements for car: gravity, speed, etc...
        updateBasicElements(engine.getManager().getMaps().getMap().getTrack());
        
        //get keyboard input object
        final Keyboard keyboard = engine.getKeyboard();
        
        //can only do one or the other
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            setTurnRight(true);
            setTurnLeft(false);
        }
        else if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            setTurnRight(false);
            setTurnLeft(true);
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_A))
            setAccelerate(true);
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_S))
            setAttack(true);
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            setTurnRight(false);
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            setTurnLeft(false);
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_A))
            setAccelerate(false);
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_S))
            setAttack(false);
    }
}