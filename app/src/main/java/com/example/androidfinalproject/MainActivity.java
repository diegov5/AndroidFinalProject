/*
 * This program uses the Spotify Android SDK to show recommended playlists based on a users listening habits and shows
 *      contextualized playlists
 *
 * CPSC 312-01, Fall 2019
 *
 * @authors Diego Valdez
 *          Patrick Seminatore
 *
 * References
 *  https://developer.spotify.com/documentation/android/quick-start/
 *  https://github.com/spotify/android-sdk
 *  https://developer.spotify.com/documentation/android/quick-start/#authorizing-user-with-single-sign-on-library
 *  https://developer.spotify.com/documentation/general/guides/content-linking-guide/
 *
 * @version v1.0
 */

package com.example.androidfinalproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.MenuItem;
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
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.io.Serializable;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    final static String TAG = "MainActivity";
    private static final String CLIENT_ID = "e9fa5421cee0490f8c1a636504ceccc8";
    private static final String REDIRECT_URI = "androidfinalproject://callback";
    SpotifyAppRemote mSpotifyAppRemote;
    ImageView naviagtionViewImage;
    TextView username;
    NavigationView navigationDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hview = navigationView.getHeaderView(0);
        naviagtionViewImage = hview.findViewById(R.id.navigationViewImage);
        username = hview.findViewById(R.id.username);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_workout, R.id.nav_sleeping,
                R.id.nav_driving)
                .setDrawerLayout(drawer)
                .build();
        Bundle bundle = new Bundle();
        bundle.putSerializable("spotifyAppRemote", (Serializable) mSpotifyAppRemote);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(navController.getGraph(), bundle);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share:
                mSpotifyAppRemote.getPlayerApi()
                        .subscribeToPlayerState()
                        .setEventCallback(playerState -> {
                            final Track track = playerState.track;
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, track.name);
                            String spotifyLink = track.uri;
                            spotifyLink = spotifyLink.replaceAll(":", "/");
                            spotifyLink = spotifyLink.replaceAll("spotify", "");
                            spotifyLink = "Hey! Check out this song shared from Recommendify, a Spotify third party app \n \n" + "open.spotify.com" + spotifyLink;
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, spotifyLink);
                            startActivity(Intent.createChooser(sharingIntent, "Share current track using"));
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "in On New Intent");
    }

    /*  https://developer.spotify.com/documentation/android/quick-start/
    *
    *
     */
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
                final String appPackageName = "com.spotify.music";
                final String referrer = "adjust_campaign=PACKAGE_NAME&adjust_tracker=ndjczk&utm_source=adjust_preinstall";

                try {
                    Uri uri = Uri.parse("market://details")
                            .buildUpon()
                            .appendQueryParameter("id", appPackageName)
                            .appendQueryParameter("referrer", referrer)
                            .build();
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (android.content.ActivityNotFoundException ignored) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details")
                            .buildUpon()
                            .appendQueryParameter("id", appPackageName)
                            .appendQueryParameter("referrer", referrer)
                            .build();
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }

            }
        });
    }

    /*  https://developer.spotify.com/documentation/android/quick-start/
     *
     *
     */
    private void connected() {
        // Then we will write some more code here.
        //mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                        String nowPlaying = track.name + " by " + track.artist.name;
                        username.setText(nowPlaying);
                    }
                    Log.d(TAG, "Context URI:" + track.imageUri);
                    mSpotifyAppRemote.getImagesApi().getImage(track.imageUri)
                            .setResultCallback(bitmap -> naviagtionViewImage.setImageBitmap(bitmap));
                });

    }


    public void onStop() {
        super.onStop();
        // Aaand we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }


    /* https://developer.spotify.com/documentation/general/guides/content-linking-guide/
     *
     *
     *
     */
    @Override
    public void onClick(View v) {
        PackageManager pm = getPackageManager();
        boolean isSpotifyInstalled;
        try {
            pm.getPackageInfo("com.spotify.music", 0);
            isSpotifyInstalled = true;
            mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(playerState -> {
                        final Track track = playerState.track;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(android.net.Uri.parse(track.uri));
                        intent.putExtra(Intent.EXTRA_REFERRER,
                                android.net.Uri.parse("android-app://" + this.getPackageName()));
                        startActivity(intent);
                    });
        } catch (PackageManager.NameNotFoundException e) {
            isSpotifyInstalled = false;
        }
    }
}
