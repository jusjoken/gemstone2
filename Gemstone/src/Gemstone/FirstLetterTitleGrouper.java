/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import sagex.phoenix.vfs.groups.TitleGrouper;

/**
 *
 * @author Birch
 */
public class FirstLetterTitleGrouper  extends FirstLetterTitleRegexGrouper {
    public FirstLetterTitleGrouper() {
    	super(new TitleGrouper());
    }    
}
