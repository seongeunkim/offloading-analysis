package com.seongekim.tcc.logic;

import java.util.List;

public class Experiment {
    private String name;
    private String method;
    private List<Integer> imageSizes;
    private List<ExperimentLayer> layers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<ExperimentLayer> getLayers() {
        return layers;
    }

    public void setLayers(List<ExperimentLayer> layers) {
        this.layers = layers;
    }

    public List<Integer> getImageSizes() {
        return imageSizes;
    }

    public void setImageSizes(List<Integer> imageSizes) {
        this.imageSizes = imageSizes;
    }
}
