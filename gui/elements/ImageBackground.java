package gui.elements;

import gui.GUI;
import misc.Assets;
import misc.MiscMath;
import misc.Page;
import misc.Window;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ImageBackground extends GUIElement {

    private float[] target, coords;
    private int img_timer, pan_timer, zoom_timer, fade_timer;
    private float angle, length[], alpha[], zoom[];
    private String image;
    
    private static float MAX_LENGTH = 25, 
            MIN_ZOOM = 1.2f, MAX_ZOOM = 1.6f,
            MIN_ALPHA = 0f, MAX_ALPHA = 0.65f;

    public ImageBackground() {
        this.target = new float[2];
        this.coords = new float[2];
        this.image = "";
        this.img_timer = 0;
        this.alpha = new float[]{0.5f, 0};
        this.zoom = new float[]{MIN_ZOOM, MAX_ZOOM};
        this.length = new float[]{0, MAX_LENGTH};
    }
    
    @Override
    public void update() {
        image();
        pan();
        fade();
        zoom();
    }
    
    private void fade() {
        fade_timer -= MiscMath.getConstant(1000, 1);
        if (fade_timer < 0) {
            fade_timer = (int)MiscMath.randomInt(1000, 8000);
            alpha[1] = alpha[1] == MAX_ALPHA ? MIN_ALPHA: MAX_ALPHA;
        }
        alpha[0] += MiscMath.getConstant(alpha[1] - alpha[0] >= 0 && !Page.getCurrentPage().getChoices().isEmpty() ? 0.1 : -0.07, 1); 
        alpha[0] = (float)MiscMath.clamp(alpha[0], MIN_ALPHA, MAX_ALPHA);
    }
    
    private void zoom() {
        zoom_timer -= MiscMath.getConstant(1000, 1);
        if (zoom_timer < 0) {
            zoom_timer = (int)MiscMath.randomInt(5000, 10000);
            zoom[1] = zoom[1] == MAX_ZOOM ? MIN_ZOOM: MAX_ZOOM;
        }
        zoom[0] += MiscMath.getConstant(zoom[1] - zoom[0] >= 0 ? 0.007 : -0.007, 1); zoom[0] = (float)MiscMath.clamp(zoom[0], MIN_ZOOM, MAX_ZOOM);
    }
    
    private void image() {
        img_timer -= MiscMath.getConstant(Page.getCurrentPage().getChoices().isEmpty() ? 250 : 1000, 1);
        if (img_timer <= 0) {
            img_timer = MiscMath.randomInt(8000, 30000);
            image = Page.getCurrentPage().getRandomImage();
        }
    }
    
    private void pan() {
        pan_timer -= MiscMath.getConstant(1000, 1);
        if (pan_timer < 0 || MiscMath.distance(coords[0], coords[1], target[0], target[1]) < 1) { 
            pan_timer = MiscMath.randomInt(1000, 3000);
            angle += MiscMath.randomInt(10, 25);
            length[1] = length[1] == MAX_LENGTH ? 50 : MAX_LENGTH;
            int[] offset = MiscMath.getRotatedOffset(0, -(int)length[0], angle);
            target[0] = (float)offset[0]; target[1] = (float)offset[1];
        }
        length[0] += MiscMath.getConstant(length[1] - length[0] >= 0 ? 25 : -25, 1); length[0] = (float)MiscMath.clamp(length[0], 0, MAX_LENGTH);
        double[] vel = MiscMath.calculateVelocity((target[0] - coords[0]), (target[1] - coords[1]));
        double div = Page.getCurrentPage().getChoices().isEmpty() ? 18 : 10;
        coords[0] += vel[0] / div;
        coords[1] += vel[1] / div;
    }

    @Override
    public boolean onPageSwitch() {
        if (image.length() == 0) image = Page.getCurrentPage().getRandomImage();
        img_timer = 150;
        return true;
    }
    
    @Override
    public void draw(Graphics g) {
        Image img = Assets.image(image, Image.FILTER_LINEAR);
        if (img == null) return;
        Image scimg = img.getScaledCopy((int)(Window.getDisplayWidth()*zoom[0]), 
                (int)(Window.getDisplayHeight()*zoom[0]));
        float[] osc = osc();
        scimg.setAlpha(1);
        float[] coords1 = new float[]{osc[0] - ((float)scimg.getWidth()/2) - coords[0], osc[1] - ((float)scimg.getHeight()/2) - coords[1]},
                coords2 = new float[]{osc[0] - ((float)scimg.getWidth()/2) + (coords[0]/2), osc[1] - ((float)scimg.getHeight()/2) + (coords[1]/2)};
        scimg.draw(coords1[0], coords1[1]);
        scimg.setAlpha(alpha[0]);
        scimg.draw(coords2[0], coords2[1]);
        if (GUI.DEBUG_MODE) g.drawString((int)angle+", "+(int)length[0]+", ["+target[0]+", "+target[1]+"], ["+coords[0]+", "+coords[1]+"]", osc[0], osc[1]);
    }
    
}
