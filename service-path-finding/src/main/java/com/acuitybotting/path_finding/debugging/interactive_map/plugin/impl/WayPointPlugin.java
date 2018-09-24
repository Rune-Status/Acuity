package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.common.utils.GsonUtil;
import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.query.AqlQuery;
import com.acuitybotting.db.arangodb.api.query.AqlResults;
import com.acuitybotting.db.arangodb.repositories.pathing.PathingRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WPEdge;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WPPath;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WPPathNode;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPoint;
import com.acuitybotting.path_finding.algorithms.wp.utils.GeoUtil;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.arangodb.ArangoCursor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WayPointPlugin extends Plugin {

    private PathingRepository wayPointRepository;

    private Executor executor = ExecutorUtil.newExecutorPool(1);
    private Location start, end;
    private WPPath wpPath;
    private Set<WPEdge> visibleEdges = new HashSet<>();

    private ScheduledThreadPoolExecutor threadPoolExecutor = ExecutorUtil.newScheduledExecutorPool(1);

    public WayPointPlugin(PathingRepository wayPointRepository) {
        this.wayPointRepository = wayPointRepository;
        threadPoolExecutor.scheduleWithFixedDelay(this::updateVisiblePoints, 1, 1, TimeUnit.SECONDS);
    }

    private void updateVisiblePoints(){
        try {

            Location base = getMapPanel().getPerspective().getBase();
            Location topRight = base.clone((int) getMapPanel().getPerspective().getTileWidth(), -(int) getMapPanel().getPerspective().getTileHeight());

            AqlQuery qp = Aql.query(
                    "LET nodes = WITHIN_RECTANGLE('WayPoint', @lat1, @long1, @lat2, @long2)\n" +
                            "FOR wp IN nodes\n" +
                            "    FILTER wp.plane == @plane\n" +
                            "    FOR v, e IN ANY wp GRAPH 'RsGraph1'\n" +
                            "      RETURN DISTINCT {'start': v, 'end': DOCUMENT(e._from)}"
            );

            qp.withParameter("lat1", GeoUtil.rsToGeo(base.getX()));
            qp.withParameter("long1", GeoUtil.rsToGeo(base.getY()));

            qp.withParameter("lat2", GeoUtil.rsToGeo(topRight.getX()));
            qp.withParameter("long2", GeoUtil.rsToGeo(topRight.getY()));

            qp.withParameter("plane", base.getPlane());

            visibleEdges = wayPointRepository.execute(qp).stream().map(s -> GsonUtil.getGson().fromJson(s, WPEdge.class)).collect(Collectors.toSet());
            getMapPanel().repaint();
        }
        catch (Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onPaint(Graphics2D graphics) {
        for (WPEdge visibleEdge : visibleEdges) {
            getPaintUtil().connectLocations(graphics, toLocation(visibleEdge.getStart()), toLocation(visibleEdge.getEnd()), Color.GREEN);
        }

        if (wpPath == null || wpPath.getPath() == null) return;
        WPPathNode last = null;
        for (WPPathNode wpPathNode : wpPath.getPath()) {
            getPaintUtil().markLocation(graphics, toLocation(wpPathNode.getNode()), Color.ORANGE);
            if (last != null) getPaintUtil().connectLocations(graphics, toLocation(last.getNode()), toLocation(wpPathNode.getNode()), Color.BLUE);
            last = wpPathNode;
        }
    }

    private Location toLocation(WayPoint node){
        return new Location(node.getX(), node.getY(), node.getPlane());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isControlDown()) {
            if (e.isShiftDown()) {
                end = getMapPanel().getMouseLocation();
                getMapPanel().repaint();
            } else {
                start = getMapPanel().getMouseLocation();
                getMapPanel().repaint();
            }

            if (start != null && end != null) {
                executor.execute(() -> {
                    AqlQuery query = Aql.query(
                                    "LET startNodes = (\n" +
                                    "    FOR n IN NEAR('WayPoint', @startLat, @startLong, 100) \n" +
                                    "    FILTER n.plane == @startPlane \n" +
                                    "    LIMIT 10\n" +
                                    "    RETURN n\n" +
                                    ")\n" +
                                    "\n" +
                                    "LET endNodes = (\n" +
                                    "    FOR n IN NEAR('WayPoint', @endLat, @endLong, 100) \n" +
                                    "    FILTER n.plane == @endPlane \n" +
                                    "    LIMIT 10\n" +
                                    "    RETURN n\n" +
                                    ")\n" +
                                    "\n" +
                                    "FOR start IN startNodes\n" +
                                    "    FOR end IN endNodes\n" +
                                    "        LET path = (\n" +
                                    "            FOR v, e IN OUTBOUND SHORTEST_PATH start TO end GRAPH 'RsGraph1'\n" +
                                    "            OPTIONS {'weightAttribute': 'weight'}\n" +
                                    "            RETURN {'node': v, \"edge\": e}\n" +
                                    "        )\n" +
                                    "        FILTER LENGTH(path) > 0\n" +
                                    "        LIMIT 1\n" +
                                    "        RETURN {'start': start, 'end': end, 'path': path}"
                    );

                    query.withParameter("startLat", GeoUtil.rsToGeo(start.getX()));
                    query.withParameter("startLong", GeoUtil.rsToGeo(start.getY()));
                    query.withParameter("startPlane", start.getPlane());

                    query.withParameter("endLat", GeoUtil.rsToGeo(end.getX()));
                    query.withParameter("endLong", GeoUtil.rsToGeo(end.getY()));
                    query.withParameter("endPlane", end.getPlane());

                    AqlResults<String> result = wayPointRepository.execute(query);
                    wpPath = result.getFirst().map(s -> GsonUtil.getGson().fromJson(s, WPPath.class)).orElse(null);
                    System.out.println("Path: " + wpPath);
                    getMapPanel().repaint();
                });
            }
        }
    }
}
