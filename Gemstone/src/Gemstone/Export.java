/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import sagex.UIContext;

/**
 *
 * @author jusjoken
 */
public class Export {
    static private final Logger LOG = Logger.getLogger(Export.class);
    private String FileName = "";
    private String FilePath = "";
    private Boolean MENUS = Boolean.FALSE;
    private Boolean FLOWS = Boolean.FALSE;
    private Boolean WIDGETS = Boolean.FALSE;
    private Boolean GENERAL = Boolean.FALSE;
    private String FLOW = "";

    public Export(String FilePath){
        this.FilePath = FilePath;
    }

    public Export(){
        
    }

    public String getALLText() {
        if (IsALL()){
            return "Clear all Exports";
        }else{
            return "Set all Exports";
        }
    }

    private Boolean IsALL(){
        if (this.FLOWS && this.MENUS && this.WIDGETS && this.GENERAL){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public void ALLToggle() {
        if (IsALL()){
            //clear all toggles
            this.FLOW = "";
            this.FLOWS = Boolean.FALSE;
            this.MENUS = Boolean.FALSE;
            this.WIDGETS = Boolean.FALSE;
            this.GENERAL = Boolean.FALSE;
        }else{
            //set all to true
            this.FLOW = "";
            this.FLOWS = Boolean.TRUE;
            this.MENUS = Boolean.TRUE;
            this.WIDGETS = Boolean.TRUE;
            this.GENERAL = Boolean.TRUE;
        }
    }

    public String getFLOW() {
        return FLOW;
    }

    public void setFLOW(String FLOW) {
        this.FLOW = FLOW;
        if (IsFLOW()){
            this.FLOWS = Boolean.FALSE;
        }
    }

    public Boolean IsFLOW() {
        if (this.FLOW.equals("")){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }

    public Boolean getFLOWS() {
        return FLOWS;
    }

    public void FLOWSToggle() {
        this.FLOWS = !this.FLOWS;
        if (this.FLOWS){
            this.FLOW = "";
        }
    }

    public Boolean getMENUS() {
        return MENUS;
    }

    public void MENUSToggle() {
        this.MENUS = !this.MENUS;
    }

    public Boolean getWIDGETS() {
        return WIDGETS;
    }

    public void WIDGETSToggle() {
        this.WIDGETS = !this.WIDGETS;
    }

    public Boolean getGENERAL() {
        return GENERAL;
    }

    public void GENERALToggle() {
        this.GENERAL = !this.GENERAL;
    }

    public String getFilePath() {
        return FilePath;
    }

    public String getFileName() {
        if (this.FileName.equals("")){
            return BuildFileName();
        }else{
            return this.FileName;
        }
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public void SaveMenus(){
        this.FilePath = ADMMenuNode.GetDefaultMenuLocation();
        
    }

    private String BuildFileName(){
        String tName = "";
        if (IsALL()){
            tName = "ALL";
        }else if(IsFLOW()){
            tName = Flow.GetFlowName(this.FLOW);
        }else{
            if (this.FLOWS){
                tName = AppendName(tName, "FLOWS");
            }
            if (this.GENERAL){
                tName = AppendName(tName, "GENERAL");
            }
            if (this.MENUS){
                tName = AppendName(tName, "MENUS");
            }
            if (this.WIDGETS){
                tName = AppendName(tName, "WIDGETS");
            }
        }
        if (tName.equals("")){
            return tName;
        }else{
            return util.PrintDateSortable() + "-" + tName;
        }
    }
    private String AppendName(String Name, String AddText){
        if (Name.equals("")){
            return AddText;
        }else{
            return Name + "-" + AddText;
        }
    }
    
    public void Execute(){
        Boolean ContinueProcessing = Boolean.TRUE;
        if (this.FilePath.equals("")){
            if (this.FileName.equals("")){
                this.FileName = BuildFileName();
                if (this.FileName.equals("")){
                    ContinueProcessing = Boolean.FALSE;
                    LOG.debug("Execute: stopped as no FileName specified or nothing to Export");
                }
            }
            this.FilePath = util.UserDataLocation() + File.separator + this.FileName + ".properties";
        }
        if (ContinueProcessing){

            Properties ExportProps = new Properties();
            ExportProps.put(Const.ExportDateTimeKey, util.PrintDateTime());
            
            //add a single Flow to the export
            if (IsFLOW()){
                ExportProps.put(util.ExportType.FLOW.toString(), this.FLOW);
                LoadAllProperties(Flow.GetFlowBaseProp(this.FLOW), ExportProps, Boolean.FALSE);
            }
            //add Menus to the export
            if (this.MENUS){
                ExportProps.put(util.ExportType.MENUS.toString(), "true");
                ADMMenuNode.PropertyLoad(ExportProps);
            }
            if (IsALL()){
                //this will also load FLOWS and WIDGETS so do it in a single call
                ExportProps.put(util.ExportType.FLOWS.toString(), "true");
                ExportProps.put(util.ExportType.WIDGETS.toString(), "true");
                ExportProps.put(util.ExportType.GENERAL.toString(), "true");
                LoadAllProperties(Const.BaseProp, ExportProps, Boolean.FALSE);
            }else{
                //add all Flows to the export
                if (this.FLOWS){
                    ExportProps.put(util.ExportType.FLOWS.toString(), "true");
                    LoadAllProperties(Flow.GetFlowsBaseProp(), ExportProps, Boolean.FALSE);
                }
                //add Widgets to the export
                if (this.WIDGETS){
                    ExportProps.put(util.ExportType.WIDGETS.toString(), "true");
                    LoadAllProperties(Const.BaseProp + Const.PropDivider + Const.WidgetProp, ExportProps, Boolean.FALSE);
                }
                //add General to the export
                if (this.GENERAL){
                    ExportProps.put(util.ExportType.GENERAL.toString(), "true");
                    LoadAllProperties(Const.BaseProp, ExportProps, Boolean.TRUE);
                }
            }

            if (ExportProps.size()>0){
                //write the properties to the properties file
                try {
                    FileOutputStream out = new FileOutputStream(this.FilePath);
                    try {
                        ExportProps.store(out, Const.PropertyComment);
                        out.close();
                    } catch (IOException ex) {
                        LOG.debug("Execute: error exporting properties " + util.class.getName() + ex);
                    }
                } catch (FileNotFoundException ex) {
                    LOG.debug("Execute: error exporting properties " + util.class.getName() + ex);
                }
            }else{
                LOG.debug("Execute: no properties to export");
            }
            
            
        }
    }

    private static void LoadAllProperties(String PropLocation, Properties PropContainer, Boolean SkipEnabled){
        LOG.debug("LoadAllProperties: started for '" + PropLocation + "' SkipEnabled '" + SkipEnabled + "'");
        LoadProperties(PropLocation, PropContainer, SkipEnabled);
        LoadSubProperties(PropLocation, PropContainer, SkipEnabled);
        LOG.debug("LoadAllProperties: completed for '" + PropLocation + "'");
    }
    
    private static void LoadProperties(String PropLocation, Properties PropContainer, Boolean SkipEnabled){
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreLeaves(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            if (SkipEnabled && (tProp.startsWith(Const.BaseProp + Const.PropDivider + Const.FlowProp) || tProp.startsWith(Const.BaseProp + Const.PropDivider + Const.WidgetProp))){
                //skip this property
                LOG.debug("LoadProperties: skipping '" + tProp + "'");
            }else{
                String tValue = util.GetProperty(tProp, util.OptionNotFound);
                PropContainer.put(tProp, tValue);
                LOG.debug("LoadProperties: '" + tProp + "' = '" + tValue + "'");
            }
        }
    }
    private static void LoadSubProperties(String PropLocation, Properties PropContainer, Boolean SkipEnabled){
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreBranches(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            LoadProperties(tProp, PropContainer, SkipEnabled);
            LoadSubProperties(tProp, PropContainer, SkipEnabled);
        }
    }
    

}
