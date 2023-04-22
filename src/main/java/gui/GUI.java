package gui;

import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.Image;
import com.github.mathiewz.slick.SlickException;
import gui.elements.GUIElement;
import misc.Assets;

import java.util.HashMap;
import java.util.LinkedList;

public class GUI {
    
    private LinkedList<GUIElement> elements;
    private int scale;
    private static HashMap<String, Image> images;
    
    public static boolean DEBUG_MODE = false;
    
    public GUI() {
        this.elements = new LinkedList<GUIElement>();
        this.scale = 1;
        this.images = new HashMap<String, Image>();
    }
    
    public static Image image(String asset) {
        return image(asset, Image.FILTER_NEAREST);
    }
    
    public static Image image(String asset, int filter) {
        if (asset == null) return null;
        if (asset.length() == 0) return null;
        if (images.containsKey(asset)) return (Image)images.get(asset);
        Image img = null;
        try {
            img = new Image("resources/gui/"+asset, false, filter);
            images.put(asset, img);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        System.out.print(img == null ? asset+" is null!\n" : "");
        return img;
    }
    
    public String[] wrap(String text, int width) {
        String[] split = text.trim().split("\\s"); //split on spaces
        String[] result = new String[0];
        LinkedList<String> temp = new LinkedList<String>(); temp.add("");
        int line_width = 0;
        for (String word: split) {
            int word_width = Assets.getFont().getWidth(word+" ");
            line_width += word_width;
            if (line_width <= width) { 
                temp.set(temp.size()-1, temp.getLast()+word+" ");
            } else { temp.add(word+" "); line_width = word_width; }
        }
        return temp.toArray(result);
    }
    
    public int getScale() { return scale; }
    public void setScale(int scale) { this.scale = scale; }
    
    public void update() {
        for (int i = elements.size()-1; i > -1; i--) {
            GUIElement e = elements.get(i);
            e.update();
        }
    }
    
    public boolean addElement(GUIElement e) {
        e.setParent(this);
        elements.add(e);
        return true;
    }
    
    public void removeElement(GUIElement e) {
        elements.remove(e);
    }
    
    public void draw(Graphics g) {
        //draw subtle shadow around border
        //g.drawImage(image("shadow.png").getScaledCopy(Window.getWidth(), Window.getHeight()), 0, 0);
        //draw elements
        for (int i = 0; i < elements.size(); i++) elements.get(i).draw(g);
    }
    
    public final boolean handleKeyPress(int key, char c) {
        for (int i = elements.size()-1; i > -1; i--) {
            GUIElement e = elements.get(i);
            boolean succ = e.onKeyPress(key, c);
            if (succ) return true;
        }
        return false;
    }
    
    public final boolean handleKeyRelease(int key, char c) {
        for (int i = elements.size()-1; i > -1; i--) {
            GUIElement e = elements.get(i);
            boolean succ = e.onKeyRelease(key, c);
            if (succ) return true;
        }
        return false;
    }
    
    public final boolean handleMouseRelease(int x, int y, int button) {
        for (int i = elements.size()-1; i > -1; i--) {
            GUIElement e = elements.get(i);
            boolean succ = e.onMouseClick(x, y, button);
            if (succ) return true;
        }
        return false;
    }
    
    public final boolean handleMouseScroll(int d) {
        for (int i = elements.size()-1; i > -1; i--) {
            GUIElement e = elements.get(i);
            boolean succ = e.onMouseScroll(d);
            if (succ) return true;
        }
        return false;
    }
    
    public final boolean handlePageSwitch() {
        for (int i = elements.size()-1; i > -1; i--) {
            GUIElement e = elements.get(i);
            e.onPageSwitch();
        }
        return false;
    }
    
}
