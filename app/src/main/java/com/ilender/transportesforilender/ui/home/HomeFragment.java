package com.ilender.transportesforilender.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ilender.transportesforilender.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {



//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // binding = null;
    }
}