package com.ubicomp.stopit.stopit.model;

import android.graphics.Path;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ubicomp.stopit.stopit.presenter.SpiralActivityPresenter;

import java.util.ArrayList;
import java.util.List;

public class DrawTriangleCoordinates {
    private DatabaseReference mDatabase;

    public final static double thetaStepSize = 0.1;
    public final static double turnsNumber = 2;
    public final static double turnFull = Math.PI * 2;
    public final static double turnsDistance = 30; // ?? What's the scaling?

    public DrawTriangleCoordinates() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // draws grey line
    public void drawGreyPath(Path triangle) {
        float x0 = SpiralActivityPresenter.width/2- SpiralActivityPresenter.width/6;   // Starting point of the spiral
        float y0 = SpiralActivityPresenter.height/2-SpiralActivityPresenter.height/6 ;  // is always in the middle of the screen
        float x1=x0*2;
        float y1=y0*2;
        triangle.moveTo(x0,y0);
        triangle.lineTo(x1,y0);
        triangle.moveTo(x1,y0);
        triangle.lineTo(x1,y1);
        triangle.moveTo(x1,y1);
        triangle.lineTo(x0,y1);
        triangle.moveTo(x0,y1);
        triangle.lineTo(x0,y0);



        }

    // finds the coordinates of specified number of dots over the whole spiral
    public List<List<Float>> getGreyCoordinates(int size) {
        float x0 = SpiralActivityPresenter.width / 2;   // Starting point of the spiral
        float y0 = SpiralActivityPresenter.height / 2;  // is always in the middle of the screen
        float x;
        float y;
        double theta = 0;
        double thetaStepSize = turnsNumber*turnFull/size;
        List<List<Float>> originalDots = new ArrayList<>();

        for (int i=1; i<=size; i++) {
            x = (float) (turnsDistance * theta * Math.cos(theta) + x0);
            y = (float) (turnsDistance * theta * Math.sin(theta) + y0);
            mDatabase.child("users").child(SpiralActivityPresenter.USERNAME).child("originalDots").child("" + i).child("x").setValue(x);
            mDatabase.child("users").child(SpiralActivityPresenter.USERNAME).child("originalDots").child("" + i).child("y").setValue(y);

            List<Float> listItem = new ArrayList<>();
            listItem.add(x);
            listItem.add(y);
            originalDots.add(listItem);

            theta += thetaStepSize;
        }

        return originalDots;
    }
}
