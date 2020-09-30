package com.dst.locarusdbcloner;

import com.dst.locarusdbcloner.response.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.Properties;

public class LocarusJsonHelper {
    private String url;
    private ObjectMapper mapper;
    private Properties properties;
    private String basicAuth;

    public LocarusJsonHelper(String locarusNum, String fromDate, Instant toDate) {
        try {
            //File propFile = new File("C:\\Users\\vpriselkov\\IdeaProjects\\RealTimeDozerControl\\DozerDataAnalzer\\src\\main\\resources\\config.properties");
            File propFile = new File("src\\main\\resources\\config.properties");
            properties = new Properties();
            properties.load(new FileReader(propFile));
            //    private static final Logger logger = LogManager.getLogger(JsonHelper.class);
            String userCredentials = properties.getProperty("locarus.user") + ":" + properties.getProperty("locarus.pass");
            basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.url = String.format(
                "http://lserver3.ru:8091/do.locator?q=track&imei=%s&mode=full&filter=false&from=%s&to=%s",
                locarusNum, fromDate, toDate);
        mapper = new ObjectMapper();
    }

//    private String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

    public String getJson() {
        try {
            System.out.println(url);
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", basicAuth);
            basicAuth = "";

//            logger.debug("Getting connection...");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            logger.debug("Connection established. ");

            String inputLine;
            StringBuilder response = new StringBuilder();
//            logger.debug("Collecting data...");
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
//            logger.debug("Data collected.");
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message getMessage() {
        String response = getJson();
        if (response != null && !response.equals("")) {
            try {
                return mapper.readValue(response, Message.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}