/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
 *
 *   This file is part of Robobo HRI Modules.
 *
 *   Robobo HRI Modules is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo HRI Modules is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo HRI Modules.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/
package com.mytechia.robobo.framework.hri.vision.colorDetection.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import com.mytechia.commons.framework.exception.ConfigurationException;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraListener;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.colorDetection.AColorDetectionModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;


/**
 * Implementation of the color detection module using OpenCV // OLD
 */
public class OpenCvColorDetectionModule extends AColorDetectionModule implements ICameraListener {
    /*
     * http://www.workwithcolor.com/orange-brown-color-hue-range-01.htm
     */

    private String TAG = "OCVColormodule";
    private Context context;
    private int cuentaframes = 0;
    private boolean paused = true;
    private ICameraModule cameraModule;

    private boolean rcpresent = false;
    private boolean processing = false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    m.log(LogLvl.INFO, TAG, "OpenCV loaded successfully");

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        context = manager.getApplicationContext();
        m = manager;
        if (!OpenCVLoader.initDebug()) {
            m.log(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);
        } else {
            m.log(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        cameraModule = manager.getModuleInstance(ICameraModule.class);
        rcmodule = manager.getModuleInstance(IRemoteControlModule.class);

        rcmodule.registerCommand("START-COLOR-DETECTION", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                startDetection();
            }
        });

