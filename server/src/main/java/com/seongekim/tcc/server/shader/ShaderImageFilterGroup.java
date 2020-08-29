package com.seongekim.tcc.server.shader;

import java.util.ArrayList;

public class ShaderImageFilterGroup extends AbstractShaderFilter {
    private ArrayList<AbstractShaderFilter> filters;

    public ShaderImageFilterGroup() throws Exception {
        filters = new ArrayList<>();
    }

    public void addFilter(AbstractShaderFilter filter) {
        filters.add(filter);
    }

    public void onInit(ShaderContext context) {
        if(!hasResultTexture())
            setResultTexture(createResultTexture(context.getWidth(), context.getHeight()));
        for(AbstractShaderFilter filter : filters)
            filter.init(context);
    }

    public void onDispose(ShaderContext context) {
        for(AbstractShaderFilter filter : filters)
            filter.dispose(context);
    }

    @Override
    public void run(ShaderContext context) throws Exception {
        if(filters.isEmpty()) return;

        filters.get(filters.size() - 1).setResultTexture(getResultTexture());
        AbstractShaderFilter lastFilter = null;
        for(AbstractShaderFilter filter : filters) {
            filter.bind(context);
            if(lastFilter != null)
                context.runOnInputTexture(lastFilter.getResultTexture(), filter);
            else
                context.run(filter);
            filter.release(context);
            lastFilter = filter;
        }
    }
}
