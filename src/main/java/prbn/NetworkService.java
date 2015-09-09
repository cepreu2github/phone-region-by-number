package prbn;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@Service
public class NetworkService implements INetworkService {

    @Override
    public InputStream getStreamFrom(URL url) throws IOException {
        return url.openConnection().getInputStream();
    }

    @Override
    public String getETagFrom(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        Map<String, List<String>> map = connection.getHeaderFields();
        String newHash = map.get("ETag").get(0);
        return newHash;
    }
}
