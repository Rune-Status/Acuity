package com.acuitybotting.discord.bot.services.rspeer;

import com.arangodb.springframework.core.ArangoOperations;
import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/7/2018.
 */
public class RsPeerUserReportGenerator {

    public static String mapToString(Map<String, Set<String>> report) {
        StringJoiner builder = new StringJoiner("\n");
        report.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> {
                    builder.add(entry.getKey() + ":");
                    for (String value : entry.getValue()) {
                        builder.add("\t" + value);
                    }
                });
        return builder.toString();
    }

    public static Map<String, Set<String>> generateAll(ArangoOperations arangoOperations) {
        Map<String, Set<String>> entries = new HashMap<>();
        buildReport(entries, generate(arangoOperations, "subDocument.email"));
        buildReport(entries, generate(arangoOperations, "subDocument.ign"));
        buildReport(entries, generate(arangoOperations, "headers.peerHost"));
        return entries;
    }

    private static void buildReport(Map<String, Set<String>> report, List<UserMatch> matches) {
        for (UserMatch match : matches) {
            for (String principalId : match.getPrincipals()) {
                Set<String> entries = report.computeIfAbsent(principalId, s -> new HashSet<>());
                entries.add("shares '" + match.getMatch() + "' with " + Arrays.stream(match.getPrincipals()).filter(s -> !s.equals(principalId)).collect(Collectors.joining(", ")));
            }
        }
    }

    private static List<UserMatch> generate(ArangoOperations arangoOperations, String query) {
        String reportQuery =
                "LET subs = (\n" +
                        "    FOR d in RabbitDocument\n" +
                        "        FILTER d.{QUERY} != NULL\n" +
                        "        LET sub = {'match' : d.{QUERY}, 'principalId': d.principalId}\n" +
                        "        RETURN DISTINCT sub\n" +
                        ")\n" +
                        "\n" +
                        "LET results = (\n" +
                        "    FOR sub IN subs\n" +
                        "        COLLECT match = sub.match INTO principalByMatch\n" +
                        "        RETURN { \n" +
                        "            match,\n" +
                        "            principals : principalByMatch[*].sub.principalId\n" +
                        "        }\n" +
                        ")\n" +
                        "\n" +
                        "FOR result in results\n" +
                        "    FILTER LENGTH(result.principals) > 1\n" +
                        "    RETURN result";

        String injectedQuery = reportQuery.replaceAll("\\{QUERY}", query);
        return arangoOperations.query(injectedQuery, null, null, UserMatch.class).asListRemaining();
    }

    @Getter
    @ToString
    public static class UserMatch {
        private String match;
        private String[] principals;
    }
}
