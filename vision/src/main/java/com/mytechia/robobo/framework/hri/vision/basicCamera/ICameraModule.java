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

package com.mytechia.robobo.framework.hri.vision.basicCamera;

import android.view.SurfaceView;

import com.mytechia.robobo.framework.IModule;

import org.opencv.android.CameraBridgeViewBase;

/**
 * Interface of the Robobo Basic Camera module
 */
public interface ICameraModule extends IModule {

    /**
     * Suscribes a listener to the frame notifications
     * @param listener The listener to be added
     */
    void suscribe(ICameraListener listener);

    /**
     * Unsuscribes a listener from the frame notifications
     * @param listener The listener to be removed
     */
    void unsuscribe(ICameraListener listener);

    /**
     * Suscribes a listener to the frame notifications
     * @param listener The listener to be added
     */
    void suscribe(ICameraListenerV2 listener);

    /**
     * Unsuscribes a listener from the frame notifications
     * @param listener The listener to be removed
     */
    void unsuscribe(ICameraListenerV2 listener);

    /**
     * Starts the image capture
     */
    void signalInit();



    /**
     * Pass a surface view to draw the images into
     * @param view The surfaceview
     */
    void passSurfaceView(SurfaceView view);

    /**
     * Pass the camerabridge for capturing images with opencv
     * @param bridgebase the OpenCV CameraBridgeBase
     */
    void passOCVthings(CameraBridgeViewBase bridgebase);

    /**
     * Changes between front and back camera
     */
    void changeCamera();

    /**
     * Returns frames for debugging
     * @param frame The frame
     * @param frameId Tag of the frame
     */
    void debugFrame(Frame frame, String frameId);

    /**
     * To show the frame in the opencv view
     * @param set True to activate the option, false instead (default false)
     */
    void showFrameInView(boolean set);
    /**
     * Sets the FPS for the frame notifications
     * @param fps
     */
    void setFps(int fps);

    /**
     * Returns the vertical resolution of the image
     * @return
     */
    int getResX();

    /**
     * Returns the horizontal resolution of the image
     * @return
     */
    int getResY();

    /**
     * Returns the current camera index
     * @return Camera code
     */
    int getCameraCode();
}
