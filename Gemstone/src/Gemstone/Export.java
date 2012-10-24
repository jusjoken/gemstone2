/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import sagex.UIContext;
import sagex.api.Global;
import sagex.phoenix.stv.DownloadUtil;
import sagex.phoenix.util.TimerUtil;

/**
 *
 * @author jusjoken
 */
public class Export {
    static private final Logger LOG = Logger.getLogger(Export.class);
    private String FileName = "";
    private String FilePath = "";
    private String ServerCopySourcePath = "";
    private String ServerCopyDestPath = "";
    private Boolean MENUS = Boolean.FALSE;
    private Boolean FLOWS = Boolean.FALSE;
    private Boolean WIDGETS = Boolean.FALSE;
    private Boolean GENERAL = Boolean.FALSE;
    private String FLOW = "";
    private Boolean FLOWExport = Boolean.FALSE;
    private Date ExportDateTime = new Date();
    private Boolean ConvertedADMMenus = Boolean.FALSE;
    private boolean UseServerFilePath = false;
    private boolean CopyExporttoServer = false;

    public Export(String FileName){
        this.FileName = FileName;
    }

    public Export(String FileName, util.ExportType SingleExportType){
        this.FileName = FileName;
        if (SingleExportType.equals(util.ExportType.FLOWS)){
            this.FLOWS = Boolean.TRUE;
        }else if (SingleExportType.equals(util.ExportType.GENERAL)){
            this.GENERAL = Boolean.TRUE;
        }else if (SingleExportType.equals(util.ExportType.MENUS)){
            this.MENUS = Boolean.TRUE;
        }else if (SingleExportType.equals(util.ExportType.WIDGETS)){
            this.WIDGETS = Boolean.TRUE;
        }else if (SingleExportType.equals(util.ExportType.ALL)){
            this.ALLToggle();
        }
        Execute();
    }

    public Export(){
        
    }

    public String getALLText() {
        if (IsALL()){
            return "Clear all Exports";
        }else{
            return "Select all Exports";
        }
    }

    public Boolean SomethingToExport(){
        if (this.FLOWExport || this.FLOWS || this.MENUS || this.WIDGETS || this.GENERAL){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
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
            this.FLOWExport = Boolean.FALSE;
            this.FLOWS = Boolean.FALSE;
            this.MENUS = Boolean.FALSE;
            this.WIDGETS = Boolean.FALSE;
            this.GENERAL = Boolean.FALSE;
        }else{
            //set all to true
            this.FLOWExport = Boolean.FALSE;
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
            this.FLOWExport = Boolean.TRUE;
        }
    }

    public String getFLOWText() {
        if (IsFLOW()){
            return Flow.GetFlowName(this.FLOW);
        }else{
            return "None selected";
        }
    }

