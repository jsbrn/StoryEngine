package misc;

import java.io.File;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageOut;

public class Window {

    //create a window object
    public static AppGameContainer WINDOW_INSTANCE;

    public static void setFullscreen() {
        try {
            if (WINDOW_INSTANCE.isFullscreen() == false) {
                WINDOW_INSTANCE
                        .setDisplayMode(Display.getDesktopDisplayMode().getWidth(),
                                Display.getDesktopDisplayMode().getHeight(), true);
                WINDOW_INSTANCE.setMouseGrabbed(true);
                
            }
        } catch (SlickException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isFullScreen() {
        return WINDOW_INSTANCE.isFullscreen();
    }

    public static void takeScreenshot(Graphics g) {
        try {
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            Image scrn = new Image(Window.getWidth(), Window.getHeight());
            String file_url = System.getProperty("user.home") + "/secret/screenshots/"
                    + year + "" + month + "" + day;
            g.copyArea(scrn, 0, 0);
            //make screenshots folder
            if (new File(System.getProperty("user.home") + "/secret/screenshots/").exists() == false) {
                new File(System.getProperty("user.home") + "/secret/screenshots/").mkdir();
            }
            //check if image_exists already
            if (new File(file_url + ".png").exists()) {
                int count = 2;
                while (true) {
                    if (new File(file_url + "_" + count + ".png").exists() == false) {
                        file_url += "_" + count;
                        break;
                    }
                    count++;
                }
            }
            ImageOut.write(scrn, file_url + ".png");
            System.out.println("Saved screenshot to " + file_url + ".png");
        } catch (SlickException ex) {

        }
    }

    public static int getFPS() {
        return WINDOW_INSTANCE.getFPS();
    }

    public static int getDisplayWidth() {
        return WINDOW_INSTANCE.getScreenWidth();
    }

    public static int getDisplayHeight() {
        return WINDOW_INSTANCE.getScreenHeight();
    }

    public static float getY() {
        return (float) Display.getY();
    }

    public static float getX() {
        return (float) Display.getX();
    }

    public static int getWidth() {
        return WINDOW_INSTANCE.getWidth();
    }

    public static int getHeight() {
        return WINDOW_INSTANCE.getHeight();
    }

    public static void setResizable(boolean resizable) {
        WINDOW_INSTANCE.setResizable(resizable);
    }

    public static void setMouseGrabbed(boolean grabbed) {
        WINDOW_INSTANCE.setMouseGrabbed(grabbed);
    }

}
