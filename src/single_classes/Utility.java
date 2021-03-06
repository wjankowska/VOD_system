package single_classes;

import javafx.scene.control.Alert;
import product.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Utility {
    private static int max_products = 200;
    private static int max_customers = 1000;
    private static int max_distributors = 500;
    private static int day_time = 2000;

    public static int getDayTime() {
        return day_time;
    }

    public static int checkIfInt(String value) {
        int res;
        try {
            res = Integer.parseInt(value);
            if(res < 0) return 0;
            return res;
        }
        catch(NumberFormatException e) {
            return 0;
        }
    }
    public static double checkIfDouble(String value) {
        double res;
        try {
            res = Double.parseDouble(value);
            if(res < 0) return 0;
            return res;
        }
        catch(NumberFormatException e) {
            return 0;
        }
    }

    public static int getMaxProducts() {
        return max_products;
    }

    public static Integer randomKey(ArrayList <Integer> keyList) {
        Random random = new Random();
        List<Integer> keys = keyList;
        Integer randomKey = keys.get(random.nextInt(keys.size()));
        return randomKey;
    }

    public static ArrayList<String> getProductsToBuy() {
        ArrayList<String> productsToBuy = new ArrayList<>();
        productsToBuy.add("Event");
        productsToBuy.add("Movie");
        productsToBuy.add("LiveStreaming");

        return productsToBuy;
    }

    public static ArrayList<String> getProductsWithPromotion() {
        ArrayList<String> productsWithPromotion = new ArrayList<>();
        productsWithPromotion.add("Movie");
        productsWithPromotion.add("LiveStreaming");
        return productsWithPromotion;
    }

    public static void addMonthToProductViewership(Product p) {
        // increase viewership
        if(p.viewership.size() <= GlobalVariables.instance.month ) p.viewership.add(0);
    }

//    public static void addMonthToProfitBalance() {
//        if(!GlobalVariables.monthlyProfitBalance.containsKey(GlobalVariables.month))
//            GlobalVariables.monthlyProfitBalance.put(GlobalVariables.month, (float)0);
//    }

    public static int getMaxCustomers() {
        return max_customers;
    }

    public static int getMaxDistributors() {
        return max_distributors;
    }

    public static void showEmptySelectMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("Field must be selected.");
        alert.showAndWait();
    }

}
