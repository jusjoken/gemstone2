/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import sagex.UIContext;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class Import {
    static private final Logger LOG = Logger.getLogger(Import.class);
    private util.ExportType eType = util.ExportType.GENERAL;
    private String FilePath = "";
    private Properties Props = new Properties();
    private Boolean IsValid = Boolean.FALSE;
    private String Description = "";
    private String Name = "";
    private String DateTime = "";
    private Boolean MENUS = Boolean.FALSE;
    private Boolean FLOWS = Boolean.FALSE;
    private Boolean WIDGETS = Boolean.FALSE;
    private Boolean GENERAL = Boolean.FALSE;
    private String FLOW = "";
    private String FLOWName = "";
    private Boolean MenusLoaded = Boolean.FALSE;
    private Boolean MenusSkipWrite = Boolean.FALSE;
    
    //constructor to pass a single ExportType and Import immediately - no user interaction
    public Import(String FilePath, util.ExportType SingleExportType){
        this(FilePath, SingleExportType, Boolean.FALSE);
    }
    public Import(String FilePath, util.ExportType SingleExportType, Boolean SkipMenuWrite){
        this(FilePath);
        IsValid = Boolean.TRUE;
        if (SingleExportType.equals(util.ExportType.FLOWS)){
            this.FLOWS = Boolean.TRUE;
        }else if (SingleExportType.equals(util.ExportType.GENERAL)){
            this.GENERAL = Boolean.TRUE;
        }else if (SingleExportType.equals(util.ExportType.MENUS)){
            this.MENUS = Boolean.TRUE;
        }else if (SingleExportType.equals(util.ExportType.WIDGETS)){
            this.WIDGETS = Boolean.TRUE;
        }
        this.MenusSkipWrite = SkipMenuWrite;
        Load();
    }
    public Import(String FilePath){
        this.FilePath = FilePath;

        Boolean KeepProcessing = Boolean.TRUE;
        if (FilePath==null){
            LOG.debug("Import: null FilePath passed.");
            KeepProcessing = Boolean.FALSE;
        }else{
            //read the properties from the properties file
            try {
                FileInputStream in = new FileInputStream(FilePath);
                try {
                    Props.load(in);
                } finally {
                    in.close();
                }
            } catch (Exception ex) {
                LOG.debug("Import: file not found inporting properties " + util.class.getName() + ex);
                KeepProcessing = Boolean.FALSE;
            }
        }
        if (KeepProcessing){
            //String tType = this.Props.getProperty(Const.ExportTypeKey, util.OptionNotFound);
            this.DateTime = this.Props.getProperty(Const.ExportDateTimeKey, "");
            
            if (!this.Props.getProperty(util.ExportType.FLOWS.toString(), util.OptionNotFound).equals(util.OptionNotFound)){
                this.FLOWS = Boolean.TRUE;
            }
            if (!this.Props.getProperty(util.ExportType.GENERAL.toString(), util.OptionNotFound).equals(util.OptionNotFound)){
                this.GENERAL = Boolean.TRUE;
            }
            if (!this.Props.getProperty(util.ExportType.MENUS.toString(), util.OptionNotFound).equals(util.OptionNotFound)){
                this.MENUS = Boolean.TRUE;
            }
            if (!this.Props.getProperty(util.ExportType.WIDGETS.toString(), util.OptionNotFound).equals(util.OptionNotFound)){
                this.WIDGETS = Boolean.TRUE;
            }
            if (!this.Props.getProperty(util.ExportType.FLOW.toString(), util.OptionNotFound).equals(util.OptionNotFound)){
                this.FLOW = this.Props.getProperty(util.ExportType.FLOW.toString(), util.OptionNotFound);
                this.FLOWName = this.Props.getProperty(Const.ExportFlowName, util.OptionNotFound);
            }
            SetImportSettings();
        }else{
            IsValid = Boolean.FALSE;
            this.Name = "Failed";
            this.Description = "Import failed. Check the logs for more details.";
        }
    }

    private Boolean IsALL(){
        if (this.FLOWS && this.MENUS && this.WIDGETS && this.GENERAL){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    public Boolean IsFLOW() {
        if (this.FLOW.equals("")){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    
    private void SetImportSettings(){
        String tName = "";
        if (IsALL()){
            IsValid = Boolean.TRUE;
            this.Name = "All Settings";
            this.Description = "This import will overwrite ALL settings for this plugin. Use with caution.";
        }else{
            Integer counter = 0;
            if (IsFLOW()){
                IsValid = Boolean.TRUE;
                counter++;
                tName = AppendName(tName, FLOWName);
                this.Name = FLOWName;
                this.Description = "Import of this Flow will overwrite any existing Flow settings if the same Flow existed previously.";
            }
            if (this.FLOWS){
                IsValid = Boolean.TRUE;
                counter++;
                tName = AppendName(tName, "FLOWS");
                this.Name = "All Flows";
                this.Description = "This import will overwrite and replace ALL existing Flows. Use with caution.";
            }
            if (this.GENERAL){
                IsValid = Boolean.TRUE;
                counter++;
                tName = AppendName(tName, "GENERAL");
            }
            if (this.MENUS){
                IsValid = Boolean.TRUE;
                counter++;
                tName = AppendName(tName, "MENUS");
                this.Name = "Menus";
                this.Description = "This import will overwrite and replace ALL menus in Menu Manager.";
            }
            if (this.WIDGETS){
                IsValid = Boolean.TRUE;
                counter++;
                tName = AppendName(tName, "WIDGETS");
                this.Name = "Widget Settings";
                this.Description = "This import will overwrite and replace ALL existing Widget settings.";
            }
            if (counter==0){
                if (IsOldADMExport(FilePath)){
                    IsValid = Boolean.TRUE;
                    this.MENUS = Boolean.TRUE;
                    //eType = util.ExportType.MENUS;
                    this.Name = "Old ADM Menus";
                    this.Description = "This import will overwrite and replace ALL menus in Menu Manager.";
                }else{
                    IsValid = Boolean.TRUE;
                    this.Name = "Unrecognized Import";
                    this.Description = "This import file was not recognized so import with CAUTION as all properties found will be imported into the SageTV properties file.";
                }
            }else if (counter>1){
                IsValid = Boolean.TRUE;
                this.Name = tName;
                this.Description = "This import will overwrite settings for multiple parts of this plugin. Use with caution.";
            }else{
                //only 1 of the areas were imported so name and description are already set above
                IsValid = Boolean.TRUE;
            }
        }
    }
    private String AppendName(String Name, String AddText){
        if (Name.equals("")){
            return AddText;
        }else{
            return Name + ", " + AddText;
        }
    }
    
    private static Boolean IsOldADMExport(String FilePath){
        //check to see if this is an old ADM menu export
        Boolean OldADMExportFound = Boolean.FALSE;
        try {
            FileInputStream in = new FileInputStream(FilePath);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
                LOG.debug("IsOldADMExport: BufferedReader '" + FilePath + "'");
                try {
                    String Line = reader.readLine();
                    LOG.debug("IsOldADMExport: reader readline '" + FilePath + "'");
                    reader.close();
                    LOG.debug("IsOldADMExport: reader close '" + FilePath + "'");
                    in.close();
                    LOG.debug("IsOldADMExport: close in '" + FilePath + "'");
                    if (Line.contains(ADMutil.ADMPropertyComment)){
                        OldADMExportFound = Boolean.TRUE;
                        LOG.debug("IsOldADMExport: old ADM Menus Export found in '" + FilePath + "'");
                    }else{
                        OldADMExportFound = Boolean.FALSE;
                        LOG.debug("IsOldADMExport: old ADM Menus Export NOT found in '" + FilePath + "'");
                    }
                } catch (IOException ex) {
                    OldADMExportFound = Boolean.FALSE;
                    LOG.debug("IsOldADMExport: IOException checking old ADM import " + util.class.getName() + ex);
                }
            } catch (UnsupportedEncodingException ex) {
                OldADMExportFound = Boolean.FALSE;
                LOG.debug("IsOldADMExport: UnsupportedEncodingException checking old ADM import " + util.class.getName() + ex);
            }

        } catch (FileNotFoundException ex) {
            LOG.debug("IsOldADMExport: file not found inporting properties " + util.class.getName() + ex);
            OldADMExportFound = Boolean.FALSE;
        }
        return OldADMExportFound;
    }

    public Boolean getMenusLoaded() {
        return MenusLoaded;
    }

    public String GetName(){
        return this.Name;
    }
    public String GetFilePath(){
        return this.FilePath;
    }
    public String GetDescription(){
        return this.Description;
    }
    public String GetDateTime(){
        return this.DateTime;
    }
    public Boolean IsValid(){
        return this.IsValid;
    }
    public util.ExportType Type(){
        return this.eType;
    }
    
    public String PreviewProps(){
        StringBuffer buf = new StringBuffer();
        TreeSet<String> PreviewKeys = new TreeSet<String>(Props.stringPropertyNames());
        for (String key:PreviewKeys){
            buf.append(key + "=" + Props.getProperty(key) + "\n");
        }
        return buf.toString();
    }

    public void Load(){
        //load the properties to the SageTV properties file or menus file
        //LOG.debug("Load: MENUS '" + this.MENUS + "' FLOWS '" + this.FLOWS + "' GENERAL '" + this.GENERAL + "' WIDGETS '" + this.WIDGETS + "'");
        if (this.Props.size()>0 && this.IsValid){
            //clean up existing Properties from the SageTV properties file before writing the new ones
            String tProp = "";
            if (IsALL()){
                tProp = Const.BaseProp;
                util.RemovePropertyAndChildren(tProp);
                LOG.debug("Load: removing old properties for ALL '" + tProp + "'");
            }else{
                if (IsFLOW()){
                    tProp = Flow.GetFlowBaseProp(this.FLOW);
                    util.RemovePropertyAndChildren(tProp);
                    LOG.debug("Load: removing old properties for specific Flow '" + tProp + "'");
                }
                if (this.FLOWS){
                    tProp = Const.BaseProp + Const.PropDivider + Const.FlowProp;
                    util.RemovePropertyAndChildren(tProp);
                    LOG.debug("Load: removing old properties for FLOWS type '" + tProp + "'");
                }
                if (this.WIDGETS){
                    tProp = Const.BaseProp + Const.PropDivider + Const.WidgetProp;
                    util.RemovePropertyAndChildren(tProp);
                    LOG.debug("Load: removing old properties for WIDGET type '" + tProp + "'");
                }
                if (this.GENERAL){
                    //remove all Gemstone properties BUT NOT FLOWS or WIDGETS
                    tProp = Const.BaseProp;
                    SafeRemoveAllProperties(tProp);
                    SafeRemoveAllSubProperties(tProp);
                    LOG.debug("Load: removing old properties for GENERAL type '" + tProp + "'");
                }
                //no need to remove properties for MENUS as they are stored in a separate file

            }
            
            //need a properties to store potential Menus properties
            PropertiesExt Menus = new PropertiesExt();

            for (String tPropertyKey : this.Props.stringPropertyNames()){
                if (tPropertyKey.equals(Const.ExportPropKey) || tPropertyKey.equals(Const.ExportTypeKey) || tPropertyKey.equals(Const.ExportPropName) || tPropertyKey.equals(Const.ExportDateTimeKey) || tPropertyKey.equals(Const.ExportFlowName)){
                    //LOG.debug("Load: skipping '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                }else if(tPropertyKey.equals("FLOW") || tPropertyKey.equals("FLOWS") || tPropertyKey.equals("GENERAL") || tPropertyKey.equals("MENUS") || tPropertyKey.equals("WIDGETS")){
                    //LOG.debug("Load: skipping type identifier '" + tPropertyKey + "'");
                }else if(tPropertyKey.startsWith(ADMutil.SagePropertyLocation)){
                    Menus.put(tPropertyKey, this.Props.getProperty(tPropertyKey));
                    //LOG.debug("Load: processing menuitem '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                }else{
                    util.SetProperty(tPropertyKey, this.Props.getProperty(tPropertyKey));
                    //LOG.debug("Load: loading '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                }
            }
            if (Menus.size()>0){
                //handle the Menus imports differently from other imports
                this.MenusLoaded = ADMMenuNode.PropertyLoad(Menus);
                if (this.MenusLoaded){
                    if (MenusSkipWrite){
                        LOG.debug("Load: menus imported - write skipped");
                    }else{
                        WriteMenuProperties(Menus);
                        LOG.debug("Load: menus imported and written");
                    }
                }else{
                    LOG.debug("Load: menus not imported");
                }
            }
        }
    }

    private void WriteMenuProperties(PropertiesExt ExportProps){
        String tFilePath = ADMMenuNode.GetDefaultMenuLocation();
        if (ExportProps.size()>0){
            ExportProps.put(Const.ExportDateTimeKey, util.PrintDateTime(new Date()));
            ExportProps.put(util.ExportType.MENUS.toString(), "true");
            //write the properties to the properties file
            try {
                FileOutputStream out = new FileOutputStream(tFilePath);
                try {
                    ExportProps.store(out, Const.PropertyComment);
                } finally {
                    out.close();
                }
            } catch (Exception ex) {
                LOG.debug("WriteMenuProperties: error exporting properties " + util.class.getName() + ex);
            }
        }else{
            LOG.debug("WriteMenuProperties: no properties to export");
        }
        
    }
    
    private static void SafeRemoveAllProperties(String PropLocation){
        //remove any property as long as it is not a Flow or Widget property
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreLeaves(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            if (tProp.startsWith(Const.BaseProp + Const.PropDivider + Const.FlowProp) || tProp.startsWith(Const.BaseProp + Const.PropDivider + Const.WidgetProp)){
                //skip this property
                LOG.debug("SafeRemoveAllProperties: skipping '" + tProp + "'");
            }else{
                util.RemoveProperty(tProp);
                LOG.debug("SafeRemoveAllProperties: removing '" + tProp + "'");
            }
        }
    }
    private static void SafeRemoveAllSubProperties(String PropLocation){
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreBranches(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            SafeRemoveAllProperties(tProp);
            SafeRemoveAllSubProperties(tProp);
        }
    }
    
    
    
}
