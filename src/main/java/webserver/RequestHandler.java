package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine(); // 요청라인

            log.debug("request Line : {}", line); // 요청라인




            if (line == null) {
                return;
            }



            String[] tokens = line.split(" ");


            int contentLength = 0;
            while (!line.equals("")) {
                line = br.readLine();
                log.debug("header : {}", line);

                if (line.contains("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                    log.debug("ContentLength : {}", contentLength);
                }
            }



            String url = tokens[1];

            if (url.startsWith("/user/create")){
                int from = url.indexOf("?");


                String body = IOUtils.readData(br, contentLength);
                Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(body);

                User user = new User(queryStringMap.get("userId"),
                        queryStringMap.get("password"),
                        queryStringMap.get("name"),
                        queryStringMap.get("email"));

                log.debug("User : {}", user);

            }


            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = Files.readAllBytes(new File("./webapp" + tokens[1]).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
