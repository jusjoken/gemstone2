/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class Import {
    static private final Logger LOG = Logger.getLogger(Import.class);
    private util.ExportType eType = util.ExportType.GENERIC;
    private String FilePath = "";
    private Properties Props = new Properties();
    private Boolean IsValid = Boolean.FALSE;
    private String Description = "";
    private String Name = "";
    
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
                    in.close();
                } catch (IOException ex) {
                    LOG.debug("Import: IO exception inporting properties " + util.class.getName() + ex);
                    KeepProcessing = Boolean.FALSE;
                }
            } catch (FileNotFoundException ex) {
                LOG.debug("Import: file not found inporting properties " + util.class.getName() + ex);
                KeepProcessing = Boolean.FALSE;
            }
        }
        if (KeepProcessing){
            String tType = Props.getProperty(Const.ExportTypeKey, util.OptionNotFound);
            if (tType.equals(util.ExportType.ALL.toString())){
                IsValid = Boolean.TRUE;
                eType = util.ExportType.ALL;
                this.Name = "All Settings";
                this.Description = "This import will overwrite ALL settings for this plugin. Use with caution.";
            }else if (tType.equals(util.ExportType.FLOW.toString())){
                IsValid = Boolean.TRUE;
                eType = util.ExportType.FLOW;
                this.Name = this.Props.getProperty(Const.ExportPropName, "Single Flow");
                this.Description = "Import of this Flow will overwrite any existing Flow settings if the same Flow existed previously.";
            }else if (tType.equals(util.ExportType.FLOWS.toString())){
                IsValid = Boolean.TRUE;
                eType = util.ExportType.FLOWS;
                this.Name = "All Flows";
                this.Description = "This import will overwrite and replace ALL existing Flows. Use with caution.";
            }else if (tType.equals(util.ExportType.WIDGETS.toString())){
                IsValid = Boolean.TRUE;
                eType = util.ExportType.WIDGETS;
                this.Name = "Widget Settings";
                this.Description = "This import will overwrite and replace ALL existing Widget settings.";
            }else if (tType.equals(util.ExportType.GENERIC.toString())){
                IsValid = Boolean.TRUE;
                eType = util.ExportType.GENERIC;
                this.Name = this.Props.getProperty(Const.ExportPropName, "Generic Import");
                this.Description = "Generic import used. No specific details are available.";
            }else if (tType.equals(util.ExportType.MENUS.toString())){
                IsValid = Boolean.TRUE;
                eType = util.ExportType.MENUS;
                this.Name = "Menus";
                this.Description = "This import will overwrite and replace ALL menus in Menu Manager.";
            }else{ //block this as it is not a valid import
                if (IsOldADMExport(FilePath)){
                    IsValid = Boolean.TRUE;
                    eType = util.ExportType.MENUS;
                    this.Name = "Old ADM Menus";
                    this.Description = "This import will overwrite and replace ALL menus in Menu Manager.";
                }else{
                    IsValid = Boolean.FALSE;
                    this.Name = "Invalid Import";
                    this.Description = "This import file was not recognized so the import will not proceed.";
                }
            }
        }else{
            this.Name = "Failed";
            this.Description = "Import failed. Check the logs for more details.";
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
    
    public String GetName(){
        return this.Name;
    }
    public String GetFilePath(){
        return this.FilePath;
    }
    public String GetDescription(){
        return this.Description;
    }
    public Boolean IsValid(){
        return this.IsValid;
    }
    public util.ExportType Type(){
        return this.eType;
    }
    public void Save(){
        //save the properties to the SageTV properties file
        if (this.Props.size()>0 && this.IsValid){
            if (this.eType.equals(util.ExportType.MENUS)){
                //handle the Menus imports differently from other imports
                //TODO: EXTERNAL MENU - call the Menu Import here from the generic import class
                LOG.debug("Save: processing menus import");
            }else{
                //clean up existing Properties from the SageTV properties file before writing the new ones
                String tProp = this.Props.getProperty(Const.ExportPropKey,util.OptionNotFound);
                if (!tProp.equals(util.OptionNotFound)){
                    util.RemovePropertyAndChildren(tProp);
                    LOG.debug("Save: removing old properties '" + tProp + "'");
                }

                for (String tPropertyKey : this.Props.stringPropertyNames()){
                    if (tPropertyKey.equals(Const.ExportPropKey) || tPropertyKey.equals(Const.ExportTypeKey) || tPropertyKey.equals(Const.ExportPropName)){
                        LOG.debug("Save: skipping '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                    }else if(tPropertyKey.startsWith(ADMutil.SagePropertyLocation)){
                        //TODO: EXTERNAL MENU - handle ADM/menuitem entries differently
                        LOG.debug("Save: processing menuitem '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                    }else{
                        util.SetProperty(tPropertyKey, this.Props.getProperty(tPropertyKey));
                        LOG.debug("Save: writing '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                    }
                }
            }
            
        }
    }
    
}
