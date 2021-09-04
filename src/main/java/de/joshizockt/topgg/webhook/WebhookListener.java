package de.joshizockt.topgg.webhook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WebhookListener extends Thread {

    private final HttpServer server;
    private String secret;
    private List<String> whitelist;

    public WebhookListener(int port) throws IOException {
        this(port, "/webhook");
    }

    public WebhookListener(int port, String path) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        // start listening
        server.createContext(path, exchange -> {
            System.out.println("incoming exchange!");
            // handle auth

            boolean success = false;
            if(whitelistActivated()) {
                for(String ip : whitelist) {
                    if(exchange.getRemoteAddress().getHostString().equalsIgnoreCase(ip)) success = true;
                }
            } else success = true;
            if(!success) {
                String response = "Error!";
                exchange.sendResponseHeaders(400, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            success = false;
            if(secret != null) {
                Headers headers = exchange.getRequestHeaders();
                for(String key : headers.keySet()) {
                    if(key.equalsIgnoreCase("Authorization")) {
                        List<String> values = headers.get(key);
                        for(String value : values) {
                            if(value.equalsIgnoreCase(secret)) {
                                success = true;
                                break;
                            }
                        }
                    }
                }
            } else success = true;
            if(!success) {
                String response = "Error!";
                exchange.sendResponseHeaders(400, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            JsonElement element;
            try {
                InputStream input = exchange.getRequestBody();
                JsonReader reader = new JsonReader(new InputStreamReader(input));
                element = JsonParser.parseReader(reader);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if(element == null || !element.isJsonObject()) {
                System.err.println("Element is not JsonObject: " + element);
                return;
            }

            try {
                JsonObject o = element.getAsJsonObject();
                boolean bot = o.get("bot") != null && !o.get("bot").isJsonNull();

                String query = "";

                if(o.get("query?") != null && !o.get("query?").isJsonNull()) {
                    query = o.get("query?").getAsString();
                }

                if(bot) {
                    handleBotHook(
                            o.get("bot").getAsLong(),
                            o.get("user").getAsLong(),
                            o.get("type").getAsString(),
                            o.get("isWeekend").getAsBoolean(),
                            query
                    );
                } else {
                    handleServerHook(
                            o.get("bot").getAsLong(),
                            o.get("user").getAsLong(),
                            o.get("type").getAsString(),
                            query
                    );
                }

                String response = "Ok!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        });

    }

    public void start() {
        server.start();
    }

    public void handleBotHook(long bot, long user, String type, boolean isWeekend, String query) { }

    public void handleServerHook(long guild, long user, String type, String query) { }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void addWhitelisted(String address) {
        whitelist.add(address);
    }

    public void setWhitelist(boolean enabled) {
        if(enabled)
            if(whitelist == null) whitelist = new ArrayList<>();
        if(!enabled)
            whitelist = null;
    }

    public String getSecret() {
        return secret;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public boolean whitelistActivated() {
        return whitelist != null;
    }

}
