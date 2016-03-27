package com.example.samanthasoto.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.harman.pulsesdk.DeviceModel;
import com.harman.pulsesdk.ImplementPulseHandler;
import com.harman.pulsesdk.PulseColor;
import com.harman.pulsesdk.PulseHandlerInterface;
import com.harman.pulsesdk.PulseNotifiedListener;
import com.harman.pulsesdk.PulseThemePattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements PulseNotifiedListener {

    Scene mScene;
    Scene m2Scene;
    Scene m3Scene;
    Scene m4Scene;


    FrameLayout framelayout;

    MainActivity pulseDemo;
    private boolean mBroadcast = false;
    private TextView mColorText;
    int counter = 0;

    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";

    // Actual power hour constants...
    public static final int SONG_INTERVAL = 60000;
    public static final int POWER_HOUR_INTERVAL = 3660000;

    // Testing constants
    //public static final int SONG_INTERVAL = 10000;
    //public static final int POWER_HOUR_INTERVAL = 51000;

    static String Tag = "MainActivity";
    private ArrayList<Fragment> fragments;
    ArrayList<Map<String, Object>> adaptParam;
    static int mWidth, mHeight, statusBarHeight,realHeight, mDensityInt;
    static float scale, mDensity;
    boolean isActive;
    static int FRAG_COLOR_ID = 0;
    Timer mTimer=null;
    boolean isConnectBT;
    int navigationBarHeight = 0;
    public ImplementPulseHandler pulseHandler = new ImplementPulseHandler();

    public int intervalRemaining = POWER_HOUR_INTERVAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        framelayout = (FrameLayout) findViewById(R.id.scene_root);

        mScene = Scene.getSceneForLayout(framelayout, R.layout.scene_party_main, this);
        //m2Scene = Scene.getSceneForLayout(framelayout, R.layout.scene_partyplay_main, this);
        //m3Scene = Scene.getSceneForLayout(framelayout, R.layout.scene_partyhow_main, this);
        //m4Scene = Scene.getSceneForLayout(framelayout, R.layout.scene_partypaused_main, this);

        pulseHandler.ConnectMasterDevice(this);
        pulseHandler.registerPulseNotifiedListener(this);
        isActive = true;
        setTimer();

        TransitionManager.go(mScene);

        Button button_party = (Button) findViewById(R.id.button_party);
        assert button_party != null;
        button_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountDownTimer countDownText = new CountDownTimer(3600000, 1000) {
                    public int seconds = 0;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Set timer text
                        TextView text = (TextView) findViewById(R.id.countdown);
                        int hours = (int) millisUntilFinished / 1000 / 60;
                        int minutes = (int) millisUntilFinished / 1000 % 60;
                        SpannableStringBuilder sb = new SpannableStringBuilder(hours + ":" + minutes);
                        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
                        sb.setSpan(b, 0, Integer.toString(hours).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        text.setText(sb);

                        // Set ring
                        seconds++;
                        if (seconds > 60) {
                            seconds = 0;
                        }
                        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                        pb.setProgress(seconds);
                    }

                    @Override
                    public void onFinish() {
                        TextView text = (TextView) findViewById(R.id.countdown);
                        text.setText("DONE!");
                    }
                };
                countDownText.start();

                // Must do +1 songs because finishing tick
                CountDownTimer countDown = new CountDownTimer(POWER_HOUR_INTERVAL, SONG_INTERVAL) {

                    public void onTick(long millisUntilFinished) {
                        intervalRemaining = intervalRemaining - SONG_INTERVAL;

                        // Change audio stateintervalRemaining
                        Context context = getApplicationContext();
                        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                        if (mAudioManager.isMusicActive()) {
                            Intent i = new Intent(SERVICECMD);
                            i.putExtra(CMDNAME, CMDNEXT);
                            sendBroadcast(i);
                        } else {
                            Intent i = new Intent(SERVICECMD);
                            i.putExtra(CMDNAME, CMDTOGGLEPAUSE);
                            sendBroadcast(i);
                        }

                        // Generate new background color
                        Random rand = new Random();
                        int red = rand.nextInt(175);
                        int green = rand.nextInt(175);
                        int blue = rand.nextInt(175);

                        // Create new pulse color from background color
                        final PulseColor backgroundColor = new PulseColor();
                        backgroundColor.red = (byte) (red);
                        backgroundColor.green = (byte) (green);
                        backgroundColor.blue = (byte) (blue);

                        // Set background color
                        int javaColor = Color.argb(255, red, green, blue);
                        //setBackgroundColor(javaColor);

                        // Set title bar color - MIGHT NOT WORK :(
                        //((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(javaColor));

                        // Create new pulse color from background color
                        PulseColor blankBackground = new PulseColor();
                        blankBackground.red = (byte) (255);
                        blankBackground.green = (byte) (255);
                        blankBackground.blue = (byte) (255);

                        final PulseColor[] bitmap = new PulseColor[99];
                        Arrays.fill(bitmap, blankBackground);

                        new CountDownTimer(SONG_INTERVAL, (SONG_INTERVAL / 99)) {

                            int i = 98;

                            public void onTick(long millisUntilFinished) {
                                if (i >= 0) {
                                    bitmap[i] = backgroundColor;
                                    i--;
                                    pulseHandler.SetColorImage(bitmap);
                                }
                            }

                            public void onFinish() {
                                // Do nothing? ¯\_(ツ)_/¯
                            }
                        }.start();

                        pulseHandler.SetColorImage(bitmap);
                    }

                    public void onFinish() {
                        Context context = getApplicationContext();
                        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                        if (mAudioManager.isMusicActive()) {
                            Intent i = new Intent(SERVICECMD);
                            i.putExtra(CMDNAME, CMDSTOP);
                            sendBroadcast(i);
                        }

                        PulseColor backgroundColor = new PulseColor();
                        backgroundColor.red = (byte) (0);
                        backgroundColor.green = (byte) (0);
                        backgroundColor.blue = (byte) (0);

                        pulseHandler.SetBackgroundColor(backgroundColor, false);

                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                        r.play();
                    }
                };

                countDown.start();


                //TransitionManager.go(m2Scene, new Fade());

            }
        });

        //Button button_go = (Button) findViewById(R.id.button_party);
        //button_go.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
