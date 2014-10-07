/*
 * Copyright (c) 2014, FHNW (Simon Schubiger)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.fhnw.ether.examples.quaternion;

import java.io.IOException;

import javax.swing.SwingUtilities;

import ch.fhnw.ether.camera.Camera;
import ch.fhnw.ether.formats.obj.parser.OBJReader;
import ch.fhnw.ether.scene.SimpleScene;

public class QuaternionExample {
	 public static void main(String[] args) {
	        // Make sure everything runs on GUI thread...
	        SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                try {
						new QuaternionExample();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        });
	    }

	    public QuaternionExample() throws IOException {
	        final QuaternionController controller = new QuaternionController();
	        Camera camera = new Camera();
	 
	        SimpleScene s = new SimpleScene();
	        new OBJReader(getClass().getResource("fhnw.obj")).getMeshes().forEach((x) -> {s.addMesh(x);} );
	        controller.setScene(s);
	        
	        controller.addView(new QuaternionView(controller, 0, 10, 512, 512, "Quaternion View", camera));
	    }
}
