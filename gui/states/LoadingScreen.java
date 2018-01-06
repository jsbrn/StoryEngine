package gui.states;


import gui.GUI;
import gui.elements.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import misc.Assets;
import misc.MiscMath;
import misc.Page;
import misc.Window;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;

public class LoadingScreen extends BasicGameState {

    private static boolean debug = false;
    private static float steps, step;
    private BufferedReader br;
    public LoadingScreen(int state) {}
    public static boolean debug() { return debug; }
    @Override
    public int getID() { return Assets.LOADING_SCREEN; }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        InputStream in = Assets.class.getResourceAsStream("/resources/story.txt");
        br = new BufferedReader(new InputStreamReader(in));
        steps = Page.STORY.size(); step = 0;
        Page.load(true);
        if (!Page.load(false)) Page.setCurrentPage(1);
    }
    
    //draws state (screen) elements
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        next();
        drawCircle(Window.getWidth()/2, Window.getHeight()/2, 20, false, g);
        drawCircle(Window.getWidth()/2, Window.getHeight()/2, (20*(1-(step/steps))), true, g);
    }
    
    private void drawCircle(float x, float y, float radius, boolean fill, Graphics g) {
        if (radius < 2) return;
        g.setAntiAlias(true);
        g.setColor(Color.white);
        g.setLineWidth(2);
        if (!fill) g.drawOval(x-radius, y-radius, radius*2, radius*2);
        if (fill) g.fillOval(x-radius, y-radius, radius*2, radius*2);
    }

    //key binding and calling update() in all objects
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        if (step >= steps) sbg.enterState(Assets.PAGE_SCREEN);
    }
    
    public boolean next() {
        if (step < 0 || step >= Page.STORY.size()) return false;
        String line = Page.STORY.get((int)step);
        if (line.indexOf("\timage: ") == 0) {
            line = line.replaceAll("(\\timage: )|( \\[.*\\])", "");
            Assets.image(line);
        }
        step++;
        return true;
    }

}
