/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone2;

import java.util.List;
import org.apache.log4j.Logger;
import sagex.phoenix.factory.ConfigurableOption;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class ConfigOption extends ConfigurableOption {

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private String PropLocation = "";
    static private final Logger LOG = Logger.getLogger(ConfigOption.class);
    public ConfigOption(String PropertyLocation, String name, String value) {
        super(name, value);
        PropLocation = PropertyLocation;
    }

    public ConfigOption(String PropertyLocation, String name, String label, String value, DataType dataType, boolean isList, ListSelection listSelection, String listValues) {
        super(name, label, value, dataType, isList, listSelection, listValues);
        PropLocation = PropertyLocation;
    }

    public ConfigOption(String PropertyLocation, String name, String label, String value, DataType dataType, boolean isList, ListSelection listSelection, List<ListValue> listValues) {
        super(name, label, value, dataType, isList, listSelection, listValues);
        PropLocation = PropertyLocation;
    }

    public ConfigOption(String PropertyLocation, String name, String label, String value, DataType dataType) {
        super(name, label, value, dataType);
        PropLocation = PropertyLocation;
    }

    public ConfigOption(String PropertyLocation, String name) {
        super(name);
        PropLocation = PropertyLocation;
    }

    public ConfigOption(String PropertyLocation, ConfigurableOption inOpt) {
        super(inOpt.getName(), inOpt.getLabel(), inOpt.value().getValue(), inOpt.getDataType(), inOpt.isList(), inOpt.getListSelection(), inOpt.getListValues());
        PropLocation = PropertyLocation;
    }

    public String GetPropertyLocation(){
        return PropLocation;
    }
    public Boolean IsSet(){
        if (GetValue().equals(SourceUI.OptionNotSet)){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    public String GetValue(){
        //get the value from the properties file
        //if not found then use NotSet
        String tReturn = util.GetOptionName(PropLocation, getName(), SourceUI.OptionNotSet);
        if (isList() && !tReturn.equals(SourceUI.OptionNotSet)){
            //check that this is a valid value
            Boolean found = Boolean.FALSE;
            for (ListValue tItem: getListValues()){
                if (tItem.getValue().equals(tReturn)){
                    found = Boolean.TRUE;
                    break;
                }
            }
            if (!found){
                LOG.debug("GetValue: for '" + getName() + "' stored value '" + tReturn + "' is not a valid value. Returning NotSet. PropertyLocation '" + PropLocation + "'");
                tReturn = SourceUI.OptionNotSet;
            }
        }
        return tReturn;
    }
    public String GetValueLabel(){
        //get the Label for the List Item
        //if not found then use NotSet
        String tReturn = util.GetOptionName(PropLocation, getName(), SourceUI.OptionNotSet);
        if (isList() && !tReturn.equals(SourceUI.OptionNotSet)){
            //check that this is a valid value and get the Label
            Boolean found = Boolean.FALSE;
            for (ListValue tItem: getListValues()){
                if (tItem.getValue().equals(tReturn)){
                    found = Boolean.TRUE;
                    tReturn = tItem.getName();
                    break;
                }
            }
            if (!found){
                LOG.debug("GetValue: for '" + getName() + "' stored value '" + tReturn + "' is not a valid value. Returning NotSet. PropertyLocation '" + PropLocation + "'");
                tReturn = SourceUI.OptionNotSet;
            }
        }
        return tReturn;
    }
    public void Clear(){
        util.SetOption(PropLocation, getName(), SourceUI.OptionNotSet);
    }
    public void SetValue(String NewValue){
        if (!isList()){
            util.SetOption(PropLocation, getName(), NewValue);
        }
    }
    public void SetNext(){
        if (isList()){
            //change the value to the next value in the list or NotSet if at the last entry already
            String CurrentValue = GetValue();
            //LOG.debug("SetNext: for '" + getName() + "' CurrentValue '" + CurrentValue + "' Values '" + getListValues() + "'");
            if (CurrentValue.equals(SourceUI.OptionNotSet)){
                util.SetOption(PropLocation, getName(), getListValues().get(0).getValue());  //default to the 1st item
                //LOG.debug("SetNext: for '" + getName() + "' CurrentValue '" + CurrentValue + "' Item 0 '" + getListValues().get(0).getValue() + "'");
            }else{
                Integer pos = ListValuesIndex(CurrentValue);
                if (pos==-1){ //not found
                    util.SetOption(PropLocation, getName(), getListValues().get(0).getValue());
                    //LOG.debug("SetNext: for '" + getName() + "' CurrentValue '" + CurrentValue + "' Item " + pos + " = '" + getListValues().get(0).getValue() + "'");
                }else if(pos==getListValues().size()-1){ //last item
                    util.SetOption(PropLocation, getName(), SourceUI.OptionNotSet);
                    //LOG.debug("SetNext: for '" + getName() + "' CurrentValue '" + CurrentValue + "' Last Item - setting to NotSet");
                }else{ //get next item
                    util.SetOption(PropLocation, getName(), getListValues().get(pos+1).getValue());
                    //LOG.debug("SetNext: for '" + getName() + "' CurrentValue '" + CurrentValue + "' Item " + (pos+1) + " = '" + getListValues().get(pos+1).getValue() + "'");
                }
            }
        }
    }
    private Integer ListValuesIndex(String ItemValue){
        Integer counter = 0;
        for (ListValue tItem: getListValues()){
            if (tItem.getValue().equals(ItemValue)){
                return counter;
            }
            counter++;
        }
        return -1;
    }
    
}
