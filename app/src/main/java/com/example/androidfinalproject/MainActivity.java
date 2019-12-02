package com.example.androidfinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ImagesApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.Track;
import com.spotify.protocol.types.Uri;
import com.squareup.picasso.Picasso;

import java.net.URI;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    final static String TAG = "MainActivity";
    private static final String CLIENT_ID = "e9fa5421cee0490f8c1a636504ceccc8";
    private static final String REDIRECT_URI = "androidfinalproject://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    ImageView naviagtionViewImage;
    TextView username;
    NavigationView navigationDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.username);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hview = navigationView.getHeaderView(0);
        naviagtionViewImage = hview.findViewById(R.id.navigationViewImage);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "in On New Intent");
    }

    @Override
    public void onStart() {
        super.onStart();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        Log.d("MainActivity", "here! Yay!");
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {

                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("MainActivity", "Connected! Yay!");

                // Now you can start interacting with App Remote
                connected();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);
                Log.e("MainActivity", "Connection failed, error above ^");
                // Something went wrong when attempting to connect! Handle errors here
            }
        });
    }

    private void connected() {
        // Then we will write some more code here.
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                    //PlayerContext currentContext = new PlayerContext();
                    Log.d(TAG, "Context URI:" + track.imageUri);
                    //username.setText(mSpotifyAppRemote.getUserApi().toString());
                    mSpotifyAppRemote.getImagesApi().getImage(track.imageUri)
                            .setResultCallback(bitmap -> naviagtionViewImage.setImageBitmap(bitmap));
                    //Picasso.get().load().into(naviagtionViewImage);
                });
        
    }


    public void onStop() {
        super.onStop();
        // Aaand we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
