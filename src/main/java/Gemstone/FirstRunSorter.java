package Gemstone;

import org.apache.log4j.Logger;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.sage.SageMediaFile;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by jusjoken on 2/24/2016.
 * Sorts a List of TV Items with the FirstRuns at the top
 */
public class FirstRunSorter implements Comparator<IMediaResource>, Serializable {
    static private final Logger LOG = Logger.getLogger(FirstRunSorter.class);
    private static final long serialVersionUID = 1L;

    public FirstRunSorter() {
    }

    public int compare(IMediaResource o1, IMediaResource o2) {
        //LOG.debug("Comparing '" + o1 + "' to '" + o2 + "'");
        if (o1 == null)
            return 1;
        if (o2 == null)
            return -1;

        //start with both values NOT being a FirstRun
        Boolean b1 = Boolean.FALSE;
        Boolean b2 = Boolean.FALSE;

        if (o1 instanceof SageMediaFile) {
            b1 = ((SageMediaFile) o1).isShowFirstRun();
        }
        if (o2 instanceof SageMediaFile) {
            b2 = ((SageMediaFile) o2).isShowFirstRun();
        }

        //LOG.debug("Comparing b1 '" + b1 + "' to b2 '" + b2 + "'");

        if (! b1 && b2) //a FirstRun (true) needs to be a lower number for b1 so it is the priority (first) in the list
            return 1;
        if (b1 && ! b2)
            return -1;

        return 0;
    }


}