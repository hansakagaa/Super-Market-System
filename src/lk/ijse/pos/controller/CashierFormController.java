package lk.ijse.pos.controller;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class CashierFormController {
    public AnchorPane root;
    public ImageView imgManageOrder;
    public ImageView imgMakeOrder;
    public ImageView imgManageCustomer;
    public Label lblMenu;
    public Label lblDescription;

    @FXML
    public void navigate(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            Parent parent = null;
            switch (icon.getId()) {
                case "imgMakeOrder":
                    parent = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/place-order-form.fxml"));
                    break;
                case "imgManageOrder":
                    parent = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/order-manage-form.fxml"));
                    break;
                case "imgManageCustomer":
                    parent = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/customer-manage-form.fxml"));
                    break;
            }

            if (parent != null) {
                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
                tt.setFromX(-scene.getWidth());
                tt.setToX(0);
                tt.play();

            }
        }
    }

    @FXML
    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            switch (icon.getId()) {
                case "imgMakeOrder":
                    lblMenu.setText("Make Customer Order");
                    lblDescription.setText("Click here if you want to place a new order");
                    break;

                case "imgManageOrder":
                    lblMenu.setText("Manage Order Details");
                    lblDescription.setText("Click to search, edit, delete or view order");
                    break;

                case "imgManageCustomer":
                    lblMenu.setText("Manage Customer Details");
                    lblDescription.setText("Click to search, edit, delete or view Customer");
                    break;
            }

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.WHITE);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    @FXML
    public void playMouseExitAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();
            icon.setEffect(null);
        }
        lblMenu.setText("Welcome");
        lblDescription.setText("Please select one of above cashier operations to proceed");
    }
}
