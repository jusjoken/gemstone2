/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.apache.commons.io.FilenameUtils;
/**
 *
 * @author jusjoken
 */
public class Fonts {
    static private final Logger LOG = Logger.getLogger(Fonts.class);
    private SortedMap<String,FontItem> FontMap = new TreeMap<String,FontItem>();
    private boolean IncludeSystemFonts = false;
    private boolean IncludeSageFonts = false;
    private boolean IncludeGemstoneFonts = false;
    private boolean Dirty = false;
    private String Current = "";
    private final String CurrentKey = "-Current";
    private enum FontSource {Gemstone,Sage,System,Current};

    public Fonts() {
        Init();
        LoadFonts();
    }
    public Fonts(String Current) {
        this.Current = Current;
        Init();
        LoadFonts();
    }
    
    private void Init(){
        IncludeGemstoneFonts = util.GetTrueFalseOption("Theme","Font/IncludeGemstone", true);
        IncludeSageFonts = util.GetTrueFalseOption("Theme","Font/IncludeSage", true);
        IncludeSystemFonts = util.GetTrueFalseOption("Theme","Font/IncludeSystem", false);
    }
    
    public void Refresh(){
        LoadFonts();
    }
    
    public static String GetNameFromPath(String Path){
        return new File(Path).getName();
    }

    public Collection<String> GetFontKeys(){
        if (Dirty){
            LoadFonts();
        }
        return FontMap.keySet();
    }
    
    public String GetFontPath(String Key){
        return FontMap.get(Key).Path;
    }
    
    public boolean isCurrentPath(String Key, String Current){
        String File1 = GetFontPath(Key);
        return isPathMatch(File1, Current);
    }

    private boolean isPathMatch(String File1, String File2){
        File1 = File1.replace("/", "\\");
        File2 = File2.replace("/", "\\");
        //LOG.debug("isPathMatch: '" + File1.toLowerCase().equals(File2.toLowerCase()) + "' for this '" + File1 + "' and current '" + File2 + "'");
        return File1.toLowerCase().equals(File2.toLowerCase());
    }

    public String GetFontDisplayName(String Key){
        return FontMap.get(Key).getDisplayName();
    }

    public String GetFontName(String Key){
        return FontMap.get(Key).getName();
    }

    public boolean isIncludeSystemFonts() {
        return IncludeSystemFonts;
    }

    public void setIncludeSystemFonts(boolean IncludeSystemFonts) {
        if (this.IncludeSystemFonts!=IncludeSystemFonts){
            Dirty=true;
            this.IncludeSystemFonts = IncludeSystemFonts;
            util.SetTrueFalseOption("Theme","Font/IncludeSystem", this.IncludeSystemFonts);
        }
    }
    public void setIncludeSystemFontsNext() {
        Dirty=true;
        this.IncludeSystemFonts = !this.IncludeSystemFonts;
        util.SetTrueFalseOption("Theme","Font/IncludeSystem", this.IncludeSystemFonts);
    }

    public boolean isIncludeSageFonts() {
        return IncludeSageFonts;
    }

    public void setIncludeSageFonts(boolean IncludeSageFonts) {
        if (this.IncludeSageFonts!=IncludeSageFonts){
            Dirty=true;
            this.IncludeSageFonts = IncludeSageFonts;
            util.SetTrueFalseOption("Theme","Font/IncludeSage", this.IncludeSageFonts);
        }
    }
    public void setIncludeSageFontsNext() {
        Dirty=true;
        this.IncludeSageFonts = !this.IncludeSageFonts;
        util.SetTrueFalseOption("Theme","Font/IncludeSage", this.IncludeSageFonts);
    }

    public boolean isIncludeGemstoneFonts() {
        return IncludeGemstoneFonts;
    }

