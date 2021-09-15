package com.example.smartmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class SongList extends AppCompatActivity {
    private String[] items ;
    private ListView songList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        songList = findViewById(R.id.songName);
        ExternalPermissionCheck();


    }

    private void ExternalPermissionCheck(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        DisplaySongName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    public ArrayList<File> ReadAudioSong (File file){
        ArrayList<File> ArrayList = new ArrayList<>();

        try{
            File[] allFiles = file.listFiles();
            for (File individualFile : allFiles){
                if(individualFile.isDirectory() && !individualFile.isHidden()){
                    ArrayList.addAll(ReadAudioSong(individualFile));
                }
                else{
                    if (individualFile.getName().endsWith(".mp3")|| individualFile.getName().endsWith(".wav")|| individualFile.getName().endsWith(".wma")) {
                        ArrayList.add(individualFile);
                    }
                }
            }

        }catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Songlist",e.getMessage());

        }

        return ArrayList;
    }

    private void DisplaySongName(){
        final ArrayList<File> audioSongs = ReadAudioSong(Environment.getExternalStorageDirectory());
        items = new String[audioSongs.size()];

        for ( int songCounter=0; songCounter< audioSongs.size(); songCounter++){

            items[songCounter]= audioSongs.get(songCounter).getName();;

        }
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        songList.setAdapter(itemsAdapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get Name from songList
                String songName =songList.getItemAtPosition(position).toString();
                Intent intent = new Intent(SongList.this,MainActivity.class);
                intent.putExtra("songs", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", position);
                startActivity(intent);
                //finish();
            }
        });

    }
}