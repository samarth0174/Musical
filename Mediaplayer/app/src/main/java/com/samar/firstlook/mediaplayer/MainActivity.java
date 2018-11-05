package com.samar.firstlook.mediaplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button playbutton;
    private Button nextbutton;
    int position;
    private Button prevbutton;
    private SeekBar seekBar;
    ArrayList<File> myMusic;
    static MediaPlayer mediaplayer;
    private ImageView artist;
    private TextView lefttime;
    private TextView righttime;
    private Bundle extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup_UI();
        if(mediaplayer != null){
            mediaplayer.stop();
            mediaplayer.release();
        }
        extra=getIntent().getExtras();
        if(extra!=null) {
            myMusic = (ArrayList) extra.getParcelableArrayList("songs");
            position = extra.getInt("pos", 0);
        }
            Uri u = Uri.parse(myMusic.get(position).toString());
            mediaplayer = MediaPlayer.create(getApplicationContext(), u);
            mediaplayer.start();
        playbutton.setBackgroundResource(android.R.drawable.ic_media_pause);

        righttime.setText(String.valueOf(mediaplayer.getDuration() / 1000) + ".00");
        seekBar.setMax(mediaplayer.getDuration());




        //setting up everything by calling the below fn




        //seekbar working



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaplayer.seekTo(progress);
                }
                if(seekBar.getProgress()==seekBar.getMax())
                {
                    playbutton.setBackgroundResource(android.R.drawable.ic_media_pause);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress()==seekBar.getMax())
                {
                    playbutton.setBackgroundResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        updatethread();

    }

    public void setup_UI() {




        artist = (ImageView) findViewById(R.id.artistimageID);
        lefttime = (TextView) findViewById(R.id.timestart);
        righttime = (TextView) findViewById(R.id.timestop);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        prevbutton = (Button) findViewById(R.id.prevbutton);
        playbutton = (Button) findViewById(R.id.playbutton);
        nextbutton = (Button) findViewById(R.id.nextbutton);

        prevbutton.setOnClickListener(this);
        playbutton.setOnClickListener(this);
        nextbutton.setOnClickListener(this);
    }


    //music playerfns
    public void pause_music() {
        if (mediaplayer != null) {
            mediaplayer.pause();
            playbutton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void start_music() {
        if (mediaplayer != null) {
            mediaplayer.start();
            updatethread();
            playbutton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    public void prev_music()
    {
        mediaplayer.stop();
        //mediaplayer.seekTo(0);
        mediaplayer.release();
        position=((position-1)<0)?(myMusic.size()-1):(position-1);
        Uri u = Uri.parse(myMusic.get( position).toString());//%mysongs so that it do not go to invalid position
        mediaplayer = MediaPlayer.create(getApplicationContext(),u);
        mediaplayer.start();
       // righttime.setText(String.valueOf(mediaplayer.getDuration() / 1000) );
        seekBar.setMax(mediaplayer.getDuration());
    }

    public void next_music()
    {
        mediaplayer.stop();
      //  mediaplayer.seekTo(0);
        mediaplayer.release();
        position=((position+1)%myMusic.size());
        Uri u = Uri.parse(myMusic.get( position).toString());
        mediaplayer = MediaPlayer.create(getApplicationContext(),u);
        mediaplayer.start();
       // righttime.setText(String.valueOf(mediaplayer.getDuration() / 1000) );
        seekBar.setMax(mediaplayer.getDuration());
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mediaplayer != null && mediaplayer.isPlaying()) {
//            mediaplayer.stop();
//            mediaplayer.release();
//            mediaplayer = null;
//        }
//    }

    //onclick listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevbutton: {

                prev_music();
            }
            break;

            case R.id.playbutton:
                if (mediaplayer.isPlaying()) {
                   pause_music();

                } else {
                    start_music();
                }

                break;

            case R.id.nextbutton:
            {
               next_music();
            }
                break;
        }
    }

    //Thread update seekbar and time label
    // Thread (Update positionBar & timeLabel)
    public void updatethread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaplayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mediaplayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            seekBar.setProgress(currentPosition);

            // Update Labels.

            String elapsedTime = createTimeLabel(currentPosition);
            lefttime.setText(elapsedTime);

            String remainingTime = createTimeLabel(mediaplayer.getDuration() - currentPosition);
            righttime.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }
}


