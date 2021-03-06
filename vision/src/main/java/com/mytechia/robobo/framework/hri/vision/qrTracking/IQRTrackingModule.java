/*******************************************************************************
 *
 *   Copyright 2018 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2018 Luis Llamas <luis.llamas@mytechia.com>
 *
 *   This file is part of Robobo Vision Modules.
 *
 *   Robobo Vision Modules is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Remote Control Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Vision Modules.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/
package com.mytechia.robobo.framework.hri.vision.qrTracking;

import com.mytechia.robobo.framework.IModule;


public interface IQRTrackingModule extends IModule {
    /**
     * Suscribes a listener to the qe notifications
     * @param listener The listener to be added
     */
    void suscribe(IQRListener listener);
    /**
     * Unsuscribes a listener from the qr notifications
     * @param listener The listener to be removed
     */
    void unsuscribe(IQRListener listener);

    /**
     * Set the number of frames passed without detection to consider a QR as lost
     * @param threshold Number of frames
     */
    void setLostThreshold(int threshold);
}
