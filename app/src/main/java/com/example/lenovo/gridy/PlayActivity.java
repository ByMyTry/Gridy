package com.example.lenovo.gridy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;


public class PlayActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    private Intent serviceIntent;

    private DataBaseHelper sqlHelper;
    private SQLiteDatabase db;
    private Cursor dbCursor;

    static {
        System.loadLibrary("lbr");
    }
    //private native String getMessageFromNative();

    private GestureDetector initGestureDetector() {
        return new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if((Math.sqrt(Math.pow(e1.getX() - e2.getX(),2.0) + Math.pow(e1.getY() - e2.getY(), 2.0)) > 120)
                        && ( Math.abs(velocityX)+ Math.abs(velocityY) > 200)) {
                    showToast("Swipe Detected");
                }
                return false;
            }
        });
    }

    private void showToast(String phrase){
        Toast.makeText(getApplicationContext(), phrase, Toast.LENGTH_SHORT).show();
    }

    private void makeAnimationChain(final ArrayList<Animation> animations, int gridId, final GameGrid gameGrid){
        final GridView grid = (GridView) this.findViewById(gridId);
        final int length = animations.size();
        final int[] kostil = new int[]{0};
        for(int i = 0; i < length; i++) {
            animations.get(i).setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    int i = kostil[0];
                    if(i < length - 1)
                        grid.startAnimation(animations.get(i + 1));
                    else {
                        View v1 = PlayActivity.this.findViewById(R.id.trybutton);
                        v1.setVisibility(View.VISIBLE);
                        gameGrid.unblokeGrid();
                    }
                    kostil[0] += 1;
                }

                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.serviceIntent=new Intent(this, MusicService.class);
        startService(this.serviceIntent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gestureDetector = this.initGestureDetector();
        View linearLayout = this.findViewById(R.id.ll);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return PlayActivity.this.gestureDetector.onTouchEvent(event);
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            }
        });

        final GameGrid gameGrid = new GameGrid(4, 3, this, R.id.gridview);
        gameGrid.renderGrid();
        this.findViewById(R.id.playbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameGrid.blokeGrid();
                v.setVisibility(View.INVISIBLE);
                gameGrid.choiceNewTraps();
                gameGrid.showTraps();
                new ShowTask().execute(gameGrid);
            }
        });

        this.findViewById(R.id.trybutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameGrid.test()) {
                    PlayActivity.this.showToast("You win");
                } else {
                    PlayActivity.this.showToast("You lose");
                }
                gameGrid.removeSelected();
                v.setVisibility(View.INVISIBLE);
                PlayActivity.this.findViewById(R.id.playbutton).setVisibility(View.VISIBLE);
            }
        });

        sqlHelper = new DataBaseHelper(getApplicationContext());
    }

    class ShowTask extends AsyncTask<GameGrid, Void, Void>{
        private GameGrid gameGrid;

        @Override
        protected Void doInBackground(GameGrid... params) {
            this.gameGrid = params[0];
            SystemClock.sleep(2000);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            gameGrid.hideTraps();
            IGridChanger[] level = new IGridChanger[]{
                    new GridLeftRotator(PlayActivity.this, R.id.gridview),
                    new GridHorizontalRotator(PlayActivity.this, R.id.gridview),
            };
            ArrayList<Animation> animations = new ArrayList<Animation>();
            for(IGridChanger gridChanger : level)
                animations.add(
                        gameGrid.changeGrid(gridChanger)
                );

            makeAnimationChain(animations, R.id.gridview, gameGrid);
            GridView grid = (GridView) PlayActivity.this.findViewById(R.id.gridview);
            grid.startAnimation(animations.get(0));
            //((Button)v).setText("try");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startService(this.serviceIntent);

        db = sqlHelper.getReadableDatabase();
        dbCursor = db.rawQuery("select * from users", null);
        dbCursor.moveToFirst();
        String name = dbCursor.getString(dbCursor.getColumnIndex("name"));
        Integer group = dbCursor.getInt(dbCursor.getColumnIndex("groupp"));
        TextView tv = (TextView)this.findViewById(R.id.textview);
        tv.setText(name + '\n' + group + '\n');
        //tv.setText(name + '\n' + group + '\n' + getMessageFromNative());
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopService(this.serviceIntent);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
        dbCursor.close();
    }
}
