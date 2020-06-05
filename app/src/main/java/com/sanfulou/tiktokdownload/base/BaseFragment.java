package com.sanfulou.tiktokdownload.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

abstract public class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(Objects.requireNonNull(container).getContext()).inflate(initLayout(), container, false);
        initView(view);
        return view;
    }

    abstract protected int initLayout();

    abstract protected void initView(View view);

}
