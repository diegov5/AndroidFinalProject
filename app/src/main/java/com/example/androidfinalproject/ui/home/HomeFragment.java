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

package com.example.androidfinalproject.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.androidfinalproject.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.Track;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;
    private TextView playlistText1;
    private TextView playlistText2;
    private TextView playlistText3;
    private TextView playlistText4;
    private TextView playlistText5;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "e9fa5421cee0490f8c1a636504ceccc8";
    private static final String REDIRECT_URI = "androidfinalproject://callback";
    ListItem[] listOfRecommendedItems;

    /*
     * Method responsible for inflating the fragment and initiating all of the widgets in
     *  the layout
     *
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_personal, container, false);

        image1 = root.findViewById(R.id.image1);
        image1.setOnClickListener(this);
        image2 = root.findViewById(R.id.image2);
        image2.setOnClickListener(this);
        image3 = root.findViewById(R.id.image3);
        image3.setOnClickListener(this);
        image4 = root.findViewById(R.id.image4);
        image4.setOnClickListener(this);
        image5 = root.findViewById(R.id.image5);
        image5.setOnClickListener(this);

        playlistText1 = root.findViewById(R.id.playlistTitle1);
        playlistText2 = root.findViewById(R.id.playlistTitle2);
        playlistText3 = root.findViewById(R.id.playlistTitle3);
        playlistText4 = root.findViewById(R.id.playlistTitle4);
        playlistText5 = root.findViewById(R.id.playlistTitle5);

        return root;
    }

    /*
     * Method responsible for playing the playlist that got clicked in the layout
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                mSpotifyAppRemote.getPlayerApi().play(listOfRecommendedItems[0].uri);
                break;
            case R.id.image2:
                mSpotifyAppRemote.getPlayerApi().play(listOfRecommendedItems[1].uri);
                break;
            case R.id.image3:
                mSpotifyAppRemote.getPlayerApi().play(listOfRecommendedItems[2].uri);
                break;
            case R.id.image4:
                mSpotifyAppRemote.getPlayerApi().play(listOfRecommendedItems[3].uri);
                break;
            case R.id.image5:
                mSpotifyAppRemote.getPlayerApi().play(listOfRecommendedItems[4].uri);
                break;
        }
    }

    /*  https://developer.spotify.com/documentation/android/quick-start/
     *   Handles the connection from this application to the Spotify service
     *   If successful, it calls the connected method, if not, calls the failure method
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
        SpotifyAppRemote.connect(getContext(), connectionParams, new Connector.ConnectionListener() {
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

    /*  https://developer.spotify.com/documentation/android/quick-start/
     *  Method is invoked once the app is successfully connected to spotify and performs an action
     *
     */
    private void connected() {
        // Then we will write some more code here.
        mSpotifyAppRemote.getContentApi().getRecommendedContentItems(ContentApi.ContentType.DEFAULT)
            .setResultCallback(listItems -> {
                mSpotifyAppRemote.getContentApi().getChildrenOfItem(listItems.items[1], 7, 0)
                        .setResultCallback(listItems1 -> {
                            listOfRecommendedItems = listItems1.items;
                            Log.d("HomeFragment", "The title for the song is " + listOfRecommendedItems[0]);
                            ImageView[] listOfImageViews = {image1, image2, image3, image4, image5};
                            TextView[] listOfTextViews = {playlistText1, playlistText2, playlistText3, playlistText4, playlistText5};
                            for (int i = 0; i < 5; i ++) {
                                int finalI = i;
                                mSpotifyAppRemote.getImagesApi().getImage(listOfRecommendedItems[i].imageUri)
                                        .setResultCallback(bitmap -> listOfImageViews[finalI].setImageBitmap(bitmap));
                                listOfTextViews[i].setText(listOfRecommendedItems[i].title);
                            }
                        });
        });
    }

    /*
     *   This method disconnects from Spotify once the app is closed
     *
     */
    public void onStop() {
        super.onStop();
        // Aaand we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}