/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
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
            }else{ //block this as it is not a valid import
                IsValid = Boolean.FALSE;
                this.Name = "Invalid Import";
                this.Description = "This import file was not recognized so the import will not proceed.";
            }
        }else{
            this.Name = "Failed";
            this.Description = "Import failed. Check the logs for more details.";
        }
        
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
            //clean up existing Properties from the SageTV properties file before writing the new ones
            String tProp = this.Props.getProperty(Const.ExportPropKey,util.OptionNotFound);
            if (!tProp.equals(util.OptionNotFound)){
                util.RemovePropertyAndChildren(tProp);
                LOG.debug("Save: removing old properties '" + tProp + "'");
            }
            
            for (String tPropertyKey : this.Props.stringPropertyNames()){
                if (tPropertyKey.equals(Const.ExportPropKey) || tPropertyKey.equals(Const.ExportTypeKey) || tPropertyKey.equals(Const.ExportPropName)){
                    LOG.debug("Save: skipping '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                }else{
                    util.SetProperty(tPropertyKey, this.Props.getProperty(tPropertyKey));
                    LOG.debug("Save: writing '" + tPropertyKey + "' = '" + this.Props.getProperty(tPropertyKey) + "'");
                }
            }
        }
    }
    
}
