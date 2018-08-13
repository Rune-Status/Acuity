package com.acuitybotting.path_finding.service;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.utils.RabbitHub;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.ReverseAStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.enviroment.PathingEnviroment;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.edges.TeleportNode;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations.PlayerImplementation;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.Direction;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.rs.utils.RsMap;
import com.acuitybotting.path_finding.service.domain.PathRequest;
import com.acuitybotting.path_finding.service.domain.PathResult;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RsPlayer;
import com.acuitybotting.path_finding.web_processing.HpaWebService;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_QUEUE;
import static com.acuitybotting.path_finding.rs.domain.graph.TileNode.IGNORE_BLOCKED;

@Getter
@Setter
@Service
@Slf4j
public class HpaPathFindingService {

    private final XteaService xteaService;
    private final AStarService aStarService;
    private final HpaWebService hpaWebService;

    private PathResult lastResult;

    private HPAGraph graph;
    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public HpaPathFindingService(XteaService xteaService, AStarService aStarService, HpaWebService hpaWebService) {
        this.xteaService = xteaService;
        this.aStarService = aStarService;
        this.hpaWebService = hpaWebService;
    }

    public void loadRsMap() {
        log.info("Started loading RsMap this may take a few moments..");
        for (RegionMap regionMap : PathingEnviroment.loadFrom(PathingEnviroment.REGION_FLAGS, "flags", RegionMap[].class).orElse(null)) {
            RsEnvironment.getRsMap().getRegions().put(Integer.valueOf(regionMap.getKey()), regionMap);
        }
        log.info("Finished loading RsMap with {} regions.", RsEnvironment.getRsMap().getRegions().size());
    }

    public HPAGraph loadHpa(int version) {
        loadRsMap();
        graph = initGraph();
        hpaWebService.loadInto(graph, version, true);
        //todo graph.addCustomNodes();
        return graph;
    }

    public HPAGraph buildHpa(int version) {
        loadRsMap();
        graph = initGraph();
        graph.build();

        hpaWebService.deleteVersion(version);
        hpaWebService.save(graph, version);
        return graph;
    }

