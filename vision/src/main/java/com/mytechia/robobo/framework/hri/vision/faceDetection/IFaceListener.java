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

package com.mytechia.robobo.framework.hri.vision.faceDetection;

import android.graphics.PointF;

/**
 * Listener interface for the face detector
 */
public interface IFaceListener {
    /**
     * Called when a face is detected
     * @param faceCoords The coordinates of the center of the face
     * @param eyesDistance The distance between eyes
     */
    void onFaceDetected(PointF faceCoords, float eyesDistance);

    /**
     * Called when a face appears fo thye first time
     * @param faceCoords The coordinates of the center of the face
     * @param eyesDistance The distance between eyes
     */
    void onFaceAppear(PointF faceCoords, float eyesDistance);

    /**
     * Called when a face disappears
     */
    void onFaceDissapear();
}
