package com.example.androidfinalproject.ui.workout;

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

public class WorkoutFragment extends Fragment implements View.OnClickListener {

    private WorkoutViewModel workoutViewModel;
    SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "e9fa5421cee0490f8c1a636504ceccc8";
    private static final String REDIRECT_URI = "androidfinalproject://callback";
    ListItem[] listOfWorkoutRecommendedItems;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    TextView playlistText1;
    TextView playlistText2;
    TextView playlistText3;
    TextView playlistText4;
    TextView playlistText5;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        workoutViewModel =
                ViewModelProviders.of(this).get(WorkoutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_workout, container, false);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                mSpotifyAppRemote.getPlayerApi().play(listOfWorkoutRecommendedItems[0].uri);
                break;
            case R.id.image2:
                mSpotifyAppRemote.getPlayerApi().play(listOfWorkoutRecommendedItems[1].uri);
                break;
            case R.id.image3:
                mSpotifyAppRemote.getPlayerApi().play(listOfWorkoutRecommendedItems[2].uri);
                break;
            case R.id.image4:
                mSpotifyAppRemote.getPlayerApi().play(listOfWorkoutRecommendedItems[3].uri);
                break;
            case R.id.image5:
                mSpotifyAppRemote.getPlayerApi().play(listOfWorkoutRecommendedItems[4].uri);
                break;
        }
    }

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

    private void connected() {
        // Then we will write some more code here.
        mSpotifyAppRemote.getContentApi().getRecommendedContentItems(ContentApi.ContentType.FITNESS)
                .setResultCallback(listItems -> {
                    listOfWorkoutRecommendedItems = listItems.items;
                    Log.d("WorkoutFragment", "The title for the song is " + listOfWorkoutRecommendedItems[0]);
                    ImageView[] listOfImageViews = {image1, image2, image3, image4, image5};
                    TextView[] listOfTextViews = {playlistText1, playlistText2, playlistText3, playlistText4, playlistText5};
                    for (int i = 0; i < 5; i ++) {
                        int finalI = i;
                        mSpotifyAppRemote.getImagesApi().getImage(listOfWorkoutRecommendedItems[i+1].imageUri)
                                .setResultCallback(bitmap -> listOfImageViews[finalI].setImageBitmap(bitmap));
                        listOfTextViews[i].setText(listOfWorkoutRecommendedItems[i+1].title);
                    }
                });
    }


    public void onStop() {
        super.onStop();
        // Aaand we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}