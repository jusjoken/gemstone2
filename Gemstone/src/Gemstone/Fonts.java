/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 *
 * @author jusjoken
 */
public class Fonts {
    static private final Logger LOG = Logger.getLogger(Fonts.class);
    private SortedMap<String,String> FontMap = new TreeMap<String,String>();
    private boolean IncludeSystemFonts = false;
    private boolean IncludeSageFonts = false;
    private boolean IncludeGemstoneFonts = false;
    private boolean Dirty = false;

    public Fonts() {
        LoadFonts();
    }
    
    public void Refresh(){
        LoadFonts();
    }

    public Collection<String> GetFontNames(){
        if (Dirty){
            LoadFonts();
        }
        return FontMap.keySet();
    }
    
    public String GetFontPath(String Key){
        return FontMap.get(Key);
    }

    public boolean isIncludeSystemFonts() {
        return IncludeSystemFonts;
    }

    public void setIncludeSystemFonts(boolean IncludeSystemFonts) {
        if (this.IncludeSystemFonts!=IncludeSystemFonts){
            Dirty=true;
            this.IncludeSystemFonts = IncludeSystemFonts;
        }
    }
    public void setIncludeSystemFontsNext() {
        Dirty=true;
        this.IncludeSystemFonts = !this.IncludeSystemFonts;
    }

    public boolean isIncludeSageFonts() {
        return IncludeSageFonts;
    }

    public void setIncludeSageFonts(boolean IncludeSageFonts) {
        if (this.IncludeSageFonts!=IncludeSageFonts){
            Dirty=true;
            this.IncludeSageFonts = IncludeSageFonts;
        }
    }
    public void setIncludeSageFontsNext() {
        Dirty=true;
        this.IncludeSageFonts = !this.IncludeSageFonts;
    }

    public boolean isIncludeGemstoneFonts() {
        return IncludeGemstoneFonts;
    }

    public void setIncludeGemstoneFonts(boolean IncludeGemstoneFonts) {
        if (this.IncludeGemstoneFonts!=IncludeGemstoneFonts){
            Dirty=true;
            this.IncludeGemstoneFonts = IncludeGemstoneFonts;
        }
    }
    public void setIncludeGemstoneFontsNext() {
        Dirty=true;
        this.IncludeGemstoneFonts = !this.IncludeGemstoneFonts;
    }
    
    private void LoadFonts(){
        Dirty = false;
        IncludeGemstoneFonts = util.GetTrueFalseOption("Theme","Font/IncludeGemstone", true);
        IncludeSageFonts = util.GetTrueFalseOption("Theme","Font/IncludeSage", true);
        IncludeSystemFonts = util.GetTrueFalseOption("Theme","Font/IncludeSystem", false);
        FontMap.clear();
        //get the Gemstone specific fonts from the theme
        if (IncludeGemstoneFonts){
            File FontLoc = new File(new File(util.ThemeLocation()), "Fonts");
            File[] files = FontLoc.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".ttf");
                }
            } );            
            for (File file : files){
                if (file.isDirectory()){
                    FontMap.put(file.getName(),util.ThemeLocation() + File.separator + "Fonts" + File.separator + file.getName());
                }
            }
        }
    }
    
    
}
