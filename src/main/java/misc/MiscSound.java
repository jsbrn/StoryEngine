package misc;

public class MiscSound {
    
    private static Page playing;
    
    public static void play(Page p) {
        if (!p.equals(playing)) {
            if (playing != null) {
                for (String clip: playing.getAudio()) { //fade out any clips that are not playing
                    boolean keep = p.getAudio().contains(clip);
                    System.out.println("Old clip: "+clip);
                    if (!keep) Assets.getSoundSystem().fadeOut(clip, null, 6000);
                        else Assets.getSoundSystem().setLooping(clip, playing.audioLoops(clip));
                }
            }
            for (String clip: p.getAudio()) playAudio(clip, p.audioLoops(clip));
        }
        playing = p;
    }
    
    private static void playAudio(String file, boolean loop) {
        System.out.println("New clip: "+file);
        if (!Assets.getSoundSystem().playing(file)) {
            Assets.getSoundSystem().backgroundMusic(file, file, false);
            //Assets.getSoundSystem().setVolume(file, 1f);
        } else {
            Assets.getSoundSystem().queueSound(file, file);
            Assets.getSoundSystem().setLooping(file, loop);
        }
        
    }
    
}
