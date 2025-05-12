package edu.emmapi.controllers.components;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicPlayer {
    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private boolean hidden;
    private int current = 0;
    private List<File> songs;

    public MusicPlayer(){
        songs = getMusic("src/main/resources/audio/music");
        if (!songs.isEmpty()){
            playSong(current);
        }
        mediaPlayer.pause();
    }

    public void playSong(int index){
        if(index >= 0 && index < songs.size()){
            if (mediaPlayer != null){
                mediaPlayer.stop();
            }
            File songFile = songs.get(index);
            Media media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            current = index;
        }
    }

    public void nextSong() {
        int nextIndex = (current + 1) % songs.size();
        playSong(nextIndex);
    }

    public void previousSong() {
        int prevIndex = (current - 1 + songs.size()) % songs.size();
        playSong(prevIndex);
    }

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying(){
        return mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void toggleHidden(){
        hidden = !hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public static List<File> getMusic(String dossier){
        File folder = new File(dossier);
        List<File> songs = new ArrayList<>();

        if(folder.exists() && folder.isDirectory()){
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".wav"));

            if(files != null){
                songs.addAll(Arrays.asList(files));
            }
        }
        else {
            System.out.println("dossier invalide");
        }
        return songs;
    }
}
