package com.mytechia.robobo.framework.hri.vision.tag.opencv;

import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.vision.tag.ATagModule;
import com.mytechia.robobo.framework.hri.vision.tag.Tag;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraListener;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.util.AuxPropertyWriter;
import com.mytechia.robobo.framework.hri.vision.util.CameraDistortionCalibrationData;


import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.android.CameraBridgeViewBase.CAMERA_ID_FRONT;

public class OpencvTagModule extends ATagModule implements ICameraListener {

    private RoboboManager m;
    private ICameraModule cameraModule;
    private String distCoeffs = "{\"rows\"\\:1,\"cols\"\\:5,\"type\"\\:0,\"data\"\\:\"AQAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\u003d\\\\u003d\\\\n\"}";
    private String cameraMatrix = "{\"rows\"\\:3,\"cols\"\\:3,\"type\"\\:0,\"data\"\\:\"/wDtAP//AAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\nAAAAAAAAAAAAAAAAAAAA\\\\n\"}";
    private int currentTagDict = Aruco.DICT_4X4_1000;
    private CameraDistortionCalibrationData calibrationData;
    private AuxPropertyWriter propertyWriter;
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        m = manager;
        propertyWriter = new AuxPropertyWriter();
        // Load camera and remote control modules
        try {
            cameraModule = m.getModuleInstance(ICameraModule.class);

        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }

        distCoeffs = propertyWriter.retrieveConf("distCoeffs", distCoeffs);
        cameraMatrix = propertyWriter.retrieveConf("cameraMatrix", cameraMatrix);
        calibrationData = new CameraDistortionCalibrationData(cameraMatrix,distCoeffs,null,null);
        cameraModule.suscribe(this);
    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }

    @Override
    public void onNewFrame(Frame frame) {

    }

    @Override
    public void onNewMat(Mat mat) {
        Mat markerIds = new Mat();
        if (cameraModule.getCameraCode() == CAMERA_ID_FRONT){
            Core.flip(mat,mat,0);

        }
        ArrayList<Mat> markerCorners = new ArrayList<>();
        ArrayList<Mat> rejectedCandidates = new ArrayList<>();
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGRA2BGR);
        DetectorParameters parameters = DetectorParameters.create();
        parameters.set_minDistanceToBorder(0);
        parameters.set_adaptiveThreshWinSizeMax(100);
        Aruco.detectMarkers(mat, Aruco.getPredefinedDictionary(currentTagDict),markerCorners,markerIds, parameters,rejectedCandidates,calibrationData.getCameraMatrixMat(),calibrationData.getDistCoeffsMat());
        int i = 0;
        //Aruco.estimatePoseSingleMarkers(markerCorners,14.5f,cameraMatrix,distCoeffs);
        List<Tag>  tags = new ArrayList<>();
        for (i = 0; i < markerIds.rows(); i++){
            Tag tag;
            if (cameraModule.getCameraCode() == CAMERA_ID_FRONT){
               tag = new Tag(markerCorners.get(i),markerIds.get(i,0)[0], true, cameraModule.getResX());

            }else {
                tag = new Tag(markerCorners.get(i),markerIds.get(i,0)[0], false, cameraModule.getResX());

            }
            //Log.w("ARUCO", tag.toString());
            tags.add(tag);
        }

        if (markerIds.rows() > 0){
            notifyMarkersDetected(tags);
        }
        //Log.w("ARUCO", "Markers: "+markerIds.toString());

    }

    @Override
    public void onDebugFrame(Frame frame, String frameId) {

    }

    @Override
    public void onOpenCVStartup() {

    }

    @Override
    public void useAruco() {
        currentTagDict = Aruco.DICT_4X4_1000;
    }

    @Override
    public void useAprilTags() {
        currentTagDict = Aruco.DICT_APRILTAG_16h5;
    }

    @Override
    public void pauseDetection() {
        cameraModule.unsuscribe(this);
    }

    @Override
    public void resumeDetection() {
        cameraModule.suscribe(this);
    }
}