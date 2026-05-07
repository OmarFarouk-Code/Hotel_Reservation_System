package hotel.GUI.controllers;

import java.time.LocalDate;

import hotel.core.BookingEngine;
import hotel.core.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;



public class AdminController {

    @FXML
    private LineChart<String, Number> revenueChart;
    
    @FXML
    private CategoryAxis xAxis;
    
    @FXML
    private NumberAxis yAxis;

    @FXML
    private VBox invoiceList;


    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label occupancyLabel;
    @FXML
    private Label activeReservationsLabel;
    @FXML
    private Label registeredGuestsLabel;


    private BookingEngine engine = new BookingEngine(); 

    @FXML
    public void initialize() {
        

        
        double totalRev = engine.calculateTotalRevenue(); 
        totalRevenueLabel.setText(String.format("$%.2f", totalRev));

      
        double occupancy = engine.calculateOccupancyPercentage();
        occupancyLabel.setText(String.format("%.0f%%", occupancy));


        int activeCount = 0;
        if(Database.getReservations() != null) {
             activeCount = Database.getReservations().size(); 
        }
        activeReservationsLabel.setText(String.valueOf(activeCount));

     
        int guestCount = 0;
        if(Database.getGuests() != null){
             guestCount = Database.getGuests().size();
        }
        registeredGuestsLabel.setText(String.valueOf(guestCount));



        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Last 30 Days");
        
       
        LocalDate today = LocalDate.now();
        
    
        double week1Rev = BookingEngine.calculateTotalRevenue(today.minusDays(7), today);
       
        double week2Rev = engine.calculateTotalRevenue(today.minusDays(14), today.minusDays(7));
      
        double week3Rev = engine.calculateTotalRevenue(today.minusDays(21), today.minusDays(14));
 
        double week4Rev = engine.calculateTotalRevenue(today.minusDays(28), today.minusDays(21));

  
        series.getData().add(new XYChart.Data<>("Week 4", week4Rev));
        series.getData().add(new XYChart.Data<>("Week 3", week3Rev));
        series.getData().add(new XYChart.Data<>("Week 2", week2Rev));
        series.getData().add(new XYChart.Data<>("Week 1", week1Rev));

        revenueChart.getData().add(series);
    }



    @FXML
    public void onAddRoom(ActionEvent event) {
        System.out.println("Opening Add Room Dialog...");
        
    }

    @FXML
    public void onAddType(ActionEvent event) {
        System.out.println("Opening Add Room Type Dialog...");

    }

    @FXML
    public void onAddAmenity(ActionEvent event) {
        System.out.println("Opening Add Amenity Dialog...");
     
    }

    @FXML
    public void onCreatePromo(ActionEvent event) {
        System.out.println("Opening Create Promo Code Dialog...");
     
    }
}