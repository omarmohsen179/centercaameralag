package com.example.nolaging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraGLSurfaceView;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity  implements
        CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {

private CameraBridgeViewBase cameraView;
private CascadeClassifier classifier;
private CascadeClassifier classifierupper;
private Mat mGray;
private Mat mRgba;
private int mAbsoluteFaceSize = 0;
private boolean isFrontCamera;

static {
        System.loadLibrary("opencv_java3");
        }

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initWindowSettings();
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.camera_view);

        cameraView.setVisibility(SurfaceView.VISIBLE);
        initClassifier();
        cameraView.enableView();
        Button switchCamera = (Button) findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
       // cameraView.enableFpsMeter();
        //cameraView.setMaxFrameSize(320,240);
        //cameraView.onPreviewFrame();

        }

@Override
public void onClick(View v) {
        switch (v.getId()) {
        case R.id.switch_camera:
        cameraView.disableView();
        if (isFrontCamera) {
        cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        isFrontCamera = false;
        } else {
        cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        isFrontCamera = true;
        }
        cameraView.enableView();
        break;
default:
        }
        }


private void initWindowSettings() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }


private void initClassifier() {
        try {
        InputStream is = getResources()
        .openRawResource(R.raw.lbpcascade_frontalface_improved);
        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
        File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface_improved.xml");
        FileOutputStream os = new FileOutputStream(cascadeFile);
        byte[] buffer = new byte[4096];

        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
        os.write(buffer, 0, bytesRead);
        }
        is.close();
        os.close();
        classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
        } catch (Exception e) {
        e.printStackTrace();
        }

        try {
                InputStream is = getResources()
                        .openRawResource(R.raw.haarcascade_frontalface_alt);
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
                FileOutputStream os = new FileOutputStream(cascadeFile);
                byte[] buffer = new byte[4096];

                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();
                classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
        } catch (Exception e) {
                e.printStackTrace();
        }
}

@Override
public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();

        }

@Override
public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        }
        int[] location={9,8,7,6,5,4,3,2,1,0,-1,-2,-3,-4,-5,-6,-7,-8,-9};
        int[] placew=new int[19];
        float screenwidth;

        int checkbody = 0;




public void detectface(int checkbody){
        int wdiv;
        int wsize;
        wsize = (int) screenwidth;
        wdiv = (int) (screenwidth / 19);
        for (int i = 0; i < 19; i += 1) {

                placew[i] = wsize - wdiv;
                wsize = wsize - wdiv;

        }
        MatOfRect faces = new MatOfRect();
        float mRelativeFaceSize = 0.2f;
        int height = mGray.rows();
        mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
        classifier.detectMultiScale(mGray, faces, 1.1, 2, 2,
                new Size(mRelativeFaceSize, mRelativeFaceSize), new Size());
        Rect[] facesArray = faces.toArray();
        for (Rect faceRect :facesArray){
                Imgproc.rectangle(mRgba, faceRect.tl(), faceRect.br(),new Scalar(0, 255, 0, 255), 9);

                if(checkbody==0){
                        int objectsizewidth
                                = (int) ((int) (( faceRect.br().x- faceRect.tl().x)/2)+faceRect.tl().x);
                        int lcwi=0;
                        wsize= (int) screenwidth;
                        wdiv= (int) (screenwidth/19);
                        for (int i = 0; i < 19; i += 1) {

                                placew[i] = wsize - wdiv;
                                wsize = wsize - wdiv;

                        }
                        for (int i = 0; i < 19; i++) {
                                if (i + 1 < 19) {
                                        if (objectsizewidth <= placew[i] && objectsizewidth >= placew[i + 1]) {
                                                lcwi = i;
                                                break;
                                        }
                                } else {
                                        if (objectsizewidth <= placew[i] && objectsizewidth >= placew[i - 1]) {
                                                lcwi = i;
                                                break;
                                        }
                                }
                        }
                        Imgproc.putText (mRgba,
                                " width== "+location[lcwi+1]
                                , new Point(10, 50)
                                , Core.FONT_HERSHEY_SIMPLEX
                                , 1
                                , new Scalar(0, 255, 0, 255)
                                , 4);
                }


        }
}
@Override

public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        int checkbody = 0;
        //Imgproc.resize( mRgba,mRgba, new Size(160,120) );

        //Imgproc.resize(mRgba, mGray, new Size(120,160) );
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
        if (isFrontCamera) {
                Core.flip(mRgba, mRgba, 1);
                Core.flip(mGray, mGray, 1);
        } else {
                Core.flip(mRgba, mRgba, 1);
                Core.flip(mGray, mGray, 1);
                Core.flip(mRgba, mRgba, 1);
                Core.flip(mGray, mGray, 1);

        }
        if (classifier != null) {
                screenwidth = mRgba.width();

                detectface(checkbody);
        }
        }
        else{
                Mat rotImage = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2,
                        mRgba.rows() / 2), 90, 1.0);
                Imgproc.warpAffine(mRgba, mRgba, rotImage, mRgba.size());
                Imgproc.warpAffine(mGray, mGray, rotImage, mRgba.size());
                if (isFrontCamera) {
                        Core.flip(mRgba, mRgba, 1);
                        Core.flip(mGray, mGray, 1);
                }
                else {
                        Core.flip(mRgba, mRgba, -1);
                        Core.flip(mGray, mGray, -1);
                     

                }
                if(classifier!=null){
                detectface(checkbody);
                }
        }

        return mRgba;
        }


@Override
protected void onPause() {
        super.onPause();
        if (cameraView != null) {
        cameraView.disableView();
        }
        }

@Override
protected void onDestroy() {
        super.onDestroy();
        cameraView.disableView();
        }
        }