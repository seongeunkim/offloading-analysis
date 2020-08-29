package com.seongekim.tcc.shared;

import java.util.ArrayList;
import java.util.List;

public class BatchImageProcessor extends ImageProcessor {
    private List<ImageProcessor> processors;

    public BatchImageProcessor() {
        this.processors = new ArrayList<ImageProcessor>();
    }

    public BatchImageProcessor(List<ImageProcessor> processors) {
        this.processors = processors;
    }

    public void add(ImageProcessor processor) {
        this.processors.add(processor);
    }

    @Override
    public IBitmap process(IBitmap src) {
        IBitmap res = src;
        for (ImageProcessor processor : processors) {
            res = processor.process(res);
        }
        return res;
    }
}
