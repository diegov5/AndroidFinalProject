package com.example.androidfinalproject.ui.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.androidfinalproject.R;

public class NavigationFragment extends Fragment {

    private NavigationViewModel navigationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        navigationViewModel =
                ViewModelProviders.of(this).get(NavigationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_navigation, container, false);
        return root;
    }
}