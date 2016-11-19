package Gemstone;

import org.apache.log4j.Logger;
import sagex.phoenix.vfs.IMediaResource;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by jusjoken on 2/20/2016.
 * Sorts a List of TV Episodes by the Season and Episode numbers
 * Converts these to a long value to aid in the sort
 */
public class SeasonEpisodeSorter implements Comparator<IMediaResource>, Serializable {
    static private final Logger LOG = Logger.getLogger(SeasonEpisodeSorter.class);
    private static final long serialVersionUID = 1L;

    public SeasonEpisodeSorter() {
    }

    public int compare(IMediaResource o1, IMediaResource o2) {
        //LOG.debug("Comparing '" + o1 + "' to '" + o2 + "'");
        if (o1 == null)
            return 1;
        if (o2 == null)
            return -1;

        long t1 = MetadataCalls.GetSeasonEpisodeForSort(o1);
        long t2 = MetadataCalls.GetSeasonEpisodeForSort(o2);
        //LOG.debug("Comparing t1 '" + t1 + "' to t2 '" + t2 + "'");

        if (t1 > t2)
            return 1;
        if (t1 < t2)
            return -1;

        return 0;
    }


}