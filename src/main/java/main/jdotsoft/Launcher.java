package main.jdotsoft;

public class Launcher {
    public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("main.SlickInitializer", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