    public Boolean IsFLOW() {
        if (this.FLOW.equals("")){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }

    public Boolean getFLOWExport() {
        return FLOWExport;
    }

    public void FLOWExportToggle() {
        this.FLOWExport = !this.FLOWExport;
        if (this.FLOWExport){
            this.FLOWS = Boolean.FALSE;
        }
    }

    public Boolean getFLOWS() {
        return FLOWS;
    }

    public void FLOWSToggle() {
        this.FLOWS = !this.FLOWS;
        if (this.FLOWS){
            this.FLOWExport = Boolean.FALSE;
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

    public boolean getUseServerFilePath() {
        return UseServerFilePath;
    }

    public void setUseServerFilePath(boolean UseServerFilePath) {
        this.UseServerFilePath = UseServerFilePath;
    }

    public boolean isCopyExporttoServer() {
        return CopyExporttoServer;
    }

    public void setCopyExporttoServer(boolean CopyExporttoServer) {
        this.CopyExporttoServer = CopyExporttoServer;
    }

    //Function to use Export to Save the Menus to an external file
    public void SaveMenus(){
        this.FLOWS = Boolean.FALSE;
        this.WIDGETS = Boolean.FALSE;
        this.GENERAL = Boolean.FALSE;
        this.FLOW = "";

        this.FilePath = ADMMenuNode.GetDefaultMenuLocation();
        this.MENUS = Boolean.TRUE;
        Execute();
    }
    public void SaveMenusLocal(){
        this.FLOWS = Boolean.FALSE;
        this.WIDGETS = Boolean.FALSE;
        this.GENERAL = Boolean.FALSE;
        this.FLOW = "";

        this.FilePath = ADMMenuNode.GetDefaultMenuLocationLocal();
        this.MENUS = Boolean.TRUE;
        Execute();
    }
    public void SaveMenusOverride(){
        this.FLOWS = Boolean.FALSE;
        this.WIDGETS = Boolean.FALSE;
        this.GENERAL = Boolean.FALSE;
        this.FLOW = "";

        this.FilePath = ADMMenuNode.GetDefaultMenuLocationOverride();
        this.MENUS = Boolean.TRUE;
        Execute();
    }
    public void SaveMenusOverride(String OverrideFilePath){
        this.FLOWS = Boolean.FALSE;
        this.WIDGETS = Boolean.FALSE;
        this.GENERAL = Boolean.FALSE;
        this.FLOW = "";

        this.FilePath = OverrideFilePath;
        this.MENUS = Boolean.TRUE;
        Execute();
    }

    //Function to use Export to Backup the Menus to an external file
    public void BackupMenus(){
        this.BackupMenus("");
    }
    public void BackupMenus(String FileNameOverride){
        this.FLOWS = Boolean.FALSE;
        this.WIDGETS = Boolean.FALSE;
        this.GENERAL = Boolean.FALSE;
        this.FLOW = "";

        if (FileNameOverride.isEmpty()){
            this.FilePath = util.PrintDateSortable(ExportDateTime) + "-" + ADMutil.PropertyBackupFile;
        }else{
            this.FilePath = util.PrintDateSortable(ExportDateTime) + "-" + FileNameOverride;
        }
        this.MENUS = Boolean.TRUE;
        Execute();
    }
    
    //Function to use Export to Backup the Menus to an external file
    public void ConvertADMMenus(){
        this.FLOWS = Boolean.FALSE;
        this.WIDGETS = Boolean.FALSE;
        this.GENERAL = Boolean.FALSE;
        this.FLOW = "";
        this.MENUS = Boolean.FALSE;

        this.FilePath = ADMutil.ConvertedADMMenusFilePath;
        this.ConvertedADMMenus = Boolean.TRUE;
        Execute();
    }
    
    private String BuildFileName(){
        String tName = "";
        if (IsALL()){
            tName = "ALL";
        }else{
            if (this.FLOWExport){
                tName = AppendName(tName, Flow.GetFlowName(this.FLOW));
            }
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
            return "None";
        }else{
            return util.PrintDateSortable(ExportDateTime) + "-" + tName;
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
        //LOG.debug("Execute: MENUS '" + this.MENUS + "' FLOWS '" + this.FLOWS + "' GENERAL '" + this.GENERAL + "' WIDGETS '" + this.WIDGETS + "' ConvertedADMMenus '" + this.ConvertedADMMenus + "'");
        Boolean ContinueProcessing = Boolean.TRUE;
        if (this.FilePath.equals("")){
            if (this.FileName.equals("")){
                this.FileName = BuildFileName();
                if (this.FileName.equals("")){
                    ContinueProcessing = Boolean.FALSE;
                    LOG.debug("Execute: stopped as no FileName specified or nothing to Export");
                }
            }
            if (this.UseServerFilePath){
                if (this.CopyExporttoServer){
                    this.ServerCopySourcePath = util.UserDataLocation();
                    //this.ServerCopyDestPath = util.UserDataLocationServer() + File.separator + "temp";
                    this.ServerCopyDestPath = util.UserDataLocationServer();
                    this.FilePath = util.UserDataLocation() + File.separator + this.FileName + ".properties";
                }else{
                    this.FilePath = util.UserDataLocationServer() + File.separator + this.FileName + ".properties";
                }
            }else{
                this.FilePath = util.UserDataLocation() + File.separator + this.FileName + ".properties";
            }
        }
        if (ContinueProcessing){

            LOG.debug("Execute: starting export to '" + this.FilePath + "'");
            
            Properties ExportProps = new Properties();
            ExportProps.put(Const.ExportDateTimeKey, util.PrintDateTime(ExportDateTime));
            
            //add a single Flow to the export
            if (this.FLOWExport && IsFLOW()){
                ExportProps.put(util.ExportType.FLOW.toString(), this.FLOW);
                ExportProps.put(Const.ExportFlowName, Flow.GetFlowName(this.FLOW));
                LoadAllProperties(Flow.GetFlowBaseProp(this.FLOW), ExportProps, Boolean.FALSE);
            }
            //add ConvertedADMMenus to the export
            if (this.ConvertedADMMenus){
                //treat this like any menu export even though it's source is the Sage Properties
                ExportProps.put(util.ExportType.MENUS.toString(), "true");
                LoadAllProperties(ADMutil.SagePropertyLocation, ExportProps, Boolean.FALSE);
            }
            //add Menus to the export
            if (this.MENUS){
                ExportProps.put(util.ExportType.MENUS.toString(), "true");
                ADMMenuNode.PropertySave(ExportProps);
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
                LOG.debug("Execute: attempting properties Write - count '" + ExportProps.size() + "'");
                //write the properties to the properties file
                try {
                    FileOutputStream out = new FileOutputStream(this.FilePath);
                    try {
                        ExportProps.store(out, Const.PropertyComment);
                    } finally {
                        out.close();
                    }
                } catch (Exception ex) {
                    LOG.debug("Execute: error exporting properties " + util.class.getName() + ex);
                }
                if (this.CopyExporttoServer){
                    //the saved file now needs to be copied to the server and then removed from the local client
                    String tFileName = this.FileName + ".properties";
                    Object success = sagex.api.Global.StartFileCopy(UIContext.SAGETV_PROCESS_LOCAL_UI, tFileName, this.ServerCopySourcePath, new File(this.ServerCopyDestPath));
                    LOG.debug("Execute: CopyExporttoServer success = '" + success + "' filename '" + tFileName + "' source '" + this.ServerCopySourcePath + "' dest '" + this.ServerCopyDestPath + "'");
                    if ((Boolean) success){
                        //start a background process to try and delete the source file after the copy is complete
                        TimerUtil.runOnce(0, new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    int maxtries = 10;
                                    int tries = 0;
                                    boolean error = false;
                                    while (true) {
                                        tries++;
                                        Object status = Global.GetFileCopyStatus(UIContext.SAGETV_PROCESS_LOCAL_UI);
                                        if (DownloadUtil.isDownloadComplete(status)) {
                                            Boolean success = sagex.api.Utility.DeleteLocalFilePath(UIContext.SAGETV_PROCESS_LOCAL_UI, new File(FilePath));
                                            LOG.debug("Execute: CopyExporttoServer - deleteting local source on try '" + tries + "' - success = '" + success + "'");
                                            break;
                                        }
                                        if (DownloadUtil.isDownloadError(status)) {
                                            LOG.debug("Execute: CopyExporttoServer - deleteting local source failed - error '" + (String) status + "' for file '" + FilePath + "'");
                                            error = true;
                                            break;
                                        }
                                        if (tries>=maxtries){
                                            LOG.debug("Execute: CopyExporttoServer - deleteting local source - failed due to max retires");
                                            error = true;
                                            break;
                                        }
                                        TimerUtil.sleep(300);
                                    }

                                    if (error)
                                        return;
                                } finally {
                                    //nothing to do here
                                }
                            }
                        });
                        LOG.debug("Execute: properties saved to Server location '" + this.ServerCopyDestPath + File.separator + tFileName + "'");
                    }else{
                        LOG.debug("Execute: properties could not be saved to Server so saved local to '" + this.FilePath + "'");
                    }
                }else{
                    LOG.debug("Execute: properties saved to '" + this.FilePath + "'");
                }
            }else{
                LOG.debug("Execute: no properties to export");
            }
            
        }
    }

    public static void LoadAllProperties(String PropLocation, Properties PropContainer, Boolean SkipEnabled){
        //LOG.debug("LoadAllProperties: started for '" + PropLocation + "' SkipEnabled '" + SkipEnabled + "'");
        PropLocation = CleanPropLocation(PropLocation);
        LoadProperties(PropLocation, PropContainer, SkipEnabled);
        LoadSubProperties(PropLocation, PropContainer, SkipEnabled);
        LOG.debug("LoadAllProperties: completed for '" + PropLocation + "'");
    }
    
    private static void LoadProperties(String PropLocation, Properties PropContainer, Boolean SkipEnabled){
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreLeaves(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        //LOG.debug("LoadProperties: for PropLocation '" + PropLocation + "' PropNames size = '" + PropNames.length + "'");
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            if (SkipEnabled && (tProp.startsWith(Const.BaseProp + Const.PropDivider + Const.FlowProp) || tProp.startsWith(Const.BaseProp + Const.PropDivider + Const.WidgetProp))){
                //skip this property
                //LOG.debug("LoadProperties: skipping '" + tProp + "'");
            }else{
                String tValue = util.GetProperty(tProp, util.OptionNotFound);
                PropContainer.put(tProp, tValue);
                //LOG.debug("LoadProperties: '" + tProp + "' = '" + tValue + "'");
            }
        }
    }
    private static void LoadSubProperties(String PropLocation, Properties PropContainer, Boolean SkipEnabled){
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreBranches(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        //LOG.debug("LoadSubProperties: for PropLocation '" + PropLocation + "' PropNames size = '" + PropNames.length + "'");
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            LoadProperties(tProp, PropContainer, SkipEnabled);
            LoadSubProperties(tProp, PropContainer, SkipEnabled);
        }
    }
    private static String CleanPropLocation(String PropLocation){
        if (PropLocation.endsWith("/")){
            return PropLocation.substring(0, PropLocation.length()-1);
        }else{
            return PropLocation;
        }
    }
    
}
