package gui.elements;

import gui.GUI;
import misc.Assets;
import misc.MiscMath;
import misc.Page;
import misc.Window;
import com.github.mathiewz.slick.Color;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.Input;

public class TextBox extends GUIElement {

    private String text;
    private String input_text;
    private int max, input_page;
    private float anim;

    public TextBox() {
        this.text = "";
        this.input_text = "";
        this.max = 32;
        this.anim = 1;
    }

    @Override
    public void update() {
        boolean equal = input_text.equals(Page.getCurrentPage().getInputText()) 
                && input_page == Page.getCurrentPage().getInputPage();
        anim += MiscMath.getConstant(!equal || !Page.getCurrentPage().requiresInput() ? 2 : -2, 1);
        anim = (float)MiscMath.clamp(anim, 0, 1);
        if (anim >= 1) {
            input_text = Page.getCurrentPage().getInputText();
            input_page = Page.getCurrentPage().getInputPage();
            text = "";
        }
    }

    private void addText(String s) {
        String newtext = text+s;
        if (newtext.length() > max) return;
        text = newtext;
    }
    
    @Override
    public float[] osc() {
        float[] osc = super.osc();
        osc[1] += (anim)*(double)((Window.getHeight() + 120)-osc[1]);
        return osc;
    }

    @Override
    public int[] dims() {
        return new int[]{(int)MiscMath.clamp(Window.getWidth()/2, 600, 1200),
            Assets.getFont().getHeight() + 20};
    }
    
    private void backspace() {
        if (text.length() == 0) return;
        text = text.substring(0, text.length()-1);
    }

    @Override
    public boolean onKeyRelease(int key, char c) {
        if (key == Input.KEY_ENTER && text.trim().length() > 0 && anim <= 0) {
            System.out.println("Submitting '"+text+"'");
            Page.getCurrentPage().submit(text.trim());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onKeyPress(int key, char c) {
        if (anim > 0) return false;
        if (key == Input.KEY_BACK) {
            backspace();
        } else if ((c+"").matches("[A-Z]|[a-z]|[0-9]|[ \\\\@#$-/:-?{-~!\"^_`\\[\\]]")) { //any visible character
            addText(c+"");
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics g) {
        float[] osc = osc();
        int[] dims = dims();
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(osc[0], osc[1], dims[0], dims[1]);
        g.setColor(text.length() == 0 ? Color.darkGray : Color.white);
        g.setFont(Assets.getFont());
        g.drawString(text.length() == 0 ? input_text : text, 
                osc[0] + 10, osc[1] + (dims[1]/2) - (Assets.getFont().getHeight())/2);
        if (System.currentTimeMillis() % 1000 < 500) {
            g.setColor(Color.white);
            int h = Assets.getFont().getHeight();
            g.drawRect(osc[0] + 10 + (Assets.getFont().getWidth(text)), osc[1] + (h/2) - 2, 1, dims[1] - h + 4);
        }
        
        String hint = text.length() > 0 ? "(press ENTER to submit)" : "";
        g.setColor(Color.black);
        g.drawString(hint, osc[0] + dims[0] - Assets.getFont().getWidth(hint) - 5, osc[1] + dims[1] + 11);
        g.setColor(Color.white);
        g.drawString(hint, osc[0] + dims[0] - Assets.getFont().getWidth(hint) - 5, osc[1] + dims[1] + 10);
        if (GUI.DEBUG_MODE) g.drawString(input_page+", "+input_text, osc[0], osc[1] - Assets.getFont().getHeight());
    }
    
}
