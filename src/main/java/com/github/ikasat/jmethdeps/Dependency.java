package com.github.ikasat.jmethdeps;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class Dependency {
    private String source;
    private List<String> destinations;
}
