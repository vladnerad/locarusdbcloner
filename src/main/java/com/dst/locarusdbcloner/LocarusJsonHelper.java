package com.dst.locarusdbcloner;

import com.dst.locarusdbcloner.response.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import ru.dst.analyze.realtime.response.Message;

public class LocarusJsonHelper {
//    private String url;
    private static final ObjectMapper mapper;
    private static String basicAuth;

   static  {
        try {
            //File propFile = new File("C:\\Users\\vpriselkov\\IdeaProjects\\RealTimeDozerControl\\DozerDataAnalzer\\src\\main\\resources\\config.properties");
//            File propFile = new File(new File("").getAbsolutePath() + File.separator + /*"\\" +*/ "src\\main\\resources\\config.properties");
            File propFile = new File(new File("").getAbsolutePath() + File.separator.concat("src").concat(File.separator).concat("main").concat(File.separator).concat("resources").concat(File.separator).concat("config.properties"));
//            System.out.println(propFile.getAbsolutePath());
            Properties properties = new Properties();
            properties.load(new FileReader(propFile));
            //    private static final Logger logger = LogManager.getLogger(JsonHelper.class);
            String userCredentials = properties.getProperty("locarus.user") + ":" + properties.getProperty("locarus.pass");
            basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapper = new ObjectMapper();
    }

    public static String getJson(String urlStr) {
        try {
            URL obj = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", basicAuth);
//            basicAuth = "";

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

    public static Message getMessage(String urlStr) {
        String response = getJson(urlStr);
        if (response != null && !response.equals("")) {
            try {
                return mapper.readValue(response, Message.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<String> getQueriesList(String locarusNum, String fromDate, Instant toDate) {
       int gap = 1;
        List<String> result = new ArrayList<>();
//        System.out.println(fromDate);
        LocalDateTime ldt = LocalDateTime.parse(fromDate.substring(0, fromDate.length()-1));
        ZoneOffset zoneOffset = ZoneOffset.ofHours(0);
        Instant from = ldt.toInstant(zoneOffset);
        Instant buff = from;
        if (ChronoUnit.DAYS.between(from, toDate) > 3) {
            while (ChronoUnit.DAYS.between(buff, toDate) > 3) {
                result.add(getHttpReq(locarusNum, buff, buff.plus(gap, ChronoUnit.DAYS)));
                buff = buff.plus(gap, ChronoUnit.DAYS);
            }
        }
        result.add(getHttpReq(locarusNum, buff, toDate));
        return result;
    }

    private static String getHttpReq(String locarusNum, Instant fromDate, Instant toDate){
       return String.format(
               "http://lserver3.ru:8091/do.locator?q=track&imei=%s&mode=full&filter=false&from=%sT00:00:00Z&to=%s",
               locarusNum, fromDate, toDate);
    }
}