package com.example.lenovo.gridy;

import android.view.animation.Animation;

import java.util.ArrayList;

public interface IGridChanger {
    ArrayList<Integer> changeData(int N, ArrayList<Integer> currentTraps);
    Animation changeUI();
}
