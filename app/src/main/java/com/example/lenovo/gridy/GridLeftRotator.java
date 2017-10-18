package com.example.lenovo.gridy;

import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;

import java.util.ArrayList;

public class GridLeftRotator implements IGridChanger {
    private final AppCompatActivity activity;
    private final GridView grid;

    GridLeftRotator(AppCompatActivity activity, int gridID){
        this.activity = activity;
        this.grid = (GridView) this.activity.findViewById(gridID);
    }

    @Override
    public ArrayList<Integer> changeData(int N, ArrayList<Integer> currentTraps){
        int[][] mt = new int[N][N];
        for (int i = 0, k = 0; i < N; i++)
            for (int j = 0; j < N; j++, k++)
                mt[i][j] = k;
        ArrayList<Integer> new_traps = new ArrayList<Integer>();
        for (int i = 0, k = 0; i < N; i++)
            for (int j = N - 1; j > -1; j--, k++)
                if(currentTraps.contains(k))
                    new_traps.add(mt[j][i]);
        /*StringBuilder sb = new StringBuilder();
        for( Integer i: this.traps)
            sb.append(i.toString() + ", ");
        Toast.makeText(this.activity.getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();*/
        return new_traps;
    }

    @Override
    public void changeUI(){
        Animation animation = AnimationUtils.loadAnimation(this.activity, R.anim.left_rotate);
        animation.setFillAfter(false);
        this.grid.startAnimation(animation);
    }
}
