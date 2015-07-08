package com.gamesbykevin.rcproam.manager;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.rcproam.car.Cars;
import com.gamesbykevin.rcproam.engine.Engine;
import com.gamesbykevin.rcproam.map.Maps;
import com.gamesbykevin.rcproam.menu.CustomMenu;
import com.gamesbykevin.rcproam.menu.CustomMenu.*;
import com.gamesbykevin.rcproam.resources.*;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //where gameplay occurs
    private Rectangle window;
    
    //the container for our race cars
    private Cars cars;
    
    //the maps of the tracks
    private Maps maps;
    
    //do we hide mini-map, time, leaderboard
    private boolean enableDetail = false;
    
    //the size of the info screen at the bottom
    private static final int INFO_WIDTH = 256;
    private static final int INFO_HEIGHT = 64;
    
    //different car types
    private static final int CAR_TYPE_TRUCK = 0;
    private static final int CAR_TYPE_SUV = 1;
    private static final int CAR_TYPE_RACE_CAR = 2;
    
    //the start delay is 5 seconds
    private static final long START_DELAY = Timers.toNanoSeconds(5000L);
    
    //the finish delay is 4 seconds
    private static final long FINISH_DELAY = Timers.toNanoSeconds(4000L);
    
    //the next delay is 2.5 seconds
    private static final long NEXT_DELAY = Timers.toNanoSeconds(2500L);
    
    //the image for win and lose
    private Image win, lose;
    
    private enum Transition
    {
        Start, Finish, Next
    }
    
    //the timers for the game transitions
    private Timers timers;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //set the audio depending on menu setting
        engine.getResources().setAudioEnabled(Toggle.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.OptionsInGame, OptionKey.Sound)] == Toggle.Off);
        
        //the window available for gameplay
        Rectangle screen = new Rectangle(engine.getMain().getScreen());
        
        //the height will be smaller to make room for the info screen
        screen.height -= INFO_HEIGHT;
        
        //set the game window where game play will occur
        setWindow(screen);
        
        //do we hide the detail screen
        setEnableDetailScreen(Toggle.values()[engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.EnableDetail)] == Toggle.Off);
        
        //store these images
        win = engine.getResources().getGameImage(GameImages.Keys.Win);
        lose = engine.getResources().getGameImage(GameImages.Keys.GameOver);
        
        //maps = new Maps(engine.getResources().getGameImage(GameImages.Keys.Maps), getWindow());
        //hero.setImage(engine.getResources().getGameImage(GameImages.Keys.Heroes));
        //enemies = new Enemies(engine.getResources().getGameImage(GameImages.Keys.Enemies));
        
        //are we playing random Mode or Campaign
        //random = CustomMenu.Toggle.values()[engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Mode)];
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        if (this.cars == null)
        {
            //create new container for cars
            this.cars = new Cars();
            
            //set the handicap setting
            this.cars.setHandicap(Toggle.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Handicap)] == Toggle.On);
            
            //are we checking for collision
            this.cars.setCheckCollision(Toggle.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Collision)] == Toggle.On);
            
            switch (engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.CarType))
            {
                case CAR_TYPE_SUV:
                    //add human car first
                    this.cars.addHuman(engine.getResources().getGameImage(GameImages.Keys.SuvRed), Color.RED, "Red");

                    //add cpu car(s)
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.SuvBlue), Color.BLUE, "Blue", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.SuvGreen), Color.GREEN, "Green", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.SuvOrange), Color.ORANGE, "Orange", engine.getRandom());
                    break;
                    
                case CAR_TYPE_TRUCK:
                    //add human car first
                    this.cars.addHuman(engine.getResources().getGameImage(GameImages.Keys.TruckRed), Color.RED, "Red");

                    //add cpu car(s)
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckBlue), Color.BLUE, "Blue", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckGreen), Color.GREEN, "Green", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.TruckOrange), Color.ORANGE, "Orange", engine.getRandom());
                    break;
                    
                case CAR_TYPE_RACE_CAR:
                    //add human car first
                    this.cars.addHuman(engine.getResources().getGameImage(GameImages.Keys.RaceCarRed), Color.RED, "Red");

                    //add cpu car(s)
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.RaceCarBlue), Color.BLUE, "Blue", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.RaceCarGreen), Color.GREEN, "Green", engine.getRandom());
                    this.cars.addCpu(engine.getResources().getGameImage(GameImages.Keys.RaceCarOrange), Color.ORANGE, "Orange", engine.getRandom());
                    break;
                    
                default:
                    throw new Exception("Car Type not setup here");
            }
            
            //set human in center of screen
            this.cars.getHuman().setLocation(getWindow());
        }
        
        if (this.timers == null)
        {
            //create timer container
            this.timers = new Timers(engine.getMain().getTime());
            
            //add timer to wait at race start
            this.timers.add(Transition.Start, START_DELAY);
            
            //add timer after race finish
            this.timers.add(Transition.Finish, FINISH_DELAY);
            
            //add timer for time before race start again
            this.timers.add(Transition.Next, NEXT_DELAY);
        }
        
        //reset timers
        this.timers.reset();
        
        //create maps if it does not already exist
        if (this.maps == null)
            this.maps = new Maps(engine.getResources());
    }
    
    public void setEnableDetailScreen(final boolean enableDetail)
    {
        this.enableDetail = enableDetail;
    }
    
    public Cars getCars()
    {
        return this.cars;
    }
    
    public Maps getMaps()
    {
        return this.maps;
    }
    
    @Override
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    @Override
    public void setWindow(final Rectangle window)
    {
        this.window = new Rectangle(window);
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (window != null)
            window = null;
        
        if (cars != null)
        {
            cars.dispose();
            cars = null;
        }
        
        if (maps != null)
        {
            maps.dispose();
            maps = null;
        }
        
        if (win != null)
        {
            win.flush();
            win = null;
        }
        
        if (lose != null)
        {
            lose.flush();
            lose = null;
        }
        
        try
        {
            //recycle objects
            super.finalize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Update all elements
     * @param engine Our game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        if (maps != null)
        {
            //update map first
            maps.update(engine);
            
            //if no longer loading
            if (!maps.isLoading())
            {
                //now update the cars
                if (cars != null)
                {
                    if (!timers.hasStarted(Transition.Start))
                    {
                        //reset cars
                        cars.reset(engine.getRandom());
                        
                        //play race start sound
                        engine.getResources().playGameAudio(GameAudio.Keys.RaceStart);
                        
                        //update timer
                        timers.update(Transition.Start);
                        
                        //place the cars in perspective to the human racer
                        cars.adjustCars(engine);
                    }
                    else
                    {
                        if (!timers.hasTimePassed(Transition.Start))
                        {
                            //update timer
                            timers.update(Transition.Start);
                        }
                        else
                        {
                            //if the race is not over yet
                            if (!cars.hasRaceCompleted())
                            {
                                //update the cars as long as the race has not completed
                                cars.update(engine);
                                
                                //if the race wasn't complete, but now is
                                if (cars.hasRaceCompleted())
                                {
                                    //play race start sound
                                    engine.getResources().playGameAudio(GameAudio.Keys.RaceFinish);
                                }
                            }
                            else
                            {
                                //if the race is over has the finish time passed
                                if (!timers.hasTimePassed(Transition.Finish))
                                {
                                    //update timer
                                    timers.update(Transition.Finish);
                                    
                                    //if the timer has now finished
                                    if (timers.hasTimePassed(Transition.Finish))
                                    {
                                        //play the next appropriate sound if win/lose
                                        if (cars.hasWin())
                                        {
                                            engine.getResources().playGameAudio(GameAudio.Keys.RaceWin);
                                            
                                            //also set the next map
                                            maps.setMap(engine, maps.getIndex() + 1);
                                        }
                                        else
                                        {
                                            engine.getResources().playGameAudio(GameAudio.Keys.RaceLose);
                                        }
                                    }
                                }
                                else
                                {
                                    //only continue if win
                                    if (cars.hasWin())
                                    {
                                        //update timer
                                        timers.update(Transition.Next);
                                        
                                        //if the next time has passed move to the next map and reset
                                        if (timers.hasTimePassed(Transition.Next))
                                        {
                                            //reset timers
                                            timers.reset();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        if (maps != null)
        {
            if (maps.isLoading())
            {
                //draw the map
                maps.render(graphics);
            }
            else
            {
                //if the race is finished and the finish timer is complete
                if (cars.hasRaceCompleted() && timers.hasTimePassed(Transition.Finish))
                {
                    if (cars.hasWin())
                    {
                        //draw win background
                        graphics.drawImage(win, 0, 0, null);
                        
                        //where the mini-map will be drawn
                        final int x = getWindow().x + (getWindow().width / 2) - (Maps.MINIMAP_WIDTH / 2);
                        final int y = getWindow().y + (getWindow().height / 2) + Maps.MINIMAP_HEIGHT;

                        //draw the mini-map with the cars on the map
                        maps.renderMiniMap(graphics, null, x, y);
                    }
                    else
                    {
                        graphics.drawImage(lose, 0, 0, null);
                    }
                }
                else
                {
                    //draw the map
                    maps.render(graphics);

                    //now draw the race cars
                    cars.render(graphics);

                    if (enableDetail)
                    {
                        //set background color for stats/info screen
                        graphics.setColor(Color.BLACK);
                        graphics.fillRect(getWindow().x, getWindow().y + getWindow().height, INFO_WIDTH, INFO_HEIGHT);

                        //where the mini-map will be drawn
                        final int x = getWindow().x + (getWindow().width / 2) - (Maps.MINIMAP_WIDTH / 3);
                        final int y = getWindow().y + getWindow().height + (INFO_HEIGHT / 2) - (Maps.MINIMAP_HEIGHT / 2);

                        //draw the mini-map with the cars on the map
                        maps.renderMiniMap(graphics, cars, x, y);

                        //draw human car info
                        cars.renderTimeInfo(graphics, getWindow().x + 1, getWindow().y + getWindow().height + (INFO_HEIGHT / 5), maps.getMap().getLaps());

                        //draw the leaderboard
                        cars.renderLeaderboard(graphics, x + (int)(Maps.MINIMAP_WIDTH * 1.1), getWindow().y + getWindow().height + (INFO_HEIGHT / 5));
                    }
                }
            }
        }
    }
}