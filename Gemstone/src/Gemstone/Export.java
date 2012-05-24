/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import org.apache.log4j.Logger;

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
    private String FLOW = "";
    private Boolean FullPath = Boolean.FALSE;

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
        if (this.FLOWS && this.MENUS && this.WIDGETS){
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
        }else{
            //set all to true
            this.FLOW = "";
            this.FLOWS = Boolean.TRUE;
            this.MENUS = Boolean.TRUE;
            this.WIDGETS = Boolean.TRUE;
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

    public String getFilePath() {
        return FilePath;
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
            
        }
    }
}