        rcmodule.registerCommand("STOP-COLOR-DETECTION", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                pauseDetection();

            }
        });

        startDetection();

    }

    @Override
    public void shutdown() throws InternalErrorException {
        cameraModule.unsuscribe(this);
    }

    @Override
    public String getModuleInfo() {
        return "OpenCv Color detection module";
    }

    @Override
    public String getModuleVersion() {
        return "0.3.0";
    }

    private void processFrame(Bitmap bmp) {
        if (!processing) {
            processing = true;
            Scalar mBlobColorHsv = new Scalar(0, 0, 0);
            //Log.d(TAG,"Cojo un frame y lo tiro por el retrete, y ya son "+cuentaframes+" frames los que el retrete se ha tragado!");
            //cuentaframes++;


            Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
            Mat imageMat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);

            Mat hsvMat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);

            Mat bwimage = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1);
            Imgproc.equalizeHist(bwimage, bwimage);
            Utils.bitmapToMat(bmp32, imageMat);

            Imgproc.cvtColor(imageMat, hsvMat, Imgproc.COLOR_RGB2HSV, 3);


            Imgproc.cvtColor(imageMat, bwimage, Imgproc.COLOR_RGB2GRAY, 1);

            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.Canny(bwimage, bwimage, 75, 100);

            //TODO Cambiar RETR_LIST por  RETR_EXTERNAL y probarlo
            Imgproc.findContours(bwimage, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            bwimage.release();


            double maxArea = -1;
            int maxAreaIdx = -1;

            for (int idx = 0; idx < contours.size(); idx++) {
                Mat contour = contours.get(idx);
                double contourarea = Imgproc.contourArea(contour);

                if ((contourarea > maxArea)) {
                    maxArea = contourarea;
                    maxAreaIdx = idx;
                }
            }


            if (maxArea > 500) {
                //MatOfPoint contour = contours.get(maxAreaIdx);
                Mat contourMat = Mat.zeros(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1);


                Imgproc.drawContours(contourMat, contours, maxAreaIdx, new Scalar(1), -1);


                //Rect boundingRect = Imgproc.boundingRect(contour);

                List<Mat> lHsv = new ArrayList<Mat>(3);
                Core.split(hsvMat, lHsv);
                Mat mH = lHsv.get(0);
                Mat mS = lHsv.get(1);
                Mat mV = lHsv.get(2);

                double meansum = 0;
                double means = 0;
                double meanv = 0;


                double squaredsum = 0;
                double squaredsumv = 0;
                double count = 0;

                for (int i = 0; i < contourMat.rows(); i++) {
                    for (int j = 0; j < contourMat.cols(); j++) {
                        if (contourMat.get(i, j)[0] != 0) {
                            count = count + 1;
                            double pixel = mH.get(i, j)[0];
                            double pixelv = mH.get(i, j)[0];
                            meansum = meansum + pixel;
                            meanv = meanv + mH.get(i, j)[0];
                            means = means + mS.get(i, j)[0];
                            squaredsum = squaredsum + Math.pow(pixel, 2);
                            squaredsumv = squaredsumv + Math.pow(pixel, 2);

                        }
                    }
                }

                double mean = meansum / count;
                meanv = meanv / count;
                means = means / count;
                double meansquearedvalues = squaredsum / count;
                double meansquearedvaluesv = squaredsumv / count;

                double variance = meansquearedvalues / (Math.pow(mean, 2));
                double variancev = meansquearedvaluesv / (Math.pow(meanv, 2));
                double variances = meansquearedvalues / (Math.pow(meanv, 2));

                m.log(LogLvl.TRACE, TAG, "Variance: " + variance + " Mean: " + mean + " Variance v: " + variancev + " Mean v: " + meanv);
                //1.02
                if (variance < 1.03) {


                    //mBlobColorHsv = Core.mean(hsvMat, contourMat); //(float) sum/pointList.size();

                    hsvMat.release();


                    float[] floatHsv = new float[3];
                    floatHsv[0] = (float) mean * 2;
                    floatHsv[1] = (float) means;
                    floatHsv[2] = (float) meanv;

                    Imgproc.drawContours(imageMat, contours, maxAreaIdx, new
                            Scalar(255));

                    Utils.matToBitmap(imageMat, bmp);

                    mBlobColorHsv.val[0] = mean * 2;
                    int colorrgb = Color.HSVToColor(floatHsv);

                    mean = mean - 8;
                    if (mean < 0) {
                        mean = 171 + Math.abs(mean);
                    }
                    if ((mean > 166) && (mean <= 179)) {
                        m.log(LogLvl.TRACE, TAG, "RED" + mean);
//                    notifyColor(colorrgb, Color.RED, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
                        notifyColor(colorrgb, Color.RED);

                    }

                    if ((mean > 0) && (mean <= 29)) {
                        m.log(LogLvl.TRACE, TAG, "YELLOW" + mean);
//                    notifyColor(colorrgb, Color.YELLOW, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
                        notifyColor(colorrgb, Color.YELLOW);
                    }
                    if ((mean > 30) && (mean <= 66)) {
                        m.log(LogLvl.TRACE, TAG, "GREEN" + mean);
//                    notifyColor(colorrgb, Color.GREEN, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
                        notifyColor(colorrgb, Color.GREEN);
                    }
                    if ((mean > 67) && (mean <= 96)) {
                        m.log(LogLvl.TRACE, TAG, "CYAN" + mean);
//                    notifyColor(colorrgb, Color.CYAN, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
                        notifyColor(colorrgb, Color.CYAN);

                    }
                    if ((mean > 97) && (mean <= 141)) {
                        m.log(LogLvl.TRACE, TAG, "BLUE" + mean);
//                    notifyColor(colorrgb, Color.BLUE, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
                        notifyColor(colorrgb, Color.BLUE);
                    }
                    if ((mean > 142) && (mean <= 165)) {
                        m.log(LogLvl.TRACE, TAG, "MAGENTA" + mean);
//                    notifyColor(colorrgb, Color.MAGENTA, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
                        notifyColor(colorrgb, Color.MAGENTA);
                    }


                }
            }
            processing = false;
        }
    }

    @Override
    public void startDetection() {
        cameraModule.suscribe(this);
        paused = false;
    }

    @Override
    public void pauseDetection() {
        paused = true;
        cameraModule.unsuscribe(this);
    }

    @Override
    public void onNewFrame(Frame frame) {
        if (!paused) {
            processFrame(frame.getBitmap());
        }
    }

    @Override
    public void onNewMat(Mat mat) {

    }

    @Override
    public void onDebugFrame(Frame frame, String frameId) {

    }

    @Override
    public void onOpenCVStartup() {

    }
}
