package prbn;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * simple service just to get InputStream by URL
 */
public interface INetworkService {

    /**
     * @param url what we want to download
     * @return stream for network resource
     * @throws IOException
     */
    InputStream getStreamFrom(URL url) throws IOException;

    /**
     * @param url url
     * @return ETag for resource
     * @throws IOException
     */
    String getETagFrom(URL url) throws IOException;

}
