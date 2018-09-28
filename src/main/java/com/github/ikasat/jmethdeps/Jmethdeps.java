package com.github.ikasat.jmethdeps;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ikasat.jmethdeps.util.JarWalker;

import lombok.Getter;
import lombok.Setter;

public class Jmethdeps {
    public static final String VERSION = "0.1.0";

    @Getter @Setter
    private boolean duplicationAllowed;
    @Getter @Setter
    private OutputFormat outputFormat = OutputFormat.TEXT;

    public void run(List<File> jarFiles) {
        for (File jarFile : jarFiles) {
            processJar(jarFile);
        }
    }

    private void processJar(File jarFile) {
        try {
            JarWalker jarWalker = new JarWalker();
            final Map<String, List<String>> mapForJson = new HashMap<>();
            jarWalker.walk(jarFile, entry -> {
                String name = entry.getName();
                if (!name.endsWith(".class")) {
                    return;
                }
                DependencyParser depParser = new DependencyParser(jarFile, entry.getName());
                depParser.setDuplicationAllowed(isDuplicationAllowed());
                try {
                    List<Dependency> deps = depParser.parse();
                    switch (getOutputFormat()) {
                    case TEXT:
                        printAsText(deps);
                        break;
                    case JSON:
                        updateMapForJson(mapForJson, deps);
                        break;
                    default:
                        throw new RuntimeException("invalid output format: " + getOutputFormat());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (getOutputFormat() == OutputFormat.JSON) {
                try {
                    printAsJson(mapForJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMapForJson(Map<String, List<String>> map, List<Dependency> deps) {
        String cachedSource = null;
        List<String> cachedValue = null;
        for (Dependency dep : deps) {
            String source = dep.getSource();
            List<String> value;
            if (cachedSource != null && source.equals(cachedSource)) {
                value = cachedValue;
            } else {
                value = map.get(source);
                if (value == null) {
                    value = new ArrayList<>();
                    map.put(source, value);
                }
                cachedSource = source;
                cachedValue = value;
            }
            value.addAll(dep.getDestinations());
        }
    }

    private void printAsJson(Map<String, List<String>> map) throws Exception {
        Map<String, List<String>> finalizedMap;
        if (isDuplicationAllowed()) {
            finalizedMap = map;
        } else {
            finalizedMap = new HashMap<>(map);
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                Set<String> finalizedValueSet = new HashSet<>(entry.getValue());
                List<String> finalizedValues = new ArrayList<>(finalizedValueSet);
                Collections.sort(finalizedValues);
                finalizedMap.put(entry.getKey(), finalizedValues);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        mapper.writeValue(System.out, finalizedMap);
    }

    private void printAsText(List<Dependency> deps) {
        for (Dependency dep : deps) {
            for (String dest : dep.getDestinations()) {
                System.out.println(dep.getSource() + " " + dest);
            }
        }
    }

    public enum OutputFormat {
        TEXT,
        JSON,
    }
}
