package com.m.apk.extractor.skech.utils.soundPool;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.util.SparseIntArray;
import java.util.HashMap;
import java.util.Map;

public class SoundPoolManager {
    private Map<String, SoundPool> soundPools;
    private Map<String, Integer> soundIds;
    private Map<Integer, String> streamIdToSoundName;
    private SparseIntArray streamStatus;
    private boolean soundEnabled = true;
    private Context context;

    public SoundPoolManager(Context context) {
        this.context = context;
        this.soundPools = new HashMap<>();
        this.soundIds = new HashMap<>();
        this.streamIdToSoundName = new HashMap<>();
        this.streamStatus = new SparseIntArray();
    }

    public void createSoundPool(String poolName, int maxStreams) {
        if (soundPools.containsKey(poolName)) {
            Log.w("SoundPoolManager", "SoundPool '" + poolName + "' já existe!");
            return;
        }

        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            }
        });

        soundPools.put(poolName, soundPool);
    }

    public void loadSound(String poolName, String soundName, int soundResId) {
        if (!soundPools.containsKey(poolName)) {
            Log.e("SoundPoolManager", "SoundPool '" + poolName + "' não encontrado!");
            return;
        }

        try {
            SoundPool soundPool = soundPools.get(poolName);
            int soundId = soundPool.load(context, soundResId, 1);
            soundIds.put(soundName, soundId);
        } catch (Exception e) {
            Log.e("SoundPoolManager", "Erro ao carregar som '" + soundName + "': " + e.getMessage());
        }
    }

    public int playSound(String soundName) {
        return playSound(soundName, 1.0f);
    }

    public int playSound(String soundName, float volume) {
        if (!soundEnabled || !soundIds.containsKey(soundName)) {
            return -1;
        }

        try {
            for (Map.Entry<String, SoundPool> entry : soundPools.entrySet()) {
                SoundPool soundPool = entry.getValue();
                Integer soundId = soundIds.get(soundName);
                
                if (soundId != null && soundId != 0) {
                    int streamId = soundPool.play(soundId, volume, volume, 1, 0, 1.0f);
                    if (streamId > 0) {
                        streamIdToSoundName.put(streamId, soundName);
                        streamStatus.put(streamId, 1);
                    }
                    return streamId;
                }
            }
        } catch (Exception e) {
            Log.e("SoundPoolManager", "Erro ao tocar som '" + soundName + "': " + e.getMessage());
        }
        return -1;
    }

    public boolean isSoundPlaying(String soundName) {
        for (Map.Entry<Integer, String> entry : streamIdToSoundName.entrySet()) {
            if (entry.getValue().equals(soundName) && streamStatus.get(entry.getKey()) == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnySoundPlaying() {
        for (int i = 0; i < streamStatus.size(); i++) {
            if (streamStatus.valueAt(i) == 1) {
                return true;
            }
        }
        return false;
    }

    public void stopSound(int streamId) {
        for (SoundPool soundPool : soundPools.values()) {
            soundPool.stop(streamId);
        }
        streamStatus.put(streamId, 0);
        streamIdToSoundName.remove(streamId);
    }

    public void stopSound(String soundName) {
        for (Map.Entry<Integer, String> entry : streamIdToSoundName.entrySet()) {
            if (entry.getValue().equals(soundName)) {
                stopSound(entry.getKey());
            }
        }
    }

    public void stopAllSounds() {
        for (SoundPool soundPool : soundPools.values()) {
            soundPool.autoPause();
        }
        streamIdToSoundName.clear();
        streamStatus.clear();
    }

    public void pauseSoundPool(String poolName) {
        if (soundPools.containsKey(poolName)) {
            SoundPool soundPool = soundPools.get(poolName);
            if (soundPool != null) {
                soundPool.autoPause();
                updateStreamStatus(soundPool, 0);
            }
        }
    }

    public void resumeSoundPool(String poolName) {
        if (soundPools.containsKey(poolName)) {
            SoundPool soundPool = soundPools.get(poolName);
            if (soundPool != null) {
                soundPool.autoResume();
                updateStreamStatus(soundPool, 1);
            }
        }
    }

    private void updateStreamStatus(SoundPool soundPool, int status) {
        for (Map.Entry<Integer, String> entry : streamIdToSoundName.entrySet()) {
            for (SoundPool sp : soundPools.values()) {
                if (sp == soundPool) {
                    streamStatus.put(entry.getKey(), status);
                }
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void release() {
        for (Map.Entry<String, SoundPool> entry : soundPools.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().release();
            }
        }
        soundPools.clear();
        soundIds.clear();
        streamIdToSoundName.clear();
        streamStatus.clear();
    }

    public void releaseSoundPool(String poolName) {
        if (soundPools.containsKey(poolName)) {
            SoundPool soundPool = soundPools.get(poolName);
            if (soundPool != null) {
                soundPool.release();
            }
            soundPools.remove(poolName);
        }
    }

    public void pauseAll() {
        for (SoundPool soundPool : soundPools.values()) {
            if (soundPool != null) {
                soundPool.autoPause();
            }
        }
        for (int i = 0; i < streamStatus.size(); i++) {
            streamStatus.setValueAt(i, 0);
        }
    }

    public void resumeAll() {
        for (SoundPool soundPool : soundPools.values()) {
            if (soundPool != null) {
                soundPool.autoResume();
            }
        }
        for (int i = 0; i < streamStatus.size(); i++) {
            streamStatus.setValueAt(i, 1);
        }
    }

    public boolean hasSound(String soundName) {
        return soundIds.containsKey(soundName);
    }

    public int getSoundCount() {
        return soundIds.size();
    }

    public int getSoundPoolCount() {
        return soundPools.size();
    }

    public SoundPool getSoundPool(String poolName) {
        return soundPools.get(poolName);
    }

    public Integer getSoundId(String soundName) {
        return soundIds.get(soundName);
    }

    public Map<Integer, String> getPlayingSounds() {
        return new HashMap<>(streamIdToSoundName);
    }

    public int getPlayingSoundsCount() {
        int count = 0;
        for (int i = 0; i < streamStatus.size(); i++) {
            if (streamStatus.valueAt(i) == 1) {
                count++;
            }
        }
        return count;
    }
}
