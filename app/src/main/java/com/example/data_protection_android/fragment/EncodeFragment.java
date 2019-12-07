package com.example.data_protection_android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.data_protection_android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;

public class EncodeFragment extends Fragment {

    @BindView(R.id.tv_progress)
    TextView progressTv;

    private static final String FILE_PATH_KEY = "FILE_PATH_KEY";

    public static EncodeFragment getInstance(String path) {
        EncodeFragment fragment = new EncodeFragment();
        Bundle args = new Bundle();
        args.putString(FILE_PATH_KEY, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encode, container, false);
        return view;
    }

    private void start() {

    }
}
