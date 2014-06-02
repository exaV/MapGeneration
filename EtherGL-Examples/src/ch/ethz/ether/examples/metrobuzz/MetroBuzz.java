/*
 * Copyright (c) 2013 - 2014, ETH Zurich & FHNW (Stefan Muller Arisona)
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
 *  Neither the name of ETH Zurich nor the names of its contributors may be
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

package ch.ethz.ether.examples.metrobuzz;

import javax.swing.SwingUtilities;

import ch.ethz.ether.examples.metrobuzz.io.matsim.Loader;
import ch.ethz.ether.examples.metrobuzz.model.Model;
import ch.ethz.ether.examples.metrobuzz.scene.Scene;
import ch.ethz.ether.examples.metrobuzz.scene.View;

public class MetroBuzz {
    public static void main(final String[] args) {
        // Make sure everything runs on GUI thread...
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MetroBuzz(args);
            }
        });
    }

	public MetroBuzz(String[] args) {
		Model model = new Model();
		System.out.println("Loading Data");
		Loader.load(model, "/Users/radar/Desktop/sioux_osm", 100 /*Integer.MAX_VALUE*/);
		System.out.println("Creating Geometry");
		model.getAgentGeometries();
		System.out.println("Done.");

		Model.printAgent(model.getAgents().get(0));
		Model.printAgent(model.getAgents().get(1));
		Model.printAgent(model.getAgents().get(2));
		
		Scene scene = new Scene();
		scene.setModel(model);
		scene.addView(new View(scene, 0, 10, 512, 512));
		//scene.addView(new View(scene, 512, 10, 512, 512));
	}

}
