package com.github.ikasat.jmethdeps.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import lombok.Getter;
import lombok.Setter;

public class JarWalker {
    @Getter @Setter
    private boolean directoryAllowed;
    @Getter @Setter
    private int maxDepth = -1;

    public void walk(File filePath, Consumer<JarEntry> consumer) throws Exception {
        walk(filePath, consumer, 0);
    }
    
    private void walk(File filePath, Consumer<JarEntry> consumer, int depth) throws FileNotFoundException, IOException {
        if (filePath.isDirectory()) {
            int maxDepth = getMaxDepth();
            int nextDepth = depth + 1;
            if (maxDepth >= 0 && nextDepth > maxDepth) {
                return;
            }
            for (File childPath : filePath.listFiles()) {
                walk(childPath, consumer, nextDepth);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(filePath);
                    JarInputStream jis = new JarInputStream(fis)) {
                for (; ; ) {
                    JarEntry entry = jis.getNextJarEntry();
                    if (entry == null) {
                        break;
                    }
                    if (!isDirectoryAllowed() && entry.isDirectory()) {
                        continue;
                    }
                    consumer.accept(entry);
                }
            }
        }
    }
}
