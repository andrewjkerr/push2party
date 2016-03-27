package com.example.samanthasoto.myapplication;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.harman.pulsesdk.ImplementPulseHandler;
import com.harman.pulsesdk.PulseHandlerInterface;

public class MainActivity extends AppCompatActivity {

    Scene mScene;
    Scene m2Scene;
    Scene m3Scene;
    Scene m4Scene;


    FrameLayout framelayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        framelayout = (FrameLayout) findViewById(R.id.scene_root);

        mScene = Scene.getSceneForLayout(framelayout, R.layout.scene_party_main, this);
        m2Scene = Scene.getSceneForLayout(framelayout, R.layout.scene_partyplay_main, this);
        m3Scene = Scene.getSceneForLayout(framelayout, R.layout.scene_partyhow_main, this);
        m4Scene = Scene.getSceneForLayout(framelayout, R.layout.scene_partypaused_main, this);


        TransitionManager.go(mScene);

        Button button_party = (Button) findViewById(R.id.button_party);
        button_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //speakers stuff from Andy

                TransitionManager.go(m2Scene, new Fade());

            }
        });

        /*ImageView button_go = (ImageView) findViewById(R.id.go_button);
        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //speakers stuff from Andy - play music

                TransitionManager.go(m2Scene, new Fade());

            }
        });*/

        /*ImageView button_pause = (ImageView) findViewById(R.id.button_pause);
        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //speakers stuff from Andy - stop music

                TransitionManager.go(m4Scene, new Fade());

            }
        });

        ImageView button_restart = (ImageView) findViewById(R.id.button_restart);
        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //speakers stuff from Andy - play music

                TransitionManager.go(m2Scene, new Fade());

            }
        });*/



        PulseHandlerInterface pulseHander = new ImplementPulseHandler();
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
}
