/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import org.apache.log4j.Logger;

/**
 *
 * @author jusjoken
 * Holds all the settings for displaying the Gemstone Info box
 */
public class InfoSettings {
    private Boolean EpisodeLevel = Boolean.FALSE;
    private boolean FlowBased = Boolean.FALSE;
    private boolean AlwaysOn = Boolean.FALSE;
    private boolean ShowCommonOptions = true;
    private String ID = "";
    private String FlowType = "";
    
    private static enum Modes{Off,Auto,AlwaysOn};
    private String ModeList = "Off:&&:Auto:&&:AlwaysOn";
    private static enum Styles{Background,Poster,Split,Simple};
    private String StyleList = "Background:&&:Poster:&&:Split:&&:Simple";
    private String StyleListNoSplit = "Background:&&:Poster:&&:Simple";
    static private final Logger LOG = Logger.getLogger(util.class);
    //TODO: InfoSettings Mode - default based on FlowType
    
    public InfoSettings(boolean FlowBased, String ID, boolean AlwaysOn, boolean ShowCommonOptions) {
        this(FlowBased, ID);
        this.AlwaysOn = AlwaysOn;
        this.ShowCommonOptions = ShowCommonOptions;
    }
    public InfoSettings(boolean FlowBased, String ID) {
        this.FlowBased = FlowBased;
        this.ID = ID;
        if (this.FlowBased){
            this.FlowType = Flow.GetFlowType(this.ID);
        }
    }
    public InfoSettings(String FlowID, Boolean EpisodeLevel) {
        this(FlowID);
        this.FlowBased = true;
        this.EpisodeLevel = EpisodeLevel;
    }
    public InfoSettings(String ID) {
        this.ID = ID;
        this.FlowBased = true;
        this.FlowType = Flow.GetFlowType(this.ID);
    }
    
    public Boolean IsEpisodeLevel(){
        return this.EpisodeLevel;
    }

    public boolean ShowCommonOptions() {
        return ShowCommonOptions;
    }
    
    public String PropSection(){
        if (FlowBased){
            return Const.FlowProp + Const.PropDivider + this.ID;
        }else{
            return Const.InfoSettings + Const.PropDivider + this.ID;
        }
    }
    
    public String PropBase(String PropName){
        if (FlowBased){
            if (EpisodeLevel){
                return Const.FlowInfoEpisodeSimpleList + Const.PropDivider + PropName;
            }else{
                return Const.FlowInfo + Const.PropDivider + PropName;
            }
        }else{
            return PropName;
        }
    }
    
    //return the full property string including the Flow Base
    public String PropBaseFull(String PropName){
        return PropSection() + Const.PropDivider + PropBase(PropName);
    }
    
    public boolean IsInfoShowing(boolean AutoInfoShowing){
        if (Mode().equals(Modes.AlwaysOn.toString())){
            return true;
        }else if (Mode().equals(Modes.Auto.toString())){
            return AutoInfoShowing;
        }else{
            return false;
        }
    }
    
    public String Mode(){
        //String tValue = Flow.GetOptionName(ID, PropBase("Mode"), util.OptionNotFound);
        if (this.AlwaysOn){
            //use this for the widget details or when you don't offer for the user to turn off the display of info
            return Modes.AlwaysOn.toString();
        }
        String tValue = util.GetOptionName(PropSection(), PropBase("Mode"), util.OptionNotFound);
        LOG.debug("Mode: value '" + tValue + "' PropSection() '" + PropSection() + "' PropBase() '" + PropBase("Mode") + "'");
        if (tValue.equals(util.OptionNotFound)){
            //return the default dependent on the FlowType or other ID
            if (FlowBased){
                if (this.EpisodeLevel){
                    return Modes.AlwaysOn.toString();
                }else if (this.FlowType.equals("Sage Flow")){
                    return Modes.AlwaysOn.toString();
                }else if (this.FlowType.equals("Center Flow")){
                    return Modes.AlwaysOn.toString();
                }else if (this.FlowType.equals("Simplified Flow")){
                    return Modes.AlwaysOn.toString();
                }else{
                    return Modes.Off.toString();
                }
            }else{
                if (this.AlwaysOn){
                    return Modes.AlwaysOn.toString();
                }else{
                    return Modes.Off.toString();
                }
            }
        }else{
            return tValue;
        }
    }
    public void ModeNext(){
        util.SetListOptionNext(PropSection(), PropBase("Mode"), ModeList);
        //Flow.SetListOptionNext(ID, PropBase("Mode"), ModeList);
    }

    public String Style(){
        String tValue = util.GetOptionName(PropSection(), PropBase("Style"), util.OptionNotFound);
        LOG.debug("Style: value '" + tValue + "' PropSection() '" + PropSection() + "' PropBase() '" + PropBase("Style") + "'");
        //String tValue = Flow.GetOptionName(ID, PropBase("Style"), util.OptionNotFound);
        if (tValue.equals(util.OptionNotFound)){
            //return the default dependent on the FlowType or other ID
            if (FlowBased){
                if (this.EpisodeLevel){
                    return Styles.Background.toString();
                }else if (this.FlowType.equals("Center Flow")){
                    return Styles.Split.toString();
                }else if (this.FlowType.equals("Fanart Flow")){
                    return Styles.Poster.toString();
                }else if (this.FlowType.equals("Sage Flow")){
                    return Styles.Poster.toString();
                }else{
                    return Styles.Background.toString();
                }
            }else{
                return Styles.Background.toString();
            }
        }else{
            return tValue;
        }
    }
    public void StyleNext(){
        if (FlowBased){
            if (this.EpisodeLevel){
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
            }else if (this.FlowType.equals("Sage Flow")){
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
            }else if (this.FlowType.equals("List Flow")){
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
            }else if (this.FlowType.equals("Fanart Flow")){
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
            }else if (this.FlowType.equals("Wall Flow")){
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
            }else if (this.FlowType.equals("Simplified Flow")){
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
            }else if (this.FlowType.equals("Inline Flow")){
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
            }else{
                util.SetListOptionNext(PropSection(), PropBase("Style"), StyleList);
            }
        }else{
            util.SetListOptionNext(PropSection(), PropBase("Style"), StyleListNoSplit);
        }
    }
    
    public Integer Delay(){
        return util.GetPropertyAsInteger(PropBaseFull("Delay"), 5000);
    }
    
    
}
