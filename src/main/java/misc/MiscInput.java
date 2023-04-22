package misc;

import gui.elements.GUIElement;
import com.github.mathiewz.slick.Input;

public class MiscInput {

    private static Input input;
    
    public static void setInput(Input input) {
        MiscInput.input = input;
    }
    
    public static boolean isKeyDown(int key) {
        return input.isKeyDown(key);
    }
    
    public static boolean mouseHovers(GUIElement e) {
        return MiscMath.pointIntersectsRect(input.getMouseX(), input.getMouseY(), 
                e.osc()[0], e.osc()[1], e.dims()[0], e.dims()[1]);
        
    }
    
    public static boolean isMousePressed(int button) {
        return input.isMousePressed(button);
    }
    
    public static boolean isMouseDown(int button) {
        return input.isMouseButtonDown(button);
    }
    
    public static int getMouseX() { return input.getMouseX(); }
    public static int getMouseY() { return input.getMouseY(); }

}