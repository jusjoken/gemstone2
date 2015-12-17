/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sagex.phoenix.factory.BaseConfigurable;
import sagex.phoenix.factory.ConfigurableOption;
import sagex.phoenix.factory.ConfigurableOption.DataType;
import sagex.phoenix.factory.ConfigurableOption.ListSelection;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.groups.IGrouper;
import sagex.phoenix.vfs.groups.TitleGrouper;
import sagex.phoenix.vfs.util.HasOptions;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
//TODO:: remove once the new phoenix version is released which now has this included
public class FirstLetterTitleRegexGrouper extends TitleGrouper implements HasOptions {
	/**
	 * {@value}
	 */
	
        private List<ConfigurableOption> options = new ArrayList<ConfigurableOption>();
	private Pattern pattern = null;
        boolean ignoreThe = false;
        boolean ignoreAll = false;

	private IGrouper grouper; 
	
    public FirstLetterTitleRegexGrouper(IGrouper grouper) {
        options.add(new ConfigurableOption("ignore-the", "Disregard 'the' when grouping", "false", DataType.bool, true, ListSelection.single, "true:Yes,no:No"));
        options.add(new ConfigurableOption("ignore-all", "Disregard 'a', 'an', and 'the' when grouping", "false", DataType.bool, true, ListSelection.single, "true:Yes,no:No"));
        this.grouper = grouper;  
    }
    @Override
    public List<ConfigurableOption> getOptions() {
            return options;
    }

    @Override
    public void onUpdate(BaseConfigurable parent) {
        ignoreThe = parent.getOption("ignore-the").getBoolean(false);
        ignoreAll = parent.getOption("ignore-all").getBoolean(false);   
        String pat = ".";
        if (ignoreThe) {        
            pat = "^(?:(?:the)\\s+)?(\\S)";
        }else if( ignoreAll){
            pat = "^(?:(?:the|a|an)\\s+)?(\\S)";
        }
        pattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public String getGroupName(IMediaResource res) {
        String grp = grouper.getGroupName(res);
        if (pattern !=null && res instanceof IMediaFile) {
                if (grp!=null) {
                        Matcher m = pattern.matcher(grp);
                        if (m.find()) {
                            if (m.groupCount()>0){
                                grp = m.group(1);
                            }else{
                                grp = grp.substring(m.start(), m.end());
                            }
                        }
                }
        }
        return grp;
    }    
}
