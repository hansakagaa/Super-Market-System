package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.SystemReportBO;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailsDTO;
import lk.ijse.pos.view.tm.CartTM;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemReportFormController {
    private final SystemReportBO systemReportBO = (SystemReportBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.SYSTEM_REPORT);
    public AnchorPane root;
    public JFXDatePicker startDate;
    public JFXDatePicker endDate;
    public JFXTextField txtIncome;
    public JFXTextField txtOrders;
    public Label lblBanner;
    public TableView<CartTM> tblItem;
    public ImageView imgMostItem;
    public ImageView imgLeastItem;
    List<CartTM> items = new ArrayList<>();
    List<CartTM> itemTM = new ArrayList<>();
    List<OrderDTO> orderDto = new ArrayList<>();
    List<OrderDetailsDTO> orderDetailsDto = new ArrayList<>();

    public void initialize(){
//
        tblItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tblItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItem.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        tblItem.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("total"));
//
    }
//
    @FXML
    public void navigateToHome(MouseEvent event) throws IOException {
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/administrator-form.fxml"))));
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }
//
    @FXML
    public void navigate(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            switch (icon.getId()) {
                case "imgMostItem":
                    setMostMovableItem();
                    break;
                case "imgLeastItem":
                    setLeastMovableItem();
                    break;
            }
        }
    }

    private void setLeastMovableItem() {
        for (CartTM dto : setMovableItem()) {
            tblItem.getItems().add(new CartTM(dto.getItemCode(), dto.getDescription(), dto.getPackSize(), dto.getUnitPrice(), dto.getOrderQty(), dto.getTotal()));
        }
    }

    private void setMostMovableItem() {
        for (CartTM dto : setMovableItem()) {
            tblItem.getItems().add(new CartTM(dto.getItemCode(), dto.getDescription(), dto.getPackSize(), dto.getUnitPrice(), dto.getOrderQty(), dto.getTotal()));
        }
    }

    private List<CartTM> setMovableItem(){
        itemTM.clear();
        for (CartTM cart : items) {
            for (CartTM cartTM : items) {
                if (cart.getItemCode().equals(cartTM.getItemCode())){
                    itemTM.add(new CartTM(cart.getItemCode(), cart.getDescription(), cart.getPackSize(), cart.getUnitPrice(), cart.getOrderQty()+cartTM.getOrderQty(), cart.getTotal().add(cartTM.getTotal())));
                }
            }
        }
        return itemTM;
    }

//
    @FXML
    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            switch (icon.getId()) {
                case "imgMostItem":
                    lblBanner.setText("Click here view Most movable Item");
                    break;

                case "imgLeastItem":
                    lblBanner.setText("Click here view Least movable Item");
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
//
    @FXML
    public void playMouseExitAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();
            icon.setEffect(null);

            lblBanner.setText("Select movable Item");
        }
    }
//
    @FXML
    public void selectDateOnAction(javafx.event.ActionEvent actionEvent) {
        tblItem.getItems().clear();
        if (startDate.getValue() != null && endDate.getValue() != null){
            BigDecimal totalInCome = new BigDecimal(0);
            int orderCount = 0;

            try {
                ArrayList<OrderDTO> dtoS = systemReportBO.getOrderFromDate(startDate.getValue().toString(), endDate.getValue().toString());
                orderDto.clear();
                orderDto.addAll(dtoS);
                orderDetailsDto.clear();
                for (OrderDTO dto : orderDto) {
                    ArrayList<OrderDetailsDTO> orderDetails = systemReportBO.getOrderDetails(dto.getOrderID());
                    orderDetailsDto.addAll(orderDetails);
                }
                orderCount = orderDto.size();
                items.clear();
                for (OrderDetailsDTO dto : orderDetailsDto) {
                    ItemDTO itemDTO = systemReportBO.searchItem(dto.getItemCode());
                    items.add(new CartTM(itemDTO.getItemCode(), itemDTO.getDescription(), itemDTO.getPackSize(), itemDTO.getUnitPrice(), dto.getOrderQty(), itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(dto.getOrderQty()))));

                    totalInCome = totalInCome.add(itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(dto.getOrderQty())));
                }

            } catch (SQLIntegrityConstraintViolationException e){
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR," "+ e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR,""+ e.getMessage()).show();
            }

            txtIncome.setText(totalInCome+"");
            txtOrders.setText(orderCount+"");
        }
    }
}
