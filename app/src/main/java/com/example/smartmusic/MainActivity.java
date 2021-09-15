package com.example.smartmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognierIntent;
    private String keeper = "";

    private ImageView pausePlayBtn,next,previous;
    private TextView songTitle;
    private LinearLayout lowerlayout;
    private Boolean mode = false ;

    private MediaPlayer myMediaplayer;
    private int position;
    private ArrayList<File> SongList;
    private String mSongName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.Recod);
        pausePlayBtn = findViewById(R.id.pausePlayBtn);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        songTitle = findViewById(R.id.songTitle);
        lowerlayout = findViewById(R.id.lower);
        VoicePermissions();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognierIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognierIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognierIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        valiateReciveValuesAndPlay();
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matchesFound = bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                if(matchesFound != null){
                    keeper = matchesFound.get(0);
                    if(keeper.contains("stop song")|| keeper.contains("pause")){
                        playPauseSong();
                        Toast.makeText(MainActivity.this, keeper, Toast.LENGTH_SHORT).show();
                    }else if (keeper.equals("start the song") || keeper.equals("start song")|| keeper.equals("play")){
                        playPauseSong();
                        Toast.makeText(MainActivity.this, keeper, Toast.LENGTH_SHORT).show();
                    }else if (keeper.equals("next") ||keeper.equals("next Song") ){
                        nextSong();
                    }else if (keeper.equals("previous") ||keeper.equals("last song")){
                        previousSong();
                    } else {
                        Toast.makeText(MainActivity.this, keeper, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSong();
            }
        });


            btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode){
                    btn.setText("Enable Voice Command");
                    lowerlayout.setVisibility(View.VISIBLE);
                    speechRecognizer.stopListening();
                }else{
                    btn.setText("Disable Voice Command");
                    lowerlayout.setVisibility(View.GONE);
                    speechRecognizer.startListening(speechRecognierIntent);
                    keeper = "";
                }
                mode = !mode;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myMediaplayer.getCurrentPosition()>0){
                    previousSong();
                }
            }
        });
    }


    private void valiateReciveValuesAndPlay(){
        if(myMediaplayer != null && myMediaplayer.isPlaying()){
            Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
            myMediaplayer.pause();
            myMediaplayer.stop();
            myMediaplayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        SongList =(ArrayList)bundle.getParcelableArrayList("songs");
        mSongName = SongList.get(position).getName();
        String SongName = bundle.getString("name");

        songTitle.setText(SongName);
        songTitle.setSelected(true);
        position = bundle.getInt("position");
        Uri uri = Uri.parse(SongList.get(position).toString());
        myMediaplayer= MediaPlayer.create(MainActivity.this,uri);
        //file:///storage/emulated/0/Android/media/com.Slack/Notifications/Slack - Ding.mp3
        myMediaplayer.start();
    }

    private void VoicePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playPauseSong(){
        if(myMediaplayer.isPlaying()){
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaplayer.pause();
        }else {
            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaplayer.start();
        }
    }

    private void nextSong(){
        myMediaplayer.pause();
        myMediaplayer.stop();
        myMediaplayer.release();
        position = ((position+1)%SongList.size());
        Uri uri = Uri.parse(SongList.get(position).toString());
        myMediaplayer=MediaPlayer.create(MainActivity.this,uri);
        mSongName =SongList.get(position).toString();
        mSongName=mSongName.substring(mSongName.length() - 20);
        songTitle.setText(mSongName);
        playPauseSong();
        myMediaplayer.start();


    }

    private  void  previousSong(){
        myMediaplayer.pause();
        myMediaplayer.stop();
        myMediaplayer.release();
        position= ((position-1)<0 ? (SongList.size()-1) : (position-1));
        Uri uri = Uri.parse(SongList.get(position).toString());
        myMediaplayer=MediaPlayer.create(MainActivity.this,uri);
        mSongName =SongList.get(position).toString();
        mSongName=mSongName.substring(mSongName.length() - 20);
        songTitle.setText(mSongName);
        playPauseSong();
        myMediaplayer.start();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myMediaplayer.pause();
        myMediaplayer.stop();
        myMediaplayer.release();
    }

}