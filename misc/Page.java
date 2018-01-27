package misc;

import gui.GUI;
import gui.states.PageScreen;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Page {

    private ArrayList<String> audio, choices, images;
    private HashMap<String, Boolean> audioloops;
    private ArrayList<Integer> links;
    private String text;
    private Object[] nextpage, inputpage;
    private int id, timeout_page;
    private double time;

    private static Page current_page;
    private static HashMap<String, String> variables = new HashMap<String, String>();
    
    private static Page CONFIRMATION_PAGE;
    
    public static ArrayList<String> STORY;

    public Page(int id) {
        this.id = id;
        this.audio = new ArrayList<String>();
        this.choices = new ArrayList<String>();
        this.links = new ArrayList<Integer>();
        this.images = new ArrayList<String>();
        this.text = "";
        this.nextpage = new Object[]{-1, ""};
        this.timeout_page = -1;
        this.inputpage = new Object[]{-1, "", ""};
        this.audioloops = new HashMap<String, Boolean>();
    }
    
    public boolean audioLoops(String audio) { return audioloops.get(audio); }
    
    public String getText() { return text; }
    public int getID() { return id; }
    public void addText(String text) { this.text += text; }

    public boolean hasNextPage() { return (Integer)nextpage[0] >= 0; }
    public void setNextPage(int next, String text) { this.nextpage = new Object[]{next, text}; }
    public String getNextText() { return hasNextPage() ? (String)nextpage[1] : ""; }
    public void next() { 
        if (hasNextPage()) {
            Assets.addChoice(id, 0);
            setCurrentPage((Integer)nextpage[0]);
        } 
    }
    
    public String getInputText() { return (String)inputpage[2]; }
    public int getInputPage() { return (Integer)inputpage[0]; }
    public boolean requiresInput() { return getInputPage() > 0; }
    public void submit(String val) { 
        if (requiresInput()) {
            Assets.addChoice(id, 0);
            setVar((String)inputpage[1], val);
            setCurrentPage((Integer)inputpage[0]);
        } 
    }
    
    public static String getVar(String key) {
        if (variables == null) variables = new HashMap<String, String>();
        String val = variables.get(key);
        System.out.println("Value of key '"+key+"': "+variables.get(key));
        return val == null ? "" : val;
    }
    
    public static void confirmQuit() {
        if (isQuitPage()) return;
        Page current = current_page;
        if (current_page == null) return;
        String text = "Quit the game?\nYou will not lose your progress.";
        CONFIRMATION_PAGE = new Page(-1) {

            @Override
            public void makeChoice(int choice_index) {
                if (choice_index == 1) 
                    setCurrentPage(getLink(choice_index));
                else
                    System.exit(0);
            }
            
        };
        
        CONFIRMATION_PAGE.addChoice(current.getID(), "Yes, save and quit.");
        CONFIRMATION_PAGE.addChoice(current.getID(), "No, keep playing.");
        CONFIRMATION_PAGE.addText(text);
        CONFIRMATION_PAGE.images = current.images;
        setCurrentPage(CONFIRMATION_PAGE);
    }
    
    public static boolean isQuitPage() {
        if (current_page == null) return false;
        return current_page.equals(CONFIRMATION_PAGE);
    }
    
    private static void setCurrentPage(Page p) {
        current_page = p;
        if (!isQuitPage()) Page.save(false); 
        if (PageScreen.getGUI() != null) PageScreen.getGUI().handlePageSwitch();
        if (!isQuitPage()) MiscSound.play(p);
    }
    
    public static void setVar(String key, String val) {
        if (variables == null) variables = new HashMap<String, String>();
        variables.put(key, val);
        System.out.println("Setting variable: "+key+" -> "+val);
    }

    public boolean hasImage(String s) { return images.contains(s); }
    
    public double getTime() { return time; }
    public void timeOut() { 
        if (timeout_page < 0) return;
        Assets.addChoice(id, 0); 
        setCurrentPage(timeout_page); 
    }

    public void addAudio(String s, boolean loop) { 
        audio.add(s); audioloops.put(s, loop); 
        System.out.println("Adding audio "+s+", loops = "+loop);
    }
    public void addImage(String s) { images.add(s); }
    public void addChoice(int i, String s) { 
        choices.add(s); 
        links.add(i); 
        System.out.println("Added choice to page "+id+": "+s+" ["+i+"]");
    }
    public int getLink(int index) { return links.get(index); }
    public ArrayList<String> getChoices() { return choices; }
    
    public void makeChoice(int choice_index) { 
        if (choice_index < 0 || choice_index >= choices.size()) return;
        Assets.addChoice(id, choice_index+1);
        setCurrentPage(getLink(choice_index)); 
        Page.save(false);
    }
    
    public String getRandomImage() { return images.isEmpty() ? "" : images.get(MiscMath.randomInt(0, images.size()-1)); }
    public ArrayList<String> getAudio() { return audio; }
    
    public static Page getCurrentPage() { return current_page; }
    public static void setCurrentPage(int id) { setCurrentPage(getPage(id)); }
    
    /**
     * FUNCTIONS TO LOAD PAGE DATA
     */
    
    public static void loadStory() {
        InputStream in = Assets.class.getResourceAsStream("/resources/story.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        STORY = new ArrayList<String>();
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                STORY.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Page getPage(int page_id) {
        if (page_id < 1) { System.out.println("Page ID "+page_id+" is invalid."); return null; }
        String cond_rgx = "( \\[.*\\])";
        Page p = null;
        
        for (int i = 0; i < STORY.size(); i++) {
            String line = STORY.get(i);

            if (line.matches("page [0-9]*:([ ]*)?")) {
                int id = Integer.parseInt(line.replace("page", "").replace(" ", "").replace(":", ""));
                if (page_id != id) continue;
                p = new Page(id);
            }

            if (p != null) {

                System.out.println("Line "+i+": "+line);
                line = replaceGetters(line);

                if (!checkConditional(line)) { System.out.println("Failed conditional: "+line); continue; }
                if (line.matches("\tset<\\w+>: (.)*")) {
                    String key = line.replaceAll("(\tset<)|(>:.*)", "").trim();
                    String val = line.replaceAll("(\tset<\\w+>: )|"+cond_rgx, "");
                    setVar(key, val);
                }
                if (line.matches("\\ttext: (.)*")) 
                    p.addText(line.replace("\ttext: ", "").replaceAll(cond_rgx, ""));
                if (line.matches("\\taudio(loop)?: (.)*")) 
                    p.addAudio(line.replaceAll("\taudio(loop)?: ", "").replaceAll(cond_rgx, ""), line.trim().indexOf("audioloop:") == 0);
                if (line.matches("\\timage: (.)*")) 
                    p.addImage(line.replace("\timage: ", "").replaceAll(cond_rgx, ""));

                if (line.matches("\\tnext -> [\\d]+: (.)*")) {
                    int p_id = Integer.parseInt(line.split(" ")[2].replace(":", ""));
                    String n_txt = line.replaceAll(cond_rgx+"|(\tnext -> [\\d]+: )", "");
                    p.setNextPage(p_id, n_txt);
                }
                if (line.matches("\\tchoice -> [\\d]+: (.)*")) {
                    int p_id = Integer.parseInt(line.split(" ")[2].replace(":", ""));
                    String c_txt = line.replaceAll(cond_rgx+"|(\tchoice -> [\\d]+: )", "");
                    p.addChoice(p_id, c_txt);
                }
                if (line.matches("\\ttimer -> [\\d]+: \\d+(\\.\\d+)?")) {
                    int p_id = Integer.parseInt(line.split(" ")[2].replace(":", ""));
                    String dur = line.replaceAll(cond_rgx+"|(\ttimer -> [\\d]+: )", "").trim();
                    p.timeout_page = p_id;
                    p.time = Double.parseDouble(dur);
                }
                if (line.matches("\\tinput<\\w+> -> \\d+: .*")) {
                    int p_id = Integer.parseInt(line.split(" ")[2].replace(":", ""));
                    String input_txt = line.replaceAll(cond_rgx+"|(\tinput<\\w+> -> [\\d]+: )", "").trim();
                    String var = line.substring(7, line.indexOf(">"));
                    System.out.println("Input found: "+p_id+", "+var+", "+input_txt);
                    p.inputpage = new Object[]{p_id, var, input_txt};
                }
                if (line.matches("end")) { break; }
            }

        }

        return p;
    }
    
    private static boolean checkConditional(String line) {
        int start = line.indexOf(" [") + 1;
        if (start == 0) return true;
        String substring = line.substring(start);
        String groups[] = substring.split("\\] \\[");
        for (String group : groups) {
            String group_ = group.replaceAll("\\]|\\[", "").trim();
            boolean group_val = false;
            for (String conditional : group_.split(", ")) {
                if (conditional.matches("!?p[0-9]+c[0-9]+")) {
                    if (Assets.choiceMade(conditional)) { group_val = true; break; }
                }
            }
            if (!group_val) return false;
        }
        return true;
    }
    
    /*
     * Recursively replace all "get<???>" instances in the text with the right
     * values in the hashmap. Recursive allows it to support nested setters.
     */
    private static String replaceGetters(String line) {
        String newline = line+"";
        Pattern pattern = Pattern.compile("get<\\w*>");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String get = matcher.group();
            String key = get.substring(4, get.length()-1);
            newline = newline.replace(get, Page.getVar(key));
            return replaceGetters(newline);
        }
        return newline;
    }
    
    public static void save(boolean settings) {
        File f = new File(System.getProperty("user.home")+"/cyoa"+(settings ? "_settings" : "")+".txt");
        FileWriter fw;
        System.out.println("Saving to file " + f.getAbsoluteFile().getAbsolutePath());
        try {
            if (!f.exists()) f.createNewFile();
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            if (!settings) for (String choice: Assets.getChoices()) bw.write("choice: "+choice+"\n");
            if (!settings && Page.variables != null) 
                for (String key: Page.variables.keySet()) bw.write("var: "+key+" = "+getVar(key)+"\n");
            if (settings) bw.write("fsize: "+Assets.getFontSize()+"\n");
            if (settings) bw.write("bg: "+Assets.TEXT_BACKGROUND+"\n");
            if (!settings) bw.write("page: "+current_page.getID()+"\n");
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void clearProgress() {
        System.out.println("Clearing progress...");
        Assets.delete(new File(System.getProperty("user.home")+"/cyoa.txt"));
        Assets.getChoices().clear();
        Page.variables.clear();
        Page.setCurrentPage(1);
    }
    
    public static boolean load(boolean settings) {
        File f = new File(System.getProperty("user.home")+"/cyoa"+(settings ? "_settings" : "")+".txt");
        if (!f.exists()) return false;
        FileReader fr;
        System.out.println("Loading from file: " + f.getAbsoluteFile().getAbsolutePath());
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                if (line.indexOf("page: ") == 0) setCurrentPage(Integer.parseInt(line.replace("page: ", "")));
                if (line.indexOf("choice: ") == 0) Assets.addChoice(line.replace("choice: ", ""));
                if (line.indexOf("fsize: ") == 0) Assets.setFontSize(Integer.parseInt(line.replace("fsize: ", "")));
                if (line.indexOf("bg: ") == 0) Assets.TEXT_BACKGROUND = Boolean.parseBoolean(line.replace("bg: ", ""));
                if (line.indexOf("var: ") == 0) {
                    line = line.replace("var: ", "");
                    String key = line.replaceAll(" = .*", "");
                    String var = line.replaceAll(".* = ", "");
                    Page.setVar(key, var);
                }
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
}
