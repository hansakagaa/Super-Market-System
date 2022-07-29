package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
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

public class LoginFormController {
    public AnchorPane root;
    public Label lblMessage;
    public JFXTextField txtUserName;
    public JFXPasswordField txtPassword;
    public JFXButton btnLogIn;

    public void initialize(){
        txtUserName.setOnAction(event -> txtPassword.requestFocus());
        txtPassword.setOnAction(event -> btnLogIn.fire());
    }

    @FXML
    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = null;
        if (txtUserName.getText().equals("cashier") && txtPassword.getText().equals("1234")){
            parent = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/cashier-form.fxml"));
        }else if (txtUserName.getText().equals("admin") && txtPassword.getText().equals("1234")){
            parent = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/administrator-form.fxml"));
        }else {
            lblMessage.setText("\"User name or Password\" is incorrect.");
        }

        if (parent != null) {
            Scene scene = new Scene(parent);
            Stage stage = (Stage) this.root.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();

            TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
            tt.setFromX(-scene.getWidth());
            tt.setToX(0);
            tt.play();
        }
    }

    @FXML
    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
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
    }
}
