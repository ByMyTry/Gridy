package com.example.lenovo.gridy;

import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameGrid {
    public final int size;
    public final int trapCount;
    private final AppCompatActivity activity;
    private final GridView grid;
    private final ArrayList<Boolean> gridData;
    private ArrayList<Integer> traps;
    private ArrayList<Integer> selected;
    private AdapterView.OnItemClickListener itemListener;

    private int DpToPx(int sizeInDp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sizeInDp,
                this.activity.getResources().getDisplayMetrics()
        );
    }

    private ArrayList<Boolean> generateGridData(int size) {
        ArrayList<Boolean> res = new ArrayList<>();
        for(int i = 0; i < size * size; i++)
            res.add(false);
        return res;
    }

    class GameAdapter extends ArrayAdapter<Boolean> {
        private LayoutInflater mInflater;

        GameAdapter(ArrayList<Boolean> list){
            super(GameGrid.this.activity, R.layout.grid_item, list);
            mInflater = LayoutInflater.from(GameGrid.this.activity);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            if(row == null){
                row = mInflater.inflate(R.layout.grid_item, parent, false);
            }
            return row;
        }
    }

    GameGrid(int size, int trapCount, AppCompatActivity activity, int gridId)
    {
        this.size = size;
        this.trapCount = trapCount;
        this.activity = activity;
        this.grid = (GridView) this.activity.findViewById(gridId);
        this.gridData = this.generateGridData(size);
        this.itemListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                if (GameGrid.this.selected.contains(position)) {
                    v.setBackgroundColor(GameGrid.this.activity.getResources().getColor(R.color.colorBlack));
                    GameGrid.this.selected.remove((Integer)position);
                } else {
                    if (GameGrid.this.selected.size() < GameGrid.this.trapCount) {
                        v.setBackgroundColor(GameGrid.this.activity.getResources().getColor(R.color.colorYellow));
                        GameGrid.this.selected.add(position);
                    }
                }
            }
        };
    }

    public void choiceNewTraps()
    {
        this.traps = new ArrayList<Integer>();
        Random random = new Random();
        for(int i = 0; i < this.trapCount; i++){
            int n;
            do {
                n = random.nextInt(this.size * this.size);
            }while(this.traps.contains(n));
            this.traps.add(n);
        }
    }

    public void renderGrid()
    {
        this.selected = new ArrayList<Integer>();
        this.grid.setNumColumns(this.size);
        GameAdapter adapter = new GameAdapter(this.gridData);
        this.grid.setAdapter(adapter);
        this.grid.setOnItemClickListener(this.itemListener);
    }

    public Animation changeGrid(IGridChanger gridChanger)
    {
        this.traps = gridChanger.changeData(this.size, this.traps);
        return gridChanger.changeUI();
    }

    public void showTraps(){
        this.grid.setOnItemClickListener(null);
        for(int i = 0; i < this.trapCount; i++)
            this.grid.getChildAt(this.traps.get(i)).setBackgroundColor(GameGrid.this.activity.getResources().getColor(R.color.colorRad));
    }

    public void hideTraps(){
        for(int i = 0; i < this.trapCount; i++)
            this.grid.getChildAt(this.traps.get(i)).setBackgroundColor(GameGrid.this.activity.getResources().getColor(R.color.colorBlack));
    }

    public void blokeGrid(){
        this.grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });
    }

    public void unblokeGrid(){
        this.grid.setOnItemClickListener(this.itemListener);
    }


    public Boolean test()
    {
        for(int i = 0; i < this.trapCount; i++)
            if(!this.selected.contains(this.traps.get(i)))
                return false;
        return true;
    }

    public void removeSelected()
    {
        for(int i = 0; i < this.selected.size(); i++){
            this.grid.getChildAt(this.selected.get(i)).setBackgroundColor(GameGrid.this.activity.getResources().getColor(R.color.colorBlack));
        }
        this.selected.clear();
    }
}
