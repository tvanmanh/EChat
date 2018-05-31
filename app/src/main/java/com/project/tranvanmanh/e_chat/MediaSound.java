package com.project.tranvanmanh.e_chat;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import static android.content.Context.AUDIO_SERVICE;

public  class MediaSound {

    private SoundPool soundPool;

    private AudioManager audioManager;

    // Số luồng âm thanh phát ra tối đa.
    private static final int MAX_STREAMS = 5;

    // Chọn loại luồng âm thanh để phát nhạc.
    private static final int streamType = AudioManager.STREAM_MUSIC;

    private boolean loaded;

    private int soundIdGun;
    private float volume;

    private int streamId;

    public void play(Context context, int sound) {
        // Đối tượng AudioManager sử dụng để điều chỉnh âm lượng.
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        // Chỉ số âm lượng hiện tại của loại luồng nhạc cụ thể (streamType).
        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);


        // Chỉ số âm lượng tối đa của loại luồng nhạc cụ thể (streamType).
        float maxVolumeIndex = (float) audioManager.getStreamMaxVolume(streamType);

        // Âm lượng  (0 --> 1)
        volume = currentVolumeIndex / maxVolumeIndex;


        // Với phiên bản Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // Với phiên bản Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // Sự kiện SoundPool đã tải lên bộ nhớ thành công.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });


        // Tải file nhạc tiếng súng (gun.wav) vào SoundPool.
        this.soundIdGun = this.soundPool.load(context, sound, 1);


        // Khi người dùng nhấn vào button "Gun".
                float leftVolumn = volume;
                float rightVolumn = volume;
                // Phát âm thanh tiếng súng. Trả về ID của luồng mới phát ra.
                streamId = this.soundPool.play(this.soundIdGun, leftVolumn, rightVolumn, 1, 0, 1f);
    }

    private void Stop(){
        soundPool.stop(streamId);
    }
}
