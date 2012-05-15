/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import org.apache.log4j.Logger;
import sage.SageTVPluginRegistry;
import sagex.phoenix.task.ITaskOperation;
import sagex.phoenix.task.ITaskProgressHandler;
import sagex.phoenix.task.TaskItem;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.SageEvent;
import sagex.plugin.SageEvents;

/**
 *
 * @author jusjoken
 */
public class GemstonePlugin extends AbstractPlugin implements ITaskOperation, ITaskProgressHandler {

    static private final Logger LOG = Logger.getLogger(GemstonePlugin.class);
    
    public GemstonePlugin(SageTVPluginRegistry registry) { 
        super(registry); 
        LOG.debug("Gemstone Plugin Loaded");
    }    

    @SageEvent(value = SageEvents.AllPluginsLoaded, background = true)
    public void onPluginsLoaded() {  
        LOG.debug("onPluginsLoaded: All Plugins Loaded");
        util.HandleNonCompatiblePlugins();
    }    
    
    @Override
    public void start() {      
        LOG.debug("start: called for GemstonePlugin");
        super.start(); 
    }

    @Override
    public void performAction(TaskItem item) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canRetry(Throwable t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onStart(TaskItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onComplete(TaskItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onError(TaskItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
