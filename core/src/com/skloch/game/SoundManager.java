package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class SoundManager implements Disposable {
    public Music overworldMusic, menuMusic;
    private Sound footstep1, footstep2;
    public boolean footstepBool;
    private float footstepTimer;
    private float sfxVolume = 0.8f, musicVolume = 0.8f;
    private Sound pauseSound, dialogueOpenSound, dialogueOptionSound, buttonSound;
    public SoundManager () {
        // Load music
        overworldMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/OverworldMusic.mp3"));
        overworldMusic.setLooping(true);
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/Streetlights.ogg"));
        menuMusic.setLooping(true);

        // Load SFX
        footstep1 = Gdx.audio.newSound(Gdx.files.internal("Sounds/footstep1 grass.ogg"));
        footstep2 = Gdx.audio.newSound(Gdx.files.internal("Sounds/footstep2 grass.ogg"));

        pauseSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Pause01.wav"));
        dialogueOpenSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/DialogueOpen.wav"));
        dialogueOptionSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/DialogueOption.wav"));
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Button.wav"));
    }

    public void setMusicVolume (float vol) {
        this.musicVolume = vol;
        overworldMusic.setVolume(musicVolume);
        menuMusic.setVolume(musicVolume);
    }

    public void setSfxVolume (float vol) {
        this.sfxVolume = vol;
    }

    public void playPause() {pauseSound.play(sfxVolume);}
    public void playDialogueOpen() {dialogueOpenSound.play(sfxVolume);}
    public void playDialogueOption() {dialogueOptionSound.play(sfxVolume);}
    public void playButton() {buttonSound.play(sfxVolume);}

    public void playOverworldMusic() {overworldMusic.play();}
    public void playMenuMusic() {menuMusic.play();}
    public void stopMenuMusic() {menuMusic.stop();}
    public void stopOverworldMusic() {overworldMusic.stop();}

    public void pauseOverworldMusic() {overworldMusic.pause();}

    public float getMusicVolume() {return musicVolume;}
    public float getSfxVolume() {return sfxVolume;}

    public void processTimers(float delta) {
        // Decrements timers for any recurring sounds, like footsteps
        // Events that make these sounds can then check that their specific timer is zero and play a noise
        footstepTimer -= delta;
        if (footstepTimer < 0) {
            footstepTimer = 0;
        }
    }

    public void playFootstep() {
        // If it is time to play a footstep, play one
        if (footstepTimer <= 0) {
            footstepTimer = 0.5f; // Delay plus avg length of footstep
            if (footstepBool == false) {
                footstep1.play(sfxVolume);
                footstepBool = true;
            } else {
                footstep2.play(sfxVolume);
                footstepBool = false;
            }
        }
    }


    @Override
    public void dispose () {
        overworldMusic.dispose();
        menuMusic.dispose();
        footstep1.dispose();
        footstep2.dispose();
        pauseSound.dispose();
        dialogueOpenSound.dispose();
        dialogueOptionSound.dispose();
    }
}
