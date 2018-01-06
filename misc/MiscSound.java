package misc;

import paulscode.sound.SoundSystemConfig;

public class MiscSound {
    
    public static void playSound(String file) {
        float base_volume = .75f;
        String sound_id = Assets.getSoundSystem().quickPlay(false, 
                file, false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
        Assets.getSoundSystem().setVolume(sound_id, 1f);
    }
    
}
