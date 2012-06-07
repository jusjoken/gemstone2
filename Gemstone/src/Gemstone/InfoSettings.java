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
    private String FlowType = "";
    
    private static enum Modes{Off,Auto,AlwaysOn};
    private String ModeList = "Off:&&:Auto:&&:AlwaysOn";
    private static enum Styles{Background,Poster,Split,Simple};
    private String StyleList = "Background:&&:Poster:&&:Split:&&:Simple";
    private String StyleListNoSplit = "Background:&&:Poster:&&:Simple";
    //TODO: InfoSettings Mode - default based on FlowType
    
    public InfoSettings(String FlowID, Boolean EpisodeLevel) {
        this(FlowID);
        this.EpisodeLevel = EpisodeLevel;
    }
    public InfoSettings(String FlowID) {
        this.FlowID = FlowID;
        this.FlowType = Flow.GetFlowType(this.FlowID);
    }
    
    public Boolean IsEpisodeLevel(){
        return this.EpisodeLevel;
    }
    
    public String PropBase(String PropName){
        if (EpisodeLevel){
            return Const.FlowInfoEpisodeSimpleList + Const.PropDivider + PropName;
        }else{
            return Const.FlowInfo + Const.PropDivider + PropName;
        }
    }
    
    //return the full property string including the Flow Base
    public String PropBaseFull(String PropName){
        if (EpisodeLevel){
            return Flow.GetFlowBaseProp(FlowID) + Const.PropDivider + Const.FlowInfoEpisodeSimpleList + Const.PropDivider + PropName;
        }else{
            return Flow.GetFlowBaseProp(FlowID) + Const.PropDivider + Const.FlowInfo + Const.PropDivider + PropName;
        }
    }
    
    public String Mode(){
        String tValue = Flow.GetOptionName(FlowID, PropBase("Mode"), util.OptionNotFound);
        if (tValue.equals(util.OptionNotFound)){
            //return the default dependent on the FlowType
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
            return tValue;
        }
    }
    public void ModeNext(){
        Flow.SetListOptionNext(FlowID, PropBase("Mode"), ModeList);
    }

    public String Style(){
        String tValue = Flow.GetOptionName(FlowID, PropBase("Style"), util.OptionNotFound);
        if (tValue.equals(util.OptionNotFound)){
            //return the default dependent on the FlowType
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
            return tValue;
        }
    }
    public void StyleNext(){
        if (this.EpisodeLevel){
            Flow.SetListOptionNext(FlowID, PropBase("Style"), StyleListNoSplit);
        }else if (this.FlowType.equals("Sage Flow")){
            Flow.SetListOptionNext(FlowID, PropBase("Style"), StyleListNoSplit);
        }else if (this.FlowType.equals("List Flow")){
            Flow.SetListOptionNext(FlowID, PropBase("Style"), StyleListNoSplit);
        }else if (this.FlowType.equals("Fanart Flow")){
            Flow.SetListOptionNext(FlowID, PropBase("Style"), StyleListNoSplit);
        }else if (this.FlowType.equals("Wall Flow")){
            Flow.SetListOptionNext(FlowID, PropBase("Style"), StyleListNoSplit);
        }else{
            Flow.SetListOptionNext(FlowID, PropBase("Style"), StyleList);
        }
    }
    
    public Integer Delay(){
        return util.GetPropertyAsInteger(PropBaseFull("Delay"), 5000);
    }
    
    
}
