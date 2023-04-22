package gui.elements;

import com.github.mathiewz.slick.Color;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.Input;
import misc.Assets;
import misc.MiscMath;
import misc.Page;
import misc.Window;

import java.util.ArrayList;

public class ChoiceViewer extends GUIElement {

    private double maxtime, time;
    private double anim, quit_anim, next_anim;
    private String next_text;
    private ArrayList<String> choices;
    
    public ChoiceViewer() {
        this.choices = new ArrayList<String>();
        this.quit_anim = 1;
        this.next_anim = 1;
        this.anim = 1;
        this.next_text = "";
    }

    @Override
    public void update() {
        ArrayList<String> current_choices = Page.getCurrentPage().getChoices();
        boolean ntxt = next_text.equals(Page.getCurrentPage().getNextText()) && next_text.length() > 0;
        boolean ctxt = choices.equals(current_choices);
        //slide up if equal, slide down if not
        anim += MiscMath.getConstant(ctxt ? -2: 2, 1); anim = MiscMath.clamp(anim, 0, 1);
        if (anim >= 1 && !current_choices.isEmpty()) {
            choices = current_choices;
            time = Page.getCurrentPage().getTime();
            maxtime = time;
        } 
        //quit and next buttons
        quit_anim += MiscMath.getConstant(!Page.isQuitPage() && !Page.getCurrentPage().requiresInput() ? -2 : 2, 1); quit_anim = MiscMath.clamp(quit_anim, 0, 1);
        next_anim += MiscMath.getConstant(ntxt ? -2 : 2, 1); next_anim = MiscMath.clamp(next_anim, 0, 1);
        if (next_anim >= 1) next_text = Page.getCurrentPage().getNextText();
        //countdown choice timer
        time -= MiscMath.getConstant(anim <= 0 ? 1 : -1, 1); time = MiscMath.clamp(time, 0, Integer.MAX_VALUE);
        if (time <= 0) { Page.getCurrentPage().timeOut(); }
    }

    @Override
    public boolean onPageSwitch() {
        return true;
    }
    
    public float[] osc() {
        float[] osc = super.osc();
        osc[1] += (anim)*(double)((Window.getHeight() + 120)-osc[1]);
        return osc;
    }

    @Override
    public int[] dims() {
        return new int[]{0, 0};
    }
    
    @Override
    public void draw(Graphics g) {
        g.setFont(Assets.getFont());
        float[] osc = osc();
        if (choices.size() >= 3) drawKey('W', osc[0], osc[1]-(maxtime > 0 ? 48 : 32), 20, choices.get(2), UP, g);
        if (choices.size() >= 1) drawKey('A', osc[0]-48, osc[1], 20, choices.get(0), LEFT, g);
        if (choices.size() >= 4) drawKey('S', osc[0], osc[1]+(maxtime > 0 ? 48 : 32), 20, choices.get(3), DOWN, g);
        if (choices.size() >= 2) drawKey('D', osc[0]+48, osc[1], 20, choices.get(1), RIGHT, g);
        
        if (maxtime > 0) drawCircle(osc[0], osc[1], 20, false, true, g);
        if (time > 0) drawCircle(osc[0], osc[1], (float)(20*(time/maxtime)), true, false, g);
        g.setColor(Color.black);
        
        drawKey('C', Window.getWidth() - 32, Window.getHeight() - 32 + (float)(64 * next_anim), 20, 
                next_text, LEFT, g);
        
        float mult = 176;
        float offset = (float)(mult*quit_anim);
        
        drawKey('X', 32, Window.getHeight() - 32 + offset, 20, "Quit", RIGHT, g);
        int volume = (int)(100 * MiscMath.round(Assets.getSoundSystem().getMasterVolume(), 0.1));
        
        drawKey('Q', 32, Window.getHeight() - 32 - 56 + offset, 20, "", RIGHT, g);
        drawKey('E', 82, Window.getHeight() - 32 - 56 + offset, 20, 
                "Adjust volume ("+(volume)+"%)", RIGHT, g);
        
        drawKey('-', 32, Window.getHeight() - 32 - 112 + offset, 20, "", RIGHT, g);
        drawKey('+', 82, Window.getHeight() - 32 - 112 + offset, 20, 
                "Adjust font size ("+(Assets.getFontSize())+" pts)", RIGHT, g);
        
    }

    @Override
    public boolean onKeyPress(int key, char c) {
        if (c == 'w' && anim <= 0) { Page.getCurrentPage().makeChoice(2); return true; }
        if (c == 'a' && anim <= 0) { Page.getCurrentPage().makeChoice(0); return true; }
        if (c == 's' && anim <= 0) { Page.getCurrentPage().makeChoice(3); return true; }
        if (c == 'd' && anim <= 0) { Page.getCurrentPage().makeChoice(1); return true; }
        if (c == '-' || c == '=' || c == '+') {
            boolean grow = c == '=' || c == '+';
            Assets.addFontSize(grow ? 4 : -4);
            Page.save(true);
        }
        if (c == '\t') {
            Assets.TEXT_BACKGROUND = !Assets.TEXT_BACKGROUND;
            Page.save(true);
        }
        
        //volume controls
        float vol = Assets.getSoundSystem().getMasterVolume();
        float newvol = (float)MiscMath.clamp(c == 'e' ? vol + 0.1f : (c == 'q' ? vol - 0.1f : vol), 0, 1);
        Assets.getSoundSystem().setMasterVolume(newvol);
        
        
        if (key == Input.KEY_F5) Page.clearProgress();
        return false;
    }
    
    private static final int UP = 0, LEFT = 1, DOWN = 2, RIGHT = 3;
    private void drawKey(char key, float x, float y, float radius, String text, int text_mode, Graphics g) {
        g.setAntiAlias(true);
        g.setLineWidth(2); 
        g.setFont(Assets.getFont());
        
        int t_w = Assets.getFont().getWidth(text), t_h = Assets.getFont().getHeight(text);
        float t_x = text_mode == UP || text_mode == DOWN ? x - (t_w/2) : (text_mode == RIGHT ? x + radius + 15 : x - radius - 15 - t_w);
        float t_y = text_mode == LEFT || text_mode == RIGHT ? y - (t_h/2) : (text_mode == DOWN ? y + radius + 10 : y - radius - 10 - t_h);
        
        drawCircle(x, y, radius, false, true, g);
        g.setAntiAlias(false);
        
        g.setColor(Color.black);
        g.drawString(key+"", x - ((float)Assets.getFont().getWidth(key+"")/2) + 1, y - ((float)Assets.getFont().getHeight(key+"")/2) + 1);
        g.drawString(text, t_x+1, t_y+1);
        g.setColor(Color.white);
        g.drawString(key+"", x - ((float)Assets.getFont().getWidth(key+"")/2), y - ((float)Assets.getFont().getHeight(key+"")/2));
        g.drawString(text, t_x, t_y);


    }
    
    private void drawCircle(float x, float y, float radius, boolean fill, boolean shadow, Graphics g) {
        g.setAntiAlias(true);
        g.setColor(Color.black);
        if (shadow) if (!fill) g.drawOval(x-radius+1, y-radius+1, radius*2, radius*2);
            else g.fillOval(x-radius+1, y-radius+1, radius*2, radius*2);
        g.setColor(Color.white);
        if (!fill) g.drawOval(x-radius, y-radius, radius*2, radius*2);
            else g.fillOval(x-radius, y-radius, radius*2, radius*2);
        g.setAntiAlias(false);
    }
    
}
