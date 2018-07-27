package com.acuitybotting.path_finding;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.utils.ArangoUtils;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.HpaPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PositionPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;
import com.acuitybotting.path_finding.enviroment.PathingEnviroment;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.service.HpaPathFindingService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final HpaPathFindingService hpaPathFindingService;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, HpaPathFindingService hpaPathFindingService) {
        this.webImageProcessingService = webImageProcessingService;
        this.hpaPathFindingService = hpaPathFindingService;
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
        MapFrame mapFrame = new MapFrame();
        mapFrame.getMapPanel().addPlugin(new PositionPlugin());
        mapFrame.show();
        return mapFrame;
    }

    private void loadXteasIn() throws IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(
                Files.readAllLines(new File(PathingEnviroment.BASE , "xteas.json").toPath()).stream().collect(Collectors.joining("")),
                JsonObject.class
        );

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonArray xteas = entry.getValue().getAsJsonArray();
            for (JsonElement xteaJson : xteas) {
                Xtea xtea = gson.fromJson(xteaJson, Xtea.class);
                xtea.setId(null);
                hpaPathFindingService.getXteaService().getXteaRepository().save(xtea);
            }
        }
    }

    @Override
    public void run(String... args) {
        try {
            dump();
            openUi().getMapPanel().addPlugin(new HpaPlugin(hpaPathFindingService.loadHpa(1)));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