//
        //        //speakers stuff from Andy - play music
//
        //        TransitionManager.go(m2Scene, new Fade());
//
        //    }
        //});
//
        //Button button_pause = (Button) findViewById(R.id.button_pause);
        //button_go.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
//
        //        //speakers stuff from Andy - stop music
//
        //        TransitionManager.go(m4Scene, new Fade());
//
        //    }
        //});
//
        //ImageView button_restart = (ImageView) findViewById(R.id.button_restart);
        //button_restart.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
//
        //        //speakers stuff from Andy - play music
//
        //        TransitionManager.go(m2Scene, new Fade());
//
        //    }
        //});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Shit from Pulse
    public int pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void pick_color(View v){
        pulseHandler.CaptureColorFromColorPicker();
    }

    public void record_sound(View v){
        pulseHandler.GetMicrophoneSoundLevel();
    }


    public void setTimer()
    {
        if(mTimer!=null)
            return;

        mTimer=new Timer();
        TimerTask task=new TimerTask()
        {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (isActive) {
                            pulseHandler.ConnectMasterDevice(MainActivity.this);
                        }
                    }
                });
            }
        };
        mTimer.schedule(task, 1000, 1500);
    }

    private void cancelTimer()
    {
        if(mTimer!=null)
        {
            mTimer.cancel();
            mTimer=null;
        }
    }

    @Override
    public void onConnectMasterDevice() {
        Log.i(Tag, "onConnectMasterDevice");
        isConnectBT = true;
        cancelTimer();
        Toast.makeText(this, "onConnectMasterDevice", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnectMasterDevice() {
        Log.i(Tag, "onDisconnectMasterDevice");
        isConnectBT = false;
        setTimer();
        Toast.makeText(this, "onDisconnectMasterDevice", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLEDPatternChanged(PulseThemePattern pattern) {
        //Toast.makeText(this, "onLEDPatternChanged:" + pattern.name(), Toast.LENGTH_SHORT);
        Log.i(Tag, "onLEDPatternChanged");

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }

    @Override
    public void onSoundEvent(final int soundLevel) {
        //Toast.makeText(this, "onSoundEvent: level=" + soundLevel, Toast.LENGTH_SHORT);
        Log.i(Tag, "soundLevel:"+soundLevel);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // ??
            }
        });
    }

    @Override
    public void onRetCaptureColor(final PulseColor capturedColor) {
//        Toast.makeText(this,
//                "onRetCaptureColor: red=" + capturedColor.red + " green=" + capturedColor.green + " blue=" + capturedColor.blue,
//                Toast.LENGTH_SHORT);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Do nothing
            }
        });
    }

    @Override
    public void onRetCaptureColor(byte red, byte green, byte blue) {
        //Toast.makeText(this, "onRetCaptureColor1: red=" + red + " green=" + green + " blue=" + blue, Toast.LENGTH_SHORT);
    }

    @Override
    public void onRetSetDeviceInfo(boolean ret) {
        //Toast.makeText(this, "onRetSetDeviceInfo:"+ret, Toast.LENGTH_SHORT);
    }

    @Override
    public void onRetGetLEDPattern(PulseThemePattern pattern) {
        //Toast.makeText(this, "onRetGetLEDPattern:" + (pattern== null ? "null":pattern.name()), Toast.LENGTH_SHORT);
    }

    @Override
    public void onRetRequestDeviceInfo(DeviceModel[] deviceModel) {
        //Toast.makeText(this, "onRetRequestDeviceInfo:"+deviceModel.toString(), Toast.LENGTH_SHORT);
    }

    @Override
    public void onRetSetLEDPattern(boolean b) {
        // Do nothing
    }

    @Override
    public void onRetBrightness(int i) {

    }
}
