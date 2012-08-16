/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import org.apache.log4j.Logger;
import sagex.phoenix.util.Loggers;
import sagex.util.Log4jConfigurator;

/**
 *
 * @author jusjoken
 */
public class Gemstone {
    private static final Gemstone INSTANCE = new Gemstone();

    static {
            // initialize phoenix
            try {
                    INSTANCE.init();
            } catch (Exception e) {
                    Loggers.LOG.error("Failed to load gemstone.", e);
            }
    }

    public static Gemstone getInstance() {
            return INSTANCE;
    }

    private Logger log = Logger.getLogger(this.getClass());

    protected void init() {
        Log4jConfigurator.configureQuietly("gemstone", this.getClass().getClassLoader());
        log.info("Initializing Gemstone - Version: " +  api.GetVersion());
        
    }

    protected Gemstone() {
        //Init Gemstone
    }
    
}
