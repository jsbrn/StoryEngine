package main;

import gui.states.LoadingScreen;
import gui.states.PageScreen;
import java.io.IOException;
import misc.Assets;
import misc.MiscSound;
import misc.Window;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;

public class SlickInitializer extends StateBasedGame {


    public SlickInitializer(String gameTitle) {
        super(gameTitle); //set window title to "gameTitle" string
        //add states
        addState(new PageScreen(Assets.PAGE_SCREEN));
        addState(new LoadingScreen(Assets.LOADING_SCREEN));
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        //initialize states
        getState(Assets.PAGE_SCREEN).init(gc, this);
        getState(Assets.LOADING_SCREEN).init(gc, this);
        //enter state
        this.enterState(Assets.LOADING_SCREEN, new FadeInTransition(), null);
    }

    public static void main(String args[]) throws IOException {

        Assets.init();
        
        try {
            //set window properties
            Window.WINDOW_INSTANCE = new AppGameContainer(new SlickInitializer(""));
            Window.WINDOW_INSTANCE.setDisplayMode(Window.getDisplayWidth()/2, Window.getDisplayHeight()/2, false);
            Window.WINDOW_INSTANCE.setResizable(true);
            Window.WINDOW_INSTANCE.setShowFPS(false);
            Window.WINDOW_INSTANCE.setVSync(true);
            Window.setFullscreen();
            Window.WINDOW_INSTANCE.setAlwaysRender(true);
            Window.WINDOW_INSTANCE.setUpdateOnlyWhenVisible(false);
            Window.WINDOW_INSTANCE.setIcons(new String[]{
                "resources/icon-16x16.png",
                "resources/icon-32x32.png",
                "resources/icon-64x64.png",
                "resources/icon-128x128.png"});
            Window.WINDOW_INSTANCE.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean closeRequested() {
        Assets.getSoundSystem().cleanup(); //clean up sound system on close
        return true;
    }
    
}
