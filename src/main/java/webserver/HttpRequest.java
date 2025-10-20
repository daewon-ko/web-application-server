package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();

    public HttpRequest(InputStream in) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = br.readLine();

            if (line == null) {
                return;
            }

            processRequestLine(line);


            while (!"".equals(line)) {
                log.debug("header : {}", line);
                line = br.readLine();
                String[] headerTokens = line.split(": ");
                headers.put(headerTokens[0].trim(), headerTokens[1].trim());
                line = br.readLine();
            }

            if ("POST".equals(method)) {
                IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
            }

        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void processRequestLine(String requestLine) {

    }


}