    public void setIncludeGemstoneFonts(boolean IncludeGemstoneFonts) {
        if (this.IncludeGemstoneFonts!=IncludeGemstoneFonts){
            Dirty=true;
            this.IncludeGemstoneFonts = IncludeGemstoneFonts;
            util.SetTrueFalseOption("Theme","Font/IncludeGemstone", this.IncludeGemstoneFonts);
        }
    }
    public void setIncludeGemstoneFontsNext() {
        Dirty=true;
        this.IncludeGemstoneFonts = !this.IncludeGemstoneFonts;
        util.SetTrueFalseOption("Theme","Font/IncludeGemstone", this.IncludeGemstoneFonts);
    }
    
    private void LoadFonts(){
        Dirty = false;
        FontMap.clear();
        //get the Gemstone specific fonts from the theme
        if (IncludeGemstoneFonts){
            LoadFontsFromFolder(Const.FontPathGemstone, FontSource.Gemstone);
        }
        //get the Sage specific fonts from the Sage root Fonts folder
        if (IncludeSageFonts){
            LoadFontsFromFolder(Const.FontPathSage, FontSource.Sage);
        }
        //get the system fonts that java can use
        if (IncludeSystemFonts){
            String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for ( int i = 0; i < names.length; ++i ){
                AddFont(names[i],names[i],FontSource.System);
            }
        }
        
        //do a last check to ensure the Current - if any - is included in the list
        if (!this.Current.equals("") && !this.FontMap.containsKey(this.CurrentKey)){
            String tPath = this.Current;
            String tName = new File(tPath).getName();
            AddFont(tName,tPath,FontSource.Current);
        }
    }

    private void LoadFontsFromFolder(String FontPath, FontSource Source){
        File FontLoc = new File(FontPath);
        LOG.debug("LoadFonts: loading fonts for '" + FontLoc + "'");
        File[] files = FontLoc.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".ttf");
            }
        } );            
        for (File file : files){
            if (!file.isDirectory()){
                String tName = FilenameUtils.removeExtension(file.getName());
                String tPath = FontLoc.toString() + File.separator + tName;
                AddFont(tName,tPath,Source);
            }
        }
    }
    
    private void AddFont(String Name, String Path, FontSource Source){
        //check for the current item to force it to the top of the list with a unique key
        if (!this.Current.equals("") && isPathMatch(Path, this.Current)){
            if (FontMap.containsKey(CurrentKey)){
                LOG.debug("AddFont: current font already exists for key '" + CurrentKey + "' [" + FontMap.get(CurrentKey) + "]");
            }else{
                FontMap.put(CurrentKey,new FontItem(Name,Path,Source));
                LOG.debug("AddFont: added current font for Key '" + CurrentKey + "' [" + FontMap.get(CurrentKey) + "]");
            }
        }else{
            String tKey = GetKey(Name,Source);
            if (FontMap.containsKey(tKey)){
                LOG.debug("AddFont: font already exists for key '" + tKey + "' [" + FontMap.get(tKey) + "]");
            }else{
                FontMap.put(tKey,new FontItem(Name,Path,Source));
                LOG.debug("AddFont: added font for Key '" + tKey + "' [" + FontMap.get(tKey) + "]");
            }
        }
        
    }
    
    private String GetKey(String Name, FontSource Source){
        return Name.toLowerCase() + ":" + Source.toString().toLowerCase();
    }

    public class FontItem {    

        private String Name = "";
        private String Path = "";
        private FontSource Source = FontSource.Gemstone;

        public FontItem(String Name, String Path, FontSource Source ) {
            this.Name = Name;
            this.Path = Path;
            this.Source = Source;
        }

        public String getName() {
            return Name;
        }

        public String getPath() {
            return Path;
        }

        @Override
        public String toString() {
            return "FontItem{" + "Name=" + Name + ", Path=" + Path + ", Source=" + Source + '}';
        }

        public String getDisplayName() {
            if (this.Source.equals(FontSource.System)||this.Source.equals(FontSource.Current)){
                return this.Name;
            }else{
                return this.Name + " (" + this.Source.toString() + ")";
            }
        }

    }
    
}
