/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.examples.mapping;

import javax.swing.SwingUtilities;

import ch.ethz.ether.model.IModel;
import ch.ethz.ether.view.IView.ViewType;

public final class MappingExample {
    public static void main(String[] args) {
        // Make sure everything runs on GUI thread...
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MappingExample();
            }
        });
    }

    /*
     * Creates a sample scene with 1 control view and 4 projector views, each
     * with 90 degrees rotation around the scene. Uses the sample calibration
     * model with a calibration rig of 0.5 units (~meters) edge lenght, and an
     * additional square 4 points at 0.8 units.
     */
    public MappingExample() {
        final MappingScene scene = new MappingScene();

        IModel model = new MappingTriangleModel(scene);

        scene.setModel(model);

        scene.addView(new MappingView(scene, 0, 10, 512, 512, ViewType.INTERACTIVE_VIEW, "View 0", 0.0f));
        scene.addView(new MappingView(scene, 530, 0, 400, 400, ViewType.MAPPED_VIEW, "View 1", 0.0f));
        //scene.addView(new MappingView(scene, 940, 0, 400, 400, ViewType.MAPPED_VIEW, "View 2", 90.0f));
        //scene.addView(new MappingView(scene, 530, 410, 400, 400, ViewType.MAPPED_VIEW, "View 3", 180.0f));
        //scene.addView(new MappingView(scene, 940, 410, 400, 400, ViewType.MAPPED_VIEW, "View 4", 270.0f));

        //try {
        //	new TUIO(scene);
        //} catch (Exception e) {
        //	e.printStackTrace();
        //}

        // XXX geometry server currently disabled
        // new GeometryServer(scene);
    }
}
