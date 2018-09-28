package com.github.ikasat.jmethdeps;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import lombok.Getter;
import lombok.Setter;

public class DependencyParser {
    @Getter
    private File filePath;
    @Getter
    private String classPath;
    @Getter @Setter
    private boolean duplicationAllowed;
    @Getter @Setter
    private Set<String> opSet;
    
    public DependencyParser(File filePath, String classPath) {
        this.filePath = filePath;
        this.classPath = classPath;
        this.opSet = makeDefaultOpSet();
    }
    
    private Set<String> makeDefaultOpSet() {
        Set<String> opSet = new HashSet<>();
        opSet.add("invokevirtual");
        opSet.add("invokeinterface");
        opSet.add("invokestatic");
        opSet.add("invokespecial");
        return opSet;
    }
    
    public List<Dependency> parse() throws Exception {
        List<Dependency> deps = new ArrayList<>();
        JavaClass javaClass = new ClassParser(getFilePath().getAbsolutePath(), getClassPath()).parse();
        for (Method method : javaClass.getMethods()) {
            List<String> dests;
            if (isDuplicationAllowed()) {
                dests = makeDestinationList(method);
            } else {
                dests = new ArrayList<>(makeDestinationSet(method));
                Collections.sort(dests);
            }
            deps.add(Dependency.builder()
                    .source(javaClass.getClassName() + "." + method.getName())
                    .destinations(dests)
                    .build());
        }
        return deps;
    }
    
    private List<String> makeDestinationList(Method method) {
        Code code = method.getCode();
        if (code == null) {
            return Collections.emptyList();
        }
        List<String> dests = new ArrayList<>();
        for (String line : code.toString().split("\n")) {
            String dest = parseLineOfCode(line);
            if (dest != null) {
                dests.add(dest);
            }
        }
        return dests;
    }

    private Set<String> makeDestinationSet(Method method) {
        Code code = method.getCode();
        if (code == null) {
            return Collections.emptySet();
        }
        Set<String> dests = new HashSet<>();
        for (String line : code.toString().split("\n")) {
            String dest = parseLineOfCode(line);
            if (dest != null) {
                dests.add(dest);
            }
        }
        return dests;
    }
    
    private String parseLineOfCode(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length <= 2) {
            return null;
        }
        if (getOpSet().contains(parts[1])) {
            return parts[2];
        }
        return null;
    }
}
