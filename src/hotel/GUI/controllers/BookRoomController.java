package hotel.GUI.controllers;

import hotel.model.entities.Room;
import hotel.core.BookingEngine;
import hotel.core.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class BookRoomController {

    @FXML private DatePicker checkInPicker;
    @FXML private DatePicker checkOutPicker;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private ComboBox<String> viewCombo; // أضفنا الـ View
    @FXML private Slider priceSlider;
    @FXML private Label priceValueLabel; // لعرض سعر السلايدر
    @FXML private Label adultCountLabel; // لعداد الأشخاص
    @FXML private VBox resultsVBox;

    private BookingEngine engine = new BookingEngine(); // محرك الحجز
    private int adults = 1; // القيمة الافتراضية للضيوف

    @FXML
    public void initialize() {
        // 1. تحميل أنواع الغرف من الداتا
        Database.getRoomTypes().forEach(type -> roomTypeCombo.getItems().add(type.getTypeName()));

        // 2. تحميل أنواع الـ Views المتاحة
        viewCombo.getItems().addAll("SEA_VIEW", "GARDEN_VIEW", "CITY_VIEW", "POOL");

        // 3. ضبط قيم افتراضية للتواريخ وعداد الأشخاص
        checkInPicker.setValue(LocalDate.now());
        checkOutPicker.setValue(LocalDate.now().plusDays(1));
        adultCountLabel.setText(String.valueOf(adults));

        // 4. ربط السلايدر بالـ Label (Listener) ليعرض السعر فورياً
        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            priceValueLabel.setText("$" + String.format("%.0f", newVal.doubleValue()));
        });
        priceValueLabel.setText("$" + String.format("%.0f", priceSlider.getValue()));

        // 5. عرض الغرف المتاحة فور فتح الشاشة
        onSearchRooms();
    }

    @FXML
    void onSearchRooms() {
        resultsVBox.getChildren().clear(); // مسح النتائج السابقة

        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        // جلب الغرف المتاحة من الـ Engine
        List<Room> availableRooms = engine.getAvailableRooms(checkIn, checkOut);

        for (Room room : availableRooms) {
            // فلترة حسب النوع
            boolean matchesType = (roomTypeCombo.getValue() == null ||
                    roomTypeCombo.getValue().equals("Select Type") ||
                    room.getRoomType().getTypeName().contains(roomTypeCombo.getValue()));

            boolean matchesPrice = (room.getRoomType().getEffectivePrice() <= priceSlider.getValue());

            boolean matchesView = (viewCombo.getValue() == null ||
                    room.getRoomType().getRoomView().toString().equals(viewCombo.getValue()));

            if (matchesType && matchesPrice && matchesView) {
                loadRoomCard(room);
            }
        }
    }

    private void loadRoomCard(Room room) {
        try {
            System.out.println("Displaying card for Room: " + room.getRoomNumber());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hotel/GUI/screens/RoomCard.fxml"));
            HBox cardNode = loader.load();
            cardNode.setMinWidth(600);
            RoomViewController itemController = loader.getController();
            itemController.setRoomData(room);

            resultsVBox.getChildren().add(cardNode);

        } catch (IOException e) {
            System.err.println("Problem in loading the card: " + e.getMessage());
        }
    }

    @FXML
    void onIncrementAdults() {
        adults++;
        adultCountLabel.setText(String.valueOf(adults));
    }

    @FXML
    void onDecrementAdults() {
        if (adults > 1) {
            adults--;
            adultCountLabel.setText(String.valueOf(adults));
        }
    }
}