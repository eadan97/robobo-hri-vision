/*******************************************************************************
 *
 *   Copyright 2017 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2017 Luis Llamas <luis.llamas@mytechia.com>
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

package com.mytechia.robobo.framework.hri.vision.colorMeasurement;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;

import java.util.HashSet;

/**
 * Abstract class that manages listeners and remote control status
 */
public abstract class AColorMesaurementModule implements IColorMesaurementModule {
    private HashSet<IColorMesauredListener> listeners = new HashSet<>();
    private String TAG = "AColorMeasurement";
    protected RoboboManager m;

    protected IRemoteControlModule rcmodule = null;
    @Override
    public void suscribe(IColorMesauredListener listener){
        listeners.add(listener);
    }
    @Override
    public void unsuscribe(IColorMesauredListener listener){
        listeners.remove(listener);
    }

    /**
     * Notifies to the suscribed listaners that new color information is available
     * @param r Red channel intensity
     * @param g Green channel intensity
     * @param b Blue channel intensity
     */
    protected void notifyColorMesaured(int r, int g, int b){
        for (IColorMesauredListener l:listeners) {
            l.onColorMesaured(r,g,b);
        }

        if (rcmodule!=null) {

            Status status = new Status("MEASUREDCOLOR");
            status.putContents("R",r+"");
            status.putContents("G",g+"");
            status.putContents("B",b+"");
            rcmodule.postStatus(status);
        }

    }
}
