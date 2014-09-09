/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
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
 */package ch.fhnw.ether.examples.metrobuzz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.fhnw.ether.examples.metrobuzz.controller.MetroBuzzController;
import ch.fhnw.ether.examples.metrobuzz.controller.ModelRenderState;
import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.model.GenericMesh;
import ch.fhnw.ether.model.IGeometry;
import ch.fhnw.ether.model.IModel;

public class Model implements IModel {
	private static final float[] ACTIVITY_COLOR = { 1f, 0f, 0f, 0.2f };
	private static final float[] TRIP_COLOR = { 0f, 1f, 0f, 0.2f };

	private final MetroBuzzController controller;
	
	private final List<Node> nodes = new ArrayList<>();
	private final List<Link> links = new ArrayList<>();

	private final Map<String, Node> idToNode = new HashMap<>();
	private final Map<String, Link> idToLink = new HashMap<>();

	private final List<Agent> agents = new ArrayList<>();

	private BoundingBox bounds = null;

	private List<GenericMesh> agentGeometries;
	private GenericMesh networkGeometry;
	private ModelRenderState renderState;

	public Model(MetroBuzzController controller) {
		this.controller = controller;
	}
	
	@Override
	public MetroBuzzController getController() {
		return controller;
	}

	@Override
	public BoundingBox getBounds() {
		if (bounds == null) {
			bounds = new BoundingBox();
			for (IGeometry geometry : getGeometries()) {
				bounds.add(geometry.getBounds());
			}
		}
		return bounds;
	}

	@Override
	public List<? extends IGeometry> getGeometries() {
		if (agentGeometries == null) {
			createGeometries();
		}
		return agentGeometries;
	}

	public GenericMesh getNetworkGeometry() {
		if (networkGeometry == null) {
			createGeometries();
		}
		return networkGeometry;
	}

