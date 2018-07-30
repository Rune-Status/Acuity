package com.acuitybotting.path_finding.service;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.ChannelListenerAdapter;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.ClientListenerAdapter;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.GraphState;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.enviroment.PathingEnviroment;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.edges.TeleportNode;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations.PlayerImplementation;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.*;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_QUEUE;
import static com.acuitybotting.path_finding.rs.domain.graph.TileNode.IGNORE_BLOCKED;

@Getter
@Setter
@Service
@Slf4j
@PropertySource("classpath:general-worker-rabbit.credentials")
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

            Gson outGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Gson inGson = new Gson();

            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);
            rabbitClient.getListeners().add(new ClientListenerAdapter() {
                @Override
                public void onConnect(MessagingClient client) {
                    MessagingChannel channel = client.createChannel();
                    channel.getListeners().add(new ChannelListenerAdapter() {
                        @Override
                        public void onConnect(MessagingChannel channel) {

                            try {
                                channel.getQueue("acuitybotting.work.find-path-test")
                                        .withListener(messageEvent -> {
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
                                                channel.respond(message, json);
                                                channel.acknowledge(message);
                                            } catch (MessagingException e) {
                                                e.printStackTrace();
                                            }
                                        })
                                        .consume(false);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }

                            try {
                                channel.getQueue("acuitybotting.work.xtea-dump")
                                        .withListener(messageEvent -> {
                                            String routing = messageEvent.getRouting();
                                            Message message = messageEvent.getMessage();

                                            if (routing.endsWith("xtea-dump")) {
                                                int[] emptyKeys = {0, 0, 0, 0};
                                                Xtea[] xteas = inGson.fromJson(message.getBody(), Xtea[].class);
                                                for (Xtea xtea : xteas) {
                                                    if (xtea.getKeys() == null || Arrays.equals(xtea.getKeys(), emptyKeys))
                                                        continue;
                                                    xteaService.getXteaRepository().save(xtea);
                                                    log.info("Saved Xtea Key {}.", xtea);
                                                }
                                                try {
                                                    channel.acknowledge(message);
                                                } catch (MessagingException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .consume(false);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onShutdown(MessagingChannel channel, Throwable cause) {
                            channel.connect();
                        }

                    });

                    channel.connect();
                }
            });

            rabbitClient.connect("APW_" + UUID.randomUUID().toString());
        } catch (Throwable e) {
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

            Collection<Edge> neighbors = new TileNode(poll).getNeighbors(null, Collections.singletonMap(IGNORE_BLOCKED, poll));
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

    @SuppressWarnings("unchecked")
    public PathResult findPath(Location startLocation, Location endLocation, RsPlayer rsPlayer) {
        startLocation = adjustLocation(startLocation);
        endLocation = adjustLocation(endLocation);

        HPARegion startRegion = graph.getRegionContaining(startLocation);
        HPARegion endRegion = graph.getRegionContaining(endLocation);

        Objects.requireNonNull(startRegion);
        Objects.requireNonNull(endRegion);

        PathResult pathResult = new PathResult();

        if (startRegion.equals(endRegion)) {
            List<Edge> internalPath = (List<Edge>) graph.findInternalPath(startLocation, endLocation, startRegion, true);
            if (internalPath != null) {
                pathResult.setPath(internalPath);
                return pathResult;
            }
        }

        Player player = rsPlayer == null ? null : new PlayerImplementation(rsPlayer);
        TerminatingNode startNode = new TerminatingNode(startRegion, startLocation, player, false);
        TerminatingNode endNode = new TerminatingNode(endRegion, endLocation, player, true);


        AStarImplementation astar = new AStarImplementation();
        astar.setArgs(Collections.singletonMap("player", player));

        for (CustomEdgeData customEdgeData : TeleportNode.getEdges()) {
            HPARegion region = graph.getRegionContaining(customEdgeData.getEnd());
            if (region == null) continue;
            HPANode teleportEnd = region.getNodes().get(customEdgeData.getEnd());
            if (teleportEnd == null) continue;
            HPAEdge hpaEdge = new CustomEdge(null, teleportEnd).setCost(customEdgeData.getCost()).setType(EdgeType.CUSTOM).setCustomEdgeData(customEdgeData);
            astar.getGlobalEdges().add(hpaEdge);
            if (hpaEdge.evaluate(new GraphState(), astar.getArgs())) startNode.getEdges().add(hpaEdge.copyWithStart(startNode));
        }

        List<Edge> hpaPath = null;

        out:
        for (Edge se : startNode.getEdges()) {
            for (Edge ee : endNode.getEdges()) {
                if (se.getEnd().equals(ee.getStart())) {
                    hpaPath = new ArrayList<>();
                    hpaPath.add(se);
                    hpaPath.add(ee);
                    break out;
                }
            }
        }

        if (hpaPath == null) {
            startNode.getEdges().forEach(edge -> astar.addStartingNode(edge.getEnd()));
            endNode.getEdges().forEach(edge -> astar.addDestinationNode(edge.getStart()));
            hpaPath = (List<Edge>) astar.findPath(new LocateableHeuristic()).orElse(null);
            if (hpaPath != null) {
                Edge edgeTo = startNode.getEdgeTo((HPANode) hpaPath.get(0).getStart());
                if (edgeTo != null) hpaPath.add(0, edgeTo);

                edgeTo = endNode.getEdgeTo((HPANode) hpaPath.get(hpaPath.size() - 1).getEnd());
                if (edgeTo != null) hpaPath.add(edgeTo);
            }

            pathResult.setAStarImplementation(astar);
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
                return sNode.getNeighbors().stream().anyMatch(edge -> edge.getEnd().equals(endNode));
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
