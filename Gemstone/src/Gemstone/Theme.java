/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 *
 * @author jusjoken
 */
public class Theme {
    
    static private final Logger LOG = Logger.getLogger(Theme.class);

    public static Collection<String> GetHighlightSets(){
        SortedSet<String> tList = new TreeSet<String>();
        File HLSetLoc = new File(new File(util.ThemeLocation()), "Highlights");
        File[] files = HLSetLoc.listFiles();
        for (File file : files){
            if (file.isDirectory()){
                tList.add(file.getName());
            }
        }
        //LOG.debug("GetHighlightSets: found '" + tList + "'");
        return tList;
    }
    
    
}
