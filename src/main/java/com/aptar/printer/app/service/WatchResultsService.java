package com.aptar.printer.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

@Service
public class WatchResultsService {

    private static Boolean shutdown;

    private WatchService successWatcher;
    private WatchService failureWatcher;

    @Value("${app.folders.path.success}")
    private String successPath;

    @Value("${app.remote.apis.success}")
    private String successURI;

    @Value("${app.folders.path.failure}")
    private String failurePath;

    @Value("${app.remote.apis.failure}")
    private String failureURI;

    @Scheduled(fixedRate = 500)
    public void run() {

        Path successDir = Paths.get(successPath);
        Path failureDir = Paths.get(failurePath);

        try {
            successWatcher = registerWatcher(successDir);
            failureWatcher = registerWatcher(failureDir);
            WatchKey successWatchKey = getWatchKey(successWatcher, successPath, successURI);
            successWatchKey.reset();

            WatchKey failureWatchKey = getWatchKey(failureWatcher, failurePath, failureURI);
            failureWatchKey.reset();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WatchService registerWatcher(Path successDir) throws IOException {
        WatchService watcher = successDir.getFileSystem().newWatchService();
        successDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        return watcher;
    }

    private WatchKey getWatchKey(WatchService failureWatcher, String failurePath, String failureURI) throws InterruptedException, IOException {
        WatchKey failureWatchKey = failureWatcher.take();
        List<WatchEvent<?>> failureEvents = failureWatchKey.pollEvents();
        for (WatchEvent event : failureEvents) {
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                String fileName = failurePath + "/" + event.context();
                sendPOST(event.context().toString(), failureURI);
            }
        }
        return failureWatchKey;
    }

    private void sendPOST(String printerName, String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(printerName.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request not worked");
        }
    }
}