	public List<GenericMesh> getAgentGeometries() {
		if (agentGeometries == null) {
			createGeometries();
		}
		return agentGeometries;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public Node getNode(String id) {
		return idToNode.get(id);
	}

	public void addNode(Node node) {
		nodes.add(node);
		idToNode.put(node.getId(), node);
		bounds = null;
	}

	public List<Link> getLinks() {
		return links;
	}

	public Link getLink(String id) {
		return idToLink.get(id);
	}

	public void addLink(Link edge) {
		links.add(edge);
		idToLink.put(edge.getId(), edge);
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public void addAgent(Agent agent) {
		agents.add(agent);
		bounds = null;
	}

	/**
	 * Normalize the model into a [-1,1][-1,1] area (we don't deal with time here)
	 */
	public void normalize() {
		// first determine overall scale of model, so we can normalize
		BoundingBox bounds = new BoundingBox();
		for (Node node : nodes) {
			bounds.add(node.getX(), node.getY(), 0);
		}

		// update node "coordinates" to [-1,1] (scale uniformly)
		for (Node node : nodes) {
			float scale = Math.max(bounds.getExtentX(), bounds.getExtentY());
			node.setX((node.getX() - bounds.getCenterX()) / scale);
			node.setY((node.getY() - bounds.getCenterY()) / scale);
		}
	}

	public static void printAgent(Agent agent) {
		System.out.println(agent);
		for (Activity activity : agent.getActivities()) {
			System.out.println(activity);
			Trip trip = activity.getTrip();
			if (trip != null) {
				System.out.println(trip);
				for (Link link : trip.getLinks()) {
					System.out.println("- " + link);
				}
			}
		}
		System.out.println();
	}

	private void createGeometries() {
		agentGeometries = new ArrayList<>();

		// add network
		int i = 0;
		int j = 0;

		float[] networkNodes = new float[nodes.size() * 3];
		for (Node node : nodes) {
			networkNodes[i++] = node.getX();
			networkNodes[i++] = node.getY();
			networkNodes[i++] = 0;
		}

		i = 0;
		float[] networkEdges = new float[links.size() * 6];
		for (Link link : links) {
			networkEdges[i++] = link.getFromNode().getX();
			networkEdges[i++] = link.getFromNode().getY();
			networkEdges[i++] = 0;
			networkEdges[i++] = link.getToNode().getX();
			networkEdges[i++] = link.getToNode().getY();
			networkEdges[i++] = 0;
		}

		networkGeometry = new GenericMesh();
		networkGeometry.setPoints(networkNodes);
		networkGeometry.setLines(networkEdges);


		// add agents (count number of paths first, then add);

		for (Agent agent : agents) {
			int numPaths = 0;
			for (Activity activity : agent.getActivities()) {
				// one path for activity + one for each link
				numPaths++;
				Trip trip = activity.getTrip();
				if (trip == null)
					continue;
				switch (trip.getMode()) {
				case WALK:
				case TRANSIT_WALK:
					numPaths++;
				default:
					numPaths += trip.getLinks().size();
				}
			}
			float[] agentEdges = new float[6 * numPaths];
			float[] agentColors = new float[8 * numPaths];

			i = 0;
			j = 0;
			for (Activity activity : agent.getActivities()) {
				agentEdges[i++] = activity.getLocation().getX();
				agentEdges[i++] = activity.getLocation().getY();
				agentEdges[i++] = normTime(activity.getStartTime());
				agentEdges[i++] = activity.getLocation().getX();
				agentEdges[i++] = activity.getLocation().getY();
				agentEdges[i++] = normTime(activity.getEndTime());
				agentColors[j++] = ACTIVITY_COLOR[0];
				agentColors[j++] = ACTIVITY_COLOR[1];
				agentColors[j++] = ACTIVITY_COLOR[2];
				agentColors[j++] = ACTIVITY_COLOR[3];
				agentColors[j++] = ACTIVITY_COLOR[0];
				agentColors[j++] = ACTIVITY_COLOR[1];
				agentColors[j++] = ACTIVITY_COLOR[2];
				agentColors[j++] = ACTIVITY_COLOR[3];
				Trip trip = activity.getTrip();
				if (trip == null)
					continue;

				// XXX: note there's some weirdness with the links, we're currently just using fromNode from each link
				switch (trip.getMode()) {
				case WALK:
				case TRANSIT_WALK: {
					Link startLink = trip.getLinks().get(0);
					Link endLink = trip.getLinks().get(1);
					agentEdges[i++] = startLink.getFromNode().getX();
					agentEdges[i++] = startLink.getFromNode().getY();
					agentEdges[i++] = normTime(trip.getStartTime());
					agentEdges[i++] = endLink.getFromNode().getX();
					agentEdges[i++] = endLink.getFromNode().getY();
					agentEdges[i++] = normTime(trip.getEndTime());
					agentColors[j++] = TRIP_COLOR[0];
					agentColors[j++] = TRIP_COLOR[1];
					agentColors[j++] = TRIP_COLOR[2];
					agentColors[j++] = TRIP_COLOR[3];
					agentColors[j++] = TRIP_COLOR[0];
					agentColors[j++] = TRIP_COLOR[1];
					agentColors[j++] = TRIP_COLOR[2];
					agentColors[j++] = TRIP_COLOR[3];
				}
					break;
				default: {
					List<Link> links = trip.getLinks();
					if (links.size() == 1) {
						System.out.println("only one link, skipping");
						continue;
					}
					float startTime = trip.getStartTime();
					float deltaTime = (trip.getEndTime() - trip.getStartTime()) / (trip.getLinks().size() - 1);
					for (int index = 0; index < links.size(); ++index) {
						Link link = links.get(index);
						agentEdges[i++] = link.getFromNode().getX();
						agentEdges[i++] = link.getFromNode().getY();
						agentEdges[i++] = normTime(startTime);
						agentColors[j++] = TRIP_COLOR[0];
						agentColors[j++] = TRIP_COLOR[1];
						agentColors[j++] = TRIP_COLOR[2];
						agentColors[j++] = TRIP_COLOR[3];
						if (index > 0 && index < links.size() - 1) {
							agentEdges[i++] = link.getFromNode().getX();
							agentEdges[i++] = link.getFromNode().getY();
							agentEdges[i++] = normTime(startTime);
							agentColors[j++] = TRIP_COLOR[0];
							agentColors[j++] = TRIP_COLOR[1];
							agentColors[j++] = TRIP_COLOR[2];
							agentColors[j++] = TRIP_COLOR[3];
						}
						startTime += deltaTime;
					}
				}
				}
			}
			GenericMesh geometry = new GenericMesh();
			geometry.setLines(agentEdges, agentColors);
			agentGeometries.add(geometry);
		}
		
		renderState = new ModelRenderState(this);
		renderState.updateNetwork();
		renderState.updateAgents();
	}

	private static float normTime(float time) {
		return time / (24 * 60 * 60);
	}
}
