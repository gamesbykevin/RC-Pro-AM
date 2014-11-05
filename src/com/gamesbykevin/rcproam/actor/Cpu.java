package com.gamesbykevin.rcproam.actor;

import com.gamesbykevin.rcproam.car.Car;
import com.gamesbykevin.rcproam.engine.Engine;

public final class Cpu extends Car
{
    public Cpu() throws Exception
    {
        super(false);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update basic elements for car: gravity, speed, etc...
        super.updateBasic(engine.getManager().getMaps().getMap().getTrack());
        
        
    }
}