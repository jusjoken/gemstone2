package Gemstone;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import sagex.api.AiringAPI;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.IMediaResource;

/**
 * Sorts based on the value of "GetRealWatchedStartTime()", but when comparing a
 * folder to an item we will figure out what the most recently watched date in
 * the folder was do the comparison
 *
 * @author jusjoken
 */
public class WatchedSorter implements Comparator<IMediaResource>, Serializable {
    static private final Logger LOG = Logger.getLogger(WatchedSorter.class);
    private static final long serialVersionUID = 1L;

    public WatchedSorter() {
    }

    public int compare(IMediaResource o1, IMediaResource o2) {
        //LOG.debug("Comparing '" + o1 + "' to '" + o2 + "'");
        if (o1 == null)
            return 1;
        if (o2 == null)
            return -1;

        long t1 = MetadataCalls.GetLastWatchedTimeStamp(o1);
        long t2 = MetadataCalls.GetLastWatchedTimeStamp(o2);
        //LOG.debug("Comparing t1 '" + t1 + "' to t2 '" + t2 + "'");

        if (t1 > t2)
            return 1;
        if (t1 < t2)
            return -1;

        return 0;
    }


}