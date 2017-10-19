package com.example.lenovo.gridy;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class GridHorizontalRotator implements IGridChanger {
    private final AppCompatActivity activity;
    private final GridView grid;

    GridHorizontalRotator(AppCompatActivity activity, int gridID){
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
                    new_traps.add(mt[i][j]);
        return new_traps;
    }

    @Override
    public Animation changeUI(){;
        return applyRotation(0, 180);
    }

    //http://www.bogotobogo.com/Android/android19AnimationB.php#View3DTransition
    private Animation applyRotation(float start, float end) {
        final float centerX = this.grid.getWidth() / 2.0f;
        final float centerY = this.grid.getHeight() / 2.0f;

        final Rotation rotation =
                new Rotation(start, end, centerX, centerY, 310.0f);
        rotation.setDuration(this.activity.getResources().getInteger(R.integer.one_anim_duration));
        rotation.setFillAfter(false);
        rotation.setInterpolator(new AccelerateInterpolator());

        //this.grid.startAnimation(rotation);
        return rotation;
    }
}

class Rotation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private Camera mCamera;


    public Rotation(float fromDegrees, float toDegrees,
                    float centerX, float centerY, float depthZ) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        if(degrees < mToDegrees / 2)
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        else
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}