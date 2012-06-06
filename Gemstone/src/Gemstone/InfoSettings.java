/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

/**
 *
 * @author jusjoken
 * Holds all the settings for displaying the Gemstone Info box
 */
public class InfoSettings {
    private Boolean EpisodeLevel = Boolean.FALSE;
    private String FlowID = "";
    private String Mode = "Off"; //can be Off, Auto or AlwaysOn
    private static final String ModeOff = "Off";
    private static final String ModeAuto = "Auto";
    private static final String ModeAlwaysOn = "AlwaysOn";
    private String ModeList = "Off:&&:Auto:&&:AlwaysOn";
    private String FlowType = "";
    //TODO: InfoSettings Mode - default based on FlowType
    
    public InfoSettings(String FlowID, Boolean EpisodeLevel) {
        this(FlowID);
        this.EpisodeLevel = EpisodeLevel;
    }
    public InfoSettings(String FlowID) {
        this.FlowID = FlowID;
        this.FlowType = Flow.GetFlowType(this.FlowID);
    }
    
    private String PropBase(String PropName){
        if (EpisodeLevel){
            return Const.FlowInfoEpisodeSimpleList + Const.PropDivider + PropName;
        }else{
            return Const.FlowInfo + Const.PropDivider + PropName;
        }
    }
    
    public String Mode(){
        String tMode = Flow.GetOptionName(FlowID, PropBase("Mode"), util.OptionNotFound);
        if (tMode.equals(util.OptionNotFound)){
            //return the default dependent on the FlowType
            if (this.FlowType.equals("Sage Flow")){
                return ModeAlwaysOn;
            }else if (this.FlowType.equals("Sage Flow")){
                return ModeAlwaysOn;
            }else{
                return ModeOff;
            }
        }else{
            return tMode;
        }
    }
    public void ModeNext(){
        Flow.SetListOptionNext(FlowID, PropBase("Mode"), ModeList);
    }
    
}