    public void consumeJobs() {
        try {
            loadHpa(1);

            RabbitHub rabbitHub = new RabbitHub();
            rabbitHub.auth(username, password);
            rabbitHub.start("APW");

            rabbitHub.createPool(5, channel ->
                    channel
                            .createQueue("acuitybotting.work.find-path-1", false)
                            .withListener(this::handleRequest)
                            .open(false));

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(MessageEvent messageEvent) {
        Gson outGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Gson inGson = new Gson();

        Message message = messageEvent.getMessage();
        PathRequest pathRequest = inGson.fromJson(message.getBody(), PathRequest.class);
        PathResult pathResult = new PathResult();

        try {
            log.info("Finding path. {}", pathRequest);
            pathResult = findPath(pathRequest.getStart(), pathRequest.getEnd(), pathRequest.getPlayer());
            List<? extends Edge> path = pathResult.getPath();
            log.info("Found path. {}", path);

            pathResult.setSubPaths(new HashMap<>());
            if (path != null) {
                for (Edge edge : path) {
                    if (edge instanceof HPAEdge) {
                        String pathKey = ((HPAEdge) edge).getPathKey();
                        List<Location> subPath = ((HPAEdge) edge).getPath();
                        if (pathKey != null && subPath != null) {
                            pathResult.getSubPaths().put(pathKey, subPath);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error during finding path. {}", e);
            pathResult.setError(e.getMessage());
        }

        String json = outGson.toJson(pathResult);
        log.info("Responding. {} {}", message.getAttributes().get(RESPONSE_QUEUE), json);
        try {
            messageEvent.getQueue().getChannel().buildResponse(message, json).send();
            messageEvent.getQueue().getChannel().acknowledge(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private boolean isBlocked(Location in) {
        RsMap rsMap = RsEnvironment.getRsMap();
        Integer flag = rsMap.getFlagAt(in).orElse(null);
        return flag == null || MapFlags.isBlocked(flag);
    }

    private Location adjustLocation(Location in) {
        if (in == null) return null;

        if (!isBlocked(in)) return in;

        Set<Location> closed = new HashSet<>();
        Queue<Location> open = new LinkedList<>();
        open.add(in);

        int attempts = 0;
        while (!open.isEmpty()) {
            if (attempts++ > 100) break;

            Location poll = open.poll();
            closed.add(poll);

            Collection<Edge> neighbors = new TileNode(poll).getOutgoingEdges(null, Collections.singletonMap(IGNORE_BLOCKED, poll));
            for (Edge neighbor : neighbors) {
                Location end = ((TileNode) neighbor.getEnd()).getLocation();
                if (!isBlocked(end)) return end;
            }

            for (Direction direction : Direction.values()) {
                Location end = poll.clone(direction.getXOff(), direction.getYOff());
                if (!closed.contains(end)) open.add(end);
            }
        }

        return in;
    }

    private Set<TerminatingNode> getTerminatingNodes(Collection<Location> locations, boolean end) {
        Set<TerminatingNode> nodes = new HashSet<>();
        for (Location endLocation : locations) {
            Location adjustedLocation = adjustLocation(endLocation);
            HPARegion regionContaining = graph.getRegionContaining(adjustedLocation);
            if (regionContaining == null) continue;
            nodes.add(new TerminatingNode(regionContaining, adjustedLocation, end));
        }
        return nodes;
    }

    private Edge getEdgeTo(Set<TerminatingNode> nodes, HPANode hpaNode) {
        for (TerminatingNode node : nodes) {
            Edge edgeTo = node.getEdgeTo(hpaNode);
            if (edgeTo != null) return edgeTo;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public PathResult findPath(Collection<Location> startLocations, Collection<Location> endLocations, RsPlayer rsPlayer) {
        PathResult pathResult = new PathResult();

        AStarImplementation astar = new ReverseAStarImplementation();
        astar.setArgs(Collections.singletonMap("player", rsPlayer == null ? null : new PlayerImplementation(rsPlayer)));

        Set<TerminatingNode> startNodes = getTerminatingNodes(startLocations, false);
        startNodes.stream().map(terminatingNode -> terminatingNode.getEdges().stream()).flatMap(Function.identity()).forEach(edge -> astar.addStartingNode(edge.getEnd()));
        Set<TerminatingNode> endNodes = getTerminatingNodes(endLocations, true);
        endNodes.stream().map(terminatingNode -> terminatingNode.getEdges().stream()).flatMap(Function.identity()).forEach(edge -> astar.addDestinationNode(edge.getStart()));

        for (CustomEdgeData customEdgeData : TeleportNode.getEdges()) {
            astar.getGlobalEdges().add(customEdgeData.toEdge(graph));
        }

        List<Edge> hpaPath = null;
        for (Node node : astar.getStartingNodes()) {
            if (astar.getDestinationNodes().contains(node)) {
                hpaPath = new ArrayList<>();
                hpaPath.add(new HPAEdge((HPANode) node, (HPANode) node));
                break;
            }
        }

        if (hpaPath == null) hpaPath = (List<Edge>) astar.findPath(new LocateableHeuristic()).orElse(null);
        pathResult.setAStarImplementation(astar);

        if (hpaPath != null) {
            Edge edgeTo = getEdgeTo(startNodes, (HPANode) hpaPath.get(0).getStart());
            if (edgeTo != null) hpaPath.add(0, edgeTo);

            edgeTo = getEdgeTo(endNodes, (HPANode) hpaPath.get(hpaPath.size() - 1).getEnd());
            if (edgeTo != null) hpaPath.add(edgeTo);
        }

        pathResult.setPath(hpaPath);
        lastResult = pathResult;

        return pathResult;
    }

    private PathFindingSupplier getPathFindingSupplier() {
        return new PathFindingSupplier() {
            @Override
            public Optional<List<? extends Edge>> findPath(Location start, Location end, Predicate<Edge> predicate, boolean ignoreStartBlocked) {
                return aStarService.findPath(
                        new LocateableHeuristic(),
                        RsEnvironment.getRsMap().getNode(start),
                        RsEnvironment.getRsMap().getNode(end),
                        predicate,
                        ignoreStartBlocked ? Collections.singletonMap(IGNORE_BLOCKED, start) : Collections.emptyMap()
                );
            }

            @Override
            public boolean isDirectlyConnected(Location start, Location end) {
                TileNode sNode = RsEnvironment.getRsMap().getNode(start);
                TileNode endNode = RsEnvironment.getRsMap().getNode(end);
                return sNode.getOutgoingEdges().stream().anyMatch(edge -> edge.getEnd().equals(endNode));
            }
        };
    }

    private HPAGraph initGraph() {
        RsEnvironment.getRsMap().calculateBounds();

        HPAGraph graph = new HPAGraph();
        graph.init(
                new Location(RsEnvironment.getRsMap().getLowestX(), RsEnvironment.getRsMap().getLowestY(), 0),
                new Location(RsEnvironment.getRsMap().getHighestX(), RsEnvironment.getRsMap().getHighestY(), 3),
                15,
                15
        );
        graph.setPathFindingSupplier(getPathFindingSupplier());
        return graph;
    }
}
