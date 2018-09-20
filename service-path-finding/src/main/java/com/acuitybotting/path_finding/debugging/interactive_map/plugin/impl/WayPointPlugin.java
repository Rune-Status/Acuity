package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.common.utils.GsonUtil;
import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.query.AqlQuery;
import com.acuitybotting.db.arangodb.api.query.AqlResults;
import com.acuitybotting.db.arangodb.repositories.pathing.PathingRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WPPath;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WPPathNode;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPoint;
import com.acuitybotting.path_finding.algorithms.wp.utils.GeoUtil;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.arangodb.ArangoCursor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executor;

public class WayPointPlugin extends Plugin {

    private PathingRepository wayPointRepository;

    private Executor executor = ExecutorUtil.newExecutorPool(1);
    private Location start, end;
    private WPPath wpPath;

    public WayPointPlugin(PathingRepository wayPointRepository) {
        this.wayPointRepository = wayPointRepository;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onPaint(Graphics2D graphics) {
        if (wpPath == null || wpPath.getPath() == null) return;
        for (WPPathNode wpPathNode : wpPath.getPath()) {
            getPaintUtil().markLocation(graphics, toLocation(wpPathNode.getNode()), Color.BLUE);
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

                    AqlQuery query = Aql.query("FOR start IN NEAR(\"WayPoint\", @startLat, @startLong, 100)\n" +
                            "    FILTER start.plane == @startPlane\n" +
                            "    FOR end IN NEAR(\"WayPoint\", @endLat, @endLong, 100) \n" +
                            "        FILTER end.plane == @endPlane\n" +
                            "        LET path = (\n" +
                            "            FOR v, e IN OUTBOUND SHORTEST_PATH start TO end GRAPH 'RsGraph1'\n" +
                            "            RETURN {'node': v, 'edge': e}\n" +
                            "        )\n" +
                            "        FILTER LENGTH(path) > 0\n" +
                            "        LIMIT 1\n" +
                            "        RETURN {'start': start, 'end': end, 'path': path}");

                    query.withParameter("startLat", GeoUtil.rsToGeo(start.getX()));
                    query.withParameter("startLong", GeoUtil.rsToGeo(start.getY()));
                    query.withParameter("startPlane", GeoUtil.rsToGeo(start.getPlane()));

                    query.withParameter("endLat", GeoUtil.rsToGeo(end.getX()));
                    query.withParameter("endLong", GeoUtil.rsToGeo(end.getY()));
                    query.withParameter("endPlane", GeoUtil.rsToGeo(end.getPlane()));

                    AqlResults<String> result = wayPointRepository.execute(query);
                    wpPath = result.getFirst().map(s -> GsonUtil.getGson().fromJson(s, WPPath.class)).orElse(null);
                    System.out.println("Path: " + wpPath);
                    getMapPanel().repaint();
                });
            }
        }
    }
}
