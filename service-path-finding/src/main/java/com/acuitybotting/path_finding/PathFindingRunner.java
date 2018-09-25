package com.acuitybotting.path_finding;

import com.acuitybotting.db.arangodb.repositories.pathing.PathingRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointRepository;
import com.acuitybotting.db.dropbox.DropboxService;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PositionPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.WayPointPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;
import com.acuitybotting.path_finding.enviroment.PathingEnviroment;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.service.HpaPathFindingService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final HpaPathFindingService hpaPathFindingService;
    private final DropboxService dropboxService;
    private final PathingRepository wayPointRepository;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, HpaPathFindingService hpaPathFindingService, DropboxService dropboxService, PathingRepository wayPointRepository) {
        this.webImageProcessingService = webImageProcessingService;
        this.hpaPathFindingService = hpaPathFindingService;
        this.dropboxService = dropboxService;
        this.wayPointRepository = wayPointRepository;
    }

    private void exportXteas() {
        hpaPathFindingService.getXteaService().exportXteasGreaterThanRev(171, PathingEnviroment.XTEAS);
    }

    private void dump() {
        hpaPathFindingService.getXteaService().saveRegionMapsFromAfter(171);
        PathingEnviroment.save(PathingEnviroment.JSON, "banks", hpaPathFindingService.getXteaService().getBanks());
        webImageProcessingService.saveImagesFromRegionMaps(RsEnvironment.getRsMap().getRegions().values(), PathingEnviroment.ACUITY_RENDERINGS);
    }

    private MapFrame openUi() throws Exception {
        AStarImplementation.debugMode = true;
        MapFrame mapFrame = new MapFrame();
        mapFrame.getMapPanel().addPlugin(new PositionPlugin());
        mapFrame.getMapPanel().addPlugin(new WayPointPlugin(wayPointRepository));
        mapFrame.show();
        return mapFrame;
    }

    @Override
    public void run(String... args) {
        try {
            //wayPointRepository.getArangoDbService().getDriver().db("Pathing-1").createAqlFunction("RS::JS_NODE_EVAUL", "function evaluateQuery(query, rsAccount, node, edge) {return eval(query);}", null);

            //PathingEnviroment.downloadFromDropbox(dropboxService, 1);
            openUi();
            //hpaPathFindingService.consumeJobs();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
