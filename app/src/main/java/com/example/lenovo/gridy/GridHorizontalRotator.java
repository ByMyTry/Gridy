package com.example.lenovo.gridy;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.GridView;

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
        return currentTraps;
    }

    @Override
    public void changeUI(){;
        applyRotation(0, 180, true);
    }

    //http://www.bogotobogo.com/Android/android19AnimationB.php#View3DTransition
    private void applyRotation(float start, float end, boolean reverse) {
        final float centerX = this.grid.getWidth() / 2.0f;
        final float centerY = this.grid.getHeight() / 2.0f;

        final Rotation rotation =
                new Rotation(start, end, centerX, centerY, 310.0f, reverse);
        rotation.setDuration(2250);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new AccelerateInterpolator());

        this.grid.startAnimation(rotation);
    }
}

class Rotation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private final boolean mReverse;
    private Camera mCamera;


    public Rotation(float fromDegrees, float toDegrees,
                    float centerX, float centerY, float depthZ, boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
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
        if(degrees < 90)
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