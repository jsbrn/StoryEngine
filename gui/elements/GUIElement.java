package gui.elements;

import gui.GUI;
import misc.Window;
import org.newdawn.slick.Graphics;

public class GUIElement {
    
    private GUI parent;
    private String context;
    
    private GUIElement anchor;
    private int anchor_x, anchor_y, anchor_offset[];
    private boolean requires_input;
    public static final int LEFT_OUT = 0, RIGHT_OUT = 1, LEFT_IN = 2, RIGHT_IN = 3, 
            CENTER = 4, TOP_OUT = 5, BOTTOM_OUT = 6, TOP_IN = 7, BOTTOM_IN = 8;

    public boolean requiresInput() { return requires_input; }
    
    protected void setRequiresInput(boolean b) { requires_input = b; }
    
    public void setContext(String s) { context = s; }
    public String getContext() { return context; }
    
    public boolean onKeyPress(int key, char c) { return false; }
    public boolean onKeyRelease(int key, char c) { return false; }
    public boolean onMouseClick(int x, int y, int button) { return false; }
    public boolean onMouseScroll(int d) { return false; }
    public boolean onPageSwitch() { return false; }

    public final void setParent(GUI parent) { this.parent = parent; }
    public final GUI getParent() { return parent; }
    
    public void setAnchor(GUIElement anch, int x_mode, int y_mode, int offset_x, int offset_y) {
        anchor = anch; anchor_x = x_mode; anchor_y = y_mode; anchor_offset = new int[]{offset_x, offset_y};
    }
    
    public void update() {}
    
    public float[] osc() { 
        float rx = 0, ry = 0;
        if (anchor_offset == null) anchor_offset = new int[2];
        if (anchor_x == LEFT_OUT) rx = -anchor_offset[0] - dims()[0];
        if (anchor_x == RIGHT_OUT) rx = anchor_offset[0] + (anchor == null ? Window.getWidth() : anchor.dims()[0]);
        if (anchor_x == LEFT_IN) rx = anchor_offset[0];
        if (anchor_x == RIGHT_IN) rx = (anchor == null ? Window.getWidth() : anchor.dims()[0]) - dims()[0] - anchor_offset[0];
        if (anchor_x == CENTER) rx = (anchor == null ? Window.getWidth()/2 : anchor.dims()[0]/2)  - (dims()[0]/2);
        
        if (anchor_y == TOP_OUT) ry = -anchor_offset[1] - dims()[1];
        if (anchor_y == BOTTOM_OUT) ry = anchor_offset[1] + (anchor == null ? Window.getHeight() : anchor.dims()[1]);
        if (anchor_y == TOP_IN) ry = anchor_offset[1];
        if (anchor_y == BOTTOM_IN) ry = (anchor == null ? Window.getHeight() : anchor.dims()[1]) - dims()[1] - anchor_offset[1];
        if (anchor_y == CENTER) ry = (anchor == null ? Window.getHeight()/2 : anchor.dims()[1]/2)  - (dims()[1]/2);
        
        rx += anchor != null ? anchor.osc()[0] : 0;
        ry += anchor != null ? anchor.osc()[1] : 0;
        
        return new float[]{rx, ry};
    }

    public int[] dims() { return new int[]{64, 64}; }
    
    public void draw(Graphics g) {}
    
    
    
}
