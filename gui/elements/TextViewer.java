package gui.elements;

import gui.GUI;
import java.awt.Point;
import java.util.ArrayList;
import misc.Assets;
import misc.MiscMath;
import misc.Page;
import misc.Window;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Polygon;

public class TextViewer extends GUIElement {
    
    private int start_index;
    private double anim, dir;
    private String last_text;
    private ArrayList<String> parsed_text;
    
    private Color WHITE, BLACK;

    public TextViewer() {
        this.start_index = 0;
        this.WHITE = new Color(255, 255, 255);
        this.BLACK = new Color(0, 0, 0);
        this.last_text = "";
        this.parsed_text = new ArrayList<String>();
        this.dir = 1;
    }
    
    @Override
    public void update() {
        anim += MiscMath.getConstant(dir, 1);
        anim = MiscMath.clamp(anim, 0, 1);
        if (anim >= 1) { start_index = 0; last_text = Page.getCurrentPage().getText(); anim = 0; dir = 0; }
    }

    @Override
    public int[] dims() {
        return new int[]{(int)MiscMath.clamp(Window.getWidth()/2, 600, 1200), 
            Window.getHeight() - 50 - 275};
    }

    @Override
    public boolean onMouseScroll(int d) {
        if (anim >= 1) return false;
        int h_offset = 0, h_rows = (int)MiscMath.clamp(parsed_text.size(), 1, dims()[1]/(5+Assets.getFont().getHeight()));
        int end_index = (int)MiscMath.clamp(h_rows + start_index, 1, parsed_text.size());
        
        start_index += d > 0 ? -1 : 1;
        start_index = (int)MiscMath.clamp(start_index, 
                0, parsed_text.size()-1);
        
        if (end_index >= parsed_text.size() && d < 0) start_index = parsed_text.size() - h_rows; //do not scroll past lower bound
        return true;
    }

    @Override
    public boolean onPageSwitch() {
        anim = 0;
        dir = 2;
        //last_page = Page.getCurrentPage().getText();
        return true;
    }

    @Override
    public boolean onKeyPress(int key, char c) {
        if (key == Input.KEY_UP) { onMouseScroll(1); return true; }
        if (key == Input.KEY_DOWN) { onMouseScroll(-1); return true; }
        if (c == 'c' && anim <= 0) { Page.getCurrentPage().next(); return true; }
        if (c == 'x' && anim <= 0) { Page.confirmQuit(); return true; }
        if (key == Input.KEY_F3) GUI.DEBUG_MODE = !GUI.DEBUG_MODE;
        return false;
    }
    
    @Override
    public void draw(Graphics g) {
        g.setFont(Assets.getFont());
        parsed_text = parseText(last_text);
        float[] osc = osc();
        drawText(osc[0] - (float)((anim)*(double)Window.getWidth()), osc[1], start_index, parsed_text, true, g);
        drawText(osc[0] + Window.getWidth() - (float)(anim*(double)Window.getWidth()), osc[1], 
                0, parseText(Page.getCurrentPage().getText()), false, g);
    }
    
    private void drawText(float x, float y, int start, ArrayList<String> parsed, boolean arrow, Graphics g) {
        int h_offset = 0, h_rows = (int)MiscMath.clamp(parsed.size(), 1, dims()[1]/(5+Assets.getFont().getHeight()));
        int end_index = (int)MiscMath.clamp(h_rows + start, 1, parsed.size());
        for (int i = start; i < end_index; i++) {
            if (i < 0 || i >= parsed.size()) continue;
            String s = parsed.get(i);
            g.setColor(BLACK);
            if (!Assets.TEXT_BACKGROUND) {
                g.drawString(s, x + 1, y + h_offset + 1);
            } else {
                g.setColor(new Color(0, 0, 0, 175));
                if (s.trim().length() > 0) g.fillRect(x - 6, y - 2 + h_offset, Assets.getFont().getWidth(s) + 6, Assets.getFont().getHeight(s) + 4);
            }
            g.setColor(WHITE);
            g.drawString(s, x, y + h_offset);
            h_offset+=Assets.getFont().getHeight(s)+5;
        }
        boolean blink = System.currentTimeMillis() % 1000 < 500;
        if (start > 0 && arrow && blink) drawArrow(true, g);
        if (end_index < parsed.size()) { if (arrow && blink) drawArrow(false, g); } else { start_index = parsed.size() - h_rows; }
    }
    
    private ArrayList<String> parseText(String text) {
        ArrayList<String> parsed = new ArrayList<String>();
        String[] newlines = text.split("(\\\\n)");
        for (String line: newlines) {
            String[] wrapped = getParent().wrap(line, dims()[0]);
            for (String w: wrapped) parsed.add(w);
            parsed.add(" ");
        }
        return parsed;
    }
    
    private void drawArrow(boolean up, Graphics g) {
        float[] osc = osc();
        int[] dims = dims();
        Point p = new Point((int)(osc[0] + (dims[0]) + 10), (int)(up ? osc[1] - 32 : osc[1] + dims[1] + 24));
        Polygon arrow = new Polygon();
        arrow.addPoint((int)p.getX(), (int)p.getY()); 
        arrow.addPoint((int)(p.getX() - 15), (int)(up ? p.getY() + 15 : p.getY() - 15)); 
        arrow.addPoint((int)(p.getX() + 15), (int)(up ? p.getY() + 15 : p.getY() - 15));
        g.setAntiAlias(true);
        g.setColor(WHITE);
        g.fill(arrow);
        g.setColor(BLACK);
        g.draw(arrow);
        g.setAntiAlias(false);
    }
    
}
