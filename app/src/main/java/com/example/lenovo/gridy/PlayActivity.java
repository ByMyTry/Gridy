package com.example.lenovo.gridy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class PlayActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    private Intent serviceIntent;

    private DataBaseHelper sqlHelper;
    private SQLiteDatabase db;
    private Cursor dbCursor;

    static {
        System.loadLibrary("lbr");
    }
    private native String getMessageFromNative();

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

        final GameGrid gameGrid = new GameGrid(5, 5, this, R.id.gridview);
        gameGrid.renderGrid();
        this.findViewById(R.id.playbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameGrid.choiceTraps();
                gameGrid.showTraps();
                new ShowTask().execute(gameGrid);
                v.setVisibility(View.INVISIBLE);
                PlayActivity.this.findViewById(R.id.trybutton).setVisibility(View.VISIBLE);
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
        private GameGrid grid;

        @Override
        protected Void doInBackground(GameGrid... params) {
            SystemClock.sleep(2000);
            this.grid = params[0];
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            this.grid.hideTraps();
            this.grid.rotateGrid();
            /*View v1 = PlayActivity.this.findViewById(R.id.ll);
            Animation animation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.animation);
            v1.startAnimation(animation);*/
            View v = PlayActivity.this.findViewById(R.id.trybutton);
            ((Button)v).setText("try");
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
        tv.setText(name + '\n' + group + '\n' + getMessageFromNative());
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
