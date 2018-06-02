package com.project.tranvanmanh.e_chat;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import static android.content.Context.AUDIO_SERVICE;

public class MediaSound{

    private static MediaSound instance;
    private static MediaPlayer mediaPlayer;

    private MediaSound(){}
    public static synchronized MediaSound getInstance(){

        if(instance == null){
            instance = new MediaSound();
        }

        return instance;
    }

    public static void Play( Context context){
       mediaPlayer = MediaPlayer.create(context,R.raw.spring);
       mediaPlayer.start();
    }

    public static void Stop(){
        mediaPlayer.stop();
    }

}
