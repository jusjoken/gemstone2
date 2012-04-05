/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import sagex.phoenix.vfs.groups.TitleGrouper;

/**
 *
 * @author Birch
 * - 04/04/2012 - updated for Gemstone
 */
public class FirstLetterTitleGrouper  extends FirstLetterTitleRegexGrouper {
    public FirstLetterTitleGrouper() {
    	super(new TitleGrouper());
    }    
}
