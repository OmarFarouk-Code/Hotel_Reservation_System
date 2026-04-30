package hotel.GUI.controllers;

import hotel.model.entities.Room;
import hotel.model.entities.Amenity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class RoomViewController {
    @FXML private ImageView roomImage;
    @FXML private Label ratingLabel;
    @FXML private Label roomTitleLabel;
    @FXML private Label priceLabel;

    // السطر اللي كان ناقص ومنور أحمر
    @FXML private Label descriptionLabel;

    @FXML private FlowPane amenitiesBox;
    @FXML private Button bookNowBtn;

    private Room currentRoom;

    public void setRoomData(Room room) {
        this.currentRoom = room;

        // 1. ربط اسم نوع الغرفة
        roomTitleLabel.setText(room.getRoomType().getTypeName());

        // 2. ربط السعر الحقيقي
        priceLabel.setText("$" + (int)room.getRoomType().getEffectivePrice());

        // 3. ربط الوصف (الآن لن يظهر أحمر)
        if (descriptionLabel != null) {
            // سحب الوصف من الـ RoomType المخزن في الـ Database
            descriptionLabel.setText(room.getRoomType().getDescription());
        }

        // 4. ربط التقييم الحقيقي من الريفيوهات
        ratingLabel.setText("⭐ " + String.format("%.1f", room.calculateAverageRating()));

        // 5. الأيقونات (Amenities)
        amenitiesBox.getChildren().clear();
        room.getAmenities().forEach(a -> {
            Label chip = new Label(a.getAmenityName());
            chip.getStyleClass().add("amenity-chip");
            amenitiesBox.getChildren().add(chip);
        });
    }

    @FXML
    private void onBookNow() {
        System.out.println("Booking room: " + currentRoom.getRoomNumber());
    }
}