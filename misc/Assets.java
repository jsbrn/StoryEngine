package misc;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class Assets {
    
    private static HashMap images;
    private static TrueTypeFont font;
    private static SoundSystem sound_system;
    
    private static ArrayList<String> choices = new ArrayList<String>();

    public static final int PAGE_SCREEN = 1, LOADING_SCREEN = 2;
    
    private static int font_size = 24;
    public static boolean TEXT_BACKGROUND = true;
    
    /**
     * Loads all assets from the project's assets folder. Clears all previously loaded assets
     * first. Should be called on every new project load.
     */
    public static void init() {
        if (images == null) images = new HashMap();
        images.clear();
        initializeSoundSystem();
        Page.loadStory();
    }
    
    public static void addChoice(String choice) {
        String split[] = choice.replace("p", " ").replace("c", " ").trim().split(" ");
        addChoice(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
    
    public static void addChoice(int page_id, int choice_id) {
        choices.add("p"+page_id+"c"+choice_id);
    }
    
    public static boolean choiceMade(String choice) {
        boolean not = choice.contains("!");
        String split[] = choice.replace("!", "")
                    .replace("p", " ").replace("c", " ").trim().split(" ");
        return !not == choiceMade(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
    
    public static boolean choiceMade(int page_id, int choice_id) {
        if (page_id == 100 && choice_id == 100) return true; //test
        return choices.contains("p"+page_id+"c"+choice_id);
    }

    public static ArrayList<String> getChoices() {
        return choices;
    }
    
    public static int getFontSize() { return font_size; }
    public static void setFontSize(int fsize) {
        font_size = fsize;
        font_size = (int)MiscMath.clamp(font_size, 20, 32);
        font = null;
    }

    public static void initializeSoundSystem() {
        try {
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.addLibrary(LibraryJavaSound.class);
            SoundSystemConfig.setSoundFilesPackage("resources/audio/");
            SoundSystemConfig.setDefaultFadeDistance(800);
            sound_system = new SoundSystem();
        } catch (SoundSystemException ex) {
            Logger.getLogger(Assets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static SoundSystem getSoundSystem() { return sound_system; }

    public static TrueTypeFont getFont() {
        if (font == null) font = new TrueTypeFont(new Font("MS Sans Serif", Font.PLAIN, font_size), true);
        return font;
    }
    
    public static void addFontSize(int amount) {
        setFontSize(font_size+amount);
    }

    
    public static void delete(File asset_or_folder) {
        if (asset_or_folder.isDirectory()) {
            File[] list = asset_or_folder.listFiles();
            for (File f: list) {
                if (f.isDirectory()) delete(f); else f.delete();
            }
        }
        asset_or_folder.delete();
    }
    
    public static int count() { return images.size(); }
    
    public static Object get(String key) {
        key = key.replaceAll("[/\\\\]", File.separator);
        if (!images.containsKey(key)) return null;
        return images.get(key);
    }
    
    public static void put(String key, Image asset) {
        images.put(key, asset);
    }
    
    public static Image image(String asset) { return image(asset, Image.FILTER_LINEAR); }
    
    public static Image image(String asset, int filter) {
        if (asset == null) return null;
        if (asset.length() == 0) return null;
        if (images.containsKey(asset)) return (Image)images.get(asset);
        Image img = null;
        try {
            img = new Image("resources/images/"+asset, false, filter);
            images.put(asset, img);
            System.out.println("Loaded "+asset+" with filter "+filter);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        System.out.print(img == null ? asset+" is null!\n" : "");
        return img;
    }

}
