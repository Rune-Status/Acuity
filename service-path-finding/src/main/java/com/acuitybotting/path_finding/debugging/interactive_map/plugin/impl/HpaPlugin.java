package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.service.HpaPathFindingService;
import com.acuitybotting.path_finding.service.domain.PathResult;
import org.springframework.expression.spel.ast.Projection;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class HpaPlugin extends Plugin {

    private HPAGraph graph;

    private Location start, end;

    private HPARegion startRegion, endRegion;
    private TerminatingNode startNode, endNode;

    private PathResult pathResult;
    private Executor executor = ExecutorUtil.newExecutorPool(1);

    private HpaPathFindingService pathFindingService;

    private Color[] nodeColorings = new Color[]{Color.BLUE, Color.RED, Color.CYAN};

    public HpaPlugin(HPAGraph hpaGraph) {
        this.graph = hpaGraph;
    }

    public void setGraph(HPAGraph graph) {
        this.graph = graph;
    }

    @Override
    public void onPaint(Graphics2D graphics) {
        if (graph == null) return;

        if (startNode != null){
            for (Edge edge : startNode.getNeighbors()) {
                if (edge instanceof HPAEdge){
                    if (((HPAEdge) edge).getType() == EdgeType.CUSTOM){
                        getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.BLACK);
                    }
                }
            }
        }

        for (HPARegion HPARegion : graph.getRegions().values()) {
            for (HPANode hpaNode : HPARegion.getNodes().values()) {
                getPaintUtil().markLocation(graphics, hpaNode.getLocation(), nodeColorings[hpaNode.getType()]);
            }

            for (HPANode hpaNode : HPARegion.getNodes().values()) {
                for (Edge edge : hpaNode.getNeighbors()) {
                    if (edge instanceof HPAEdge) {
                        getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.BLUE);
                    }
                }
            }
        }

        Location click = getMapPanel().getMouseLocation();
        HPARegion clickRegion = graph.getRegionContaining(click);
        if (clickRegion != null){
            Location l1 = clickRegion.getRoot().clone();
            l1.setPlane(click.getPlane());
            Location l2 = l1.clone(clickRegion.getWidth() - 1, 0);
            Location l3 = l1.clone(clickRegion.getWidth() - 1 , clickRegion.getHeight() - 1);
            Location l4 = l1.clone(0, clickRegion.getHeight() -  1);

            getPaintUtil().connectLocations(graphics, l1, l2, Color.MAGENTA);
            getPaintUtil().connectLocations(graphics, l2, l3, Color.MAGENTA);
            getPaintUtil().connectLocations(graphics, l3, l4, Color.MAGENTA);
            getPaintUtil().connectLocations(graphics, l4, l1, Color.MAGENTA);

            List<LocationPair> externalConnections = graph.findExternalConnections(clickRegion, graph.getPathFindingSupplier());
            for (LocationPair externalConnection : externalConnections) {
                if (externalConnection.getStart().getPlane() != click.getPlane()) continue;
                getPaintUtil().connectLocations(graphics, externalConnection.getStart(), externalConnection.getEnd(), Color.BLUE);
            }

            HPANode hpaNode = clickRegion.getNodes().get(click);
            if (hpaNode != null){
                for (Edge edge : hpaNode.getNeighbors()) {
                    getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.MAGENTA);
                }
            }
        }


        if (pathResult != null){
            if (pathResult.getAStarImplementation() != null){
                for (Node node : pathResult.getAStarImplementation().getCostCache().keySet()) {
                    getPaintUtil().markLocation(graphics, node, Color.ORANGE);
                }
            }

            if (pathResult.getPath() != null) {
                for (Edge edge : pathResult.getPath()) {
                    getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.MAGENTA);
                }
            }
        }

        if (startNode != null) getPaintUtil().markLocation(graphics, startNode, Color.RED);
        if (endNode != null) getPaintUtil().markLocation(graphics, endNode, Color.GREEN);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isControlDown()) {
            if (e.isShiftDown()) {
                end = getMapPanel().getMouseLocation();
                endRegion = graph.getRegionContaining(end);
                if (endRegion != null) {
                    endNode = new TerminatingNode(endRegion, end, true);
                }
                getMapPanel().repaint();
            } else {
                start = getMapPanel().getMouseLocation();
                startRegion = graph.getRegionContaining(start);
                if (startRegion != null) {
                    startNode = new TerminatingNode(startRegion, start, false);
                }

                getMapPanel().repaint();
            }

            if (startNode != null && endNode != null) {
                executor.execute(() -> {
                    try {
                        pathResult = pathFindingService.findPath(start, end, null);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    getMapPanel().repaint();
                });
            }
        }
    }

    public HpaPlugin setPathFindingService(HpaPathFindingService pathFindingService) {
        this.pathFindingService = pathFindingService;
        return this;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onClose() {

    }
}
