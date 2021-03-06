package single_classes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import product.*;

import java.io.*;
import java.net.URLConnection;
import java.util.*;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;


public class DatabaseManager {
    private String APIkey;
    private String url;

    public DatabaseManager() {

        url = "http://www.omdbapi.com/?";
        APIkey = "apikey=8b9932f5";
    }

    private JsonObject objectFromUrl(String tempUrl) throws IOException {
        URL url = new URL(tempUrl);
        URLConnection request = url.openConnection();

        // do not wait infinitely for connection
        request.setConnectTimeout(6000);
        request.setReadTimeout(6000);

        request.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser();
        InputStream tmp = (InputStream) request.getContent();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootobj = root.getAsJsonObject();
        return rootobj;
    }

    private Event createNewEvent( int distributorId, String imageLink, String name, String description,
                                 String durationTime, String prodCountry, float rating, String specialGuests) {

        Date d = new Date(System.currentTimeMillis() + (int)(Math.random() * 1000000000 + 1000));
        int entryPrice = (int)(Math.random() * 200 + 50);

        return new Event(distributorId, imageLink, name, description,
                durationTime, prodCountry, rating, entryPrice, specialGuests, d);
    }

    private Movie createNewMovie(int distributorId, String title, String imageLink, String description, int prodYear, String durationTime,
                                 String prodCountry, float rating, String actors, String genres) {
        return  new Movie( distributorId, imageLink, title, description, prodYear,
        durationTime, prodCountry, rating, actors, (int)(Math.random() * 30 + 5), genres);
    }

    private Series createNewSeries(int distributorId, String imageLink, String name, String description, int prodDate,
                                   String durationTime, String prodCountry, float rating, String actors, int numberOfSeasons, String genres) {

        return new Series(distributorId, imageLink, name, description, prodDate,
        durationTime, prodCountry, rating, actors, numberOfSeasons, genres);
    }

    private LiveStreaming createNewLivestreaming(int distributorId, String imageLink, String name, String description,
                                                 int prodDate, String durationTime, String prodCountry, float rating) {

        Date startDate = new Date(System.currentTimeMillis() + (int)(Math.random() * 1000000000 + 1000));
        int price = (int)(Math.random() * 100 + 50);

        return new LiveStreaming(distributorId, imageLink, name, description, prodDate,
        durationTime, prodCountry, rating, price, startDate);
    }

    public int getNextMovie(int distributorId) {
        // to do: products cannot repeat

        // randomize id on imdb
        String id = "tt" + Integer.toString( (int)(Math.random() * 7000000 + 1) );

        // create url by id
        String tempUrl = url + "i=" + id + "&" + APIkey;
        try {
            JsonObject obj = objectFromUrl(tempUrl);
            if(obj.get("Response").getAsString().equals("False")) {
                return 0;
            }

            // if downloaded Product is an episode, check for the series
            if(obj.get("Type").getAsString().equals("episode")) {
                String seriesID = obj.get("seriesID").getAsString();
                if(!seriesID.equals("N/A")) {
                    String newUrl = url + "i=" + seriesID + "&" + APIkey;
                    obj = objectFromUrl(newUrl);
                }
            }

            String title = obj.get("Title").getAsString();
            String imageLink = obj.get("Poster").getAsString();

            if(imageLink.equals("N/A")) imageLink = "https://cdn.pixabay.com/photo/2013/07/12/13/56/film-reel-147631_1280.png";

            String description = obj.get("Plot").getAsString();

            String tempYear = obj.get("Year").getAsString();
            tempYear = tempYear.replaceAll("[^\\d.]", "");
            int prodYear = Integer.parseInt(tempYear);

            String durationTime = obj.get("Runtime").getAsString(); // in minutes
            String prodCountry = obj.get("Country").getAsString();
            String actors = obj.get("Actors").getAsString();
            String genres = obj.get("Genre").getAsString();

            float rating;

            String temp = obj.get("imdbRating").getAsString();

            if(temp.equals("N/A")) {
                rating = (float)(Math.random() * 5 + 1);
            } else rating = Float.parseFloat(temp);

            int createdId;
            if(obj.get("Type").getAsString().equals("movie")) {
                double choose = Math.random();
                // 70% chance for creating new movie, 30% for creating new event
                if(choose > 0.3) {
                    // create movie
                    createdId = GlobalVariables.instance.addProduct(createNewMovie(distributorId, title, imageLink, description, prodYear, durationTime,
                            prodCountry, rating, actors, genres));
                    return createdId;
                } else {
                    // create event
                    // kind of event - 50% chance for meeting with actors, 50% for meeting with director
                    double second_choose = Math.random();
                    String name, desc, guests;
                    if(second_choose > 0.5) {
                        name = "Meeting with the actors of '" + title + "'";
                        desc = "Meeting with: " + actors + ". ";
                        guests = actors;
                    } else {
                        guests = obj.get("Director").getAsString();
                        name = "Meeting with the director of '" + title + "'";
                        desc = "Meeting with: " + guests;
                    }

                    createdId = GlobalVariables.instance.addProduct(createNewEvent(distributorId, imageLink, name, desc,
                                     durationTime, prodCountry, rating, guests));
                    return createdId;
                }
            } else if (obj.get("Type").getAsString().equals("series")) {
                    int totalSeasons;

                    String tempSeasons = obj.get("totalSeasons").getAsString();

                    if(tempSeasons.equals("N/A")) {
                        totalSeasons = (int)(Math.random() * 5 + 1);
                    } else totalSeasons = Integer.parseInt(tempSeasons);

                createdId = GlobalVariables.instance.addProduct(createNewSeries(distributorId, imageLink, title, description, prodYear,
                            durationTime, prodCountry, rating, actors, totalSeasons, genres));
                return createdId;
            } else {
                // create livestreaming
                    String name;

                    if(obj.get("Type").getAsString().equals("episode")) {
                        name = "Watching live '" + title + "' played in theater " + prodCountry;
                    } else {
                        name = "Playing online '" + title + "'";
                    }

                createdId = GlobalVariables.instance.addProduct(createNewLivestreaming(distributorId, imageLink, name, description, prodYear,
                            durationTime, prodCountry, rating));
                    return createdId;
            }
        } catch (IOException e) {
            return 0;
        }
    }
}

