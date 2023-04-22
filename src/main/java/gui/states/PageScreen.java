package gui.states;


import gui.GUI;
import gui.elements.*;
import misc.Assets;
import misc.MiscMath;
import misc.Window;
import org.lwjgl.input.Mouse;
import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.Input;
import com.github.mathiewz.slick.SlickException;
import com.github.mathiewz.slick.state.BasicGameState;
import com.github.mathiewz.slick.state.StateBasedGame;

public class PageScreen extends BasicGameState {

    private static boolean debug = false;
    private static GUI gui;
    
    public PageScreen(int state) {}
    public static boolean debug() { return debug; }
    @Override
    public int getID() { return Assets.PAGE_SCREEN; }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        gui = new GUI();
        ImageBackground i = new ImageBackground();
        i.setAnchor(null, GUIElement.CENTER, GUIElement.CENTER, 0, 0);
        gui.addElement(i);
        TextViewer t = new TextViewer();
        t.setAnchor(null, GUIElement.CENTER, GUIElement.BOTTOM_IN, 0, 50);
        gui.addElement(t);
        ChoiceViewer c = new ChoiceViewer();
        c.setAnchor(null, GUIElement.CENTER, GUIElement.BOTTOM_IN, 0, 125);
        gui.addElement(c);
        TextBox tb = new TextBox();
        tb.setAnchor(null, GUIElement.CENTER, GUIElement.BOTTOM_IN, 0, 125);
        gui.addElement(tb);
    }

    public static GUI getGUI() {
        return gui;
    }
    
    //draws state (screen) elements
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        gui.draw(g);
    }

    //key binding and calling update() in all objects
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        MiscMath.DELTA_TIME = delta > 200 ? 200 : delta; //200ms between frames is 5FPS
        gui.update();
        int scroll = Mouse.getDWheel();
        if (scroll != 0) gui.handleMouseScroll(scroll);
        gc.getInput().enableKeyRepeat();
    }

    public void keyPressed(int key, char c) {
        gui.handleKeyPress(key, c);
    }
    
    @Override
    public void keyReleased(int key, char c) {
        gui.handleKeyRelease(key, c);
        if (key == Input.KEY_F11) Window.setFullscreen();
    }
    
    @Override
    public void mouseClicked(int button, int x, int y, int click_count) {
       
    }

}
