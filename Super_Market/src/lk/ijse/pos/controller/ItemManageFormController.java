package lk.ijse.pos.controller;

import javafx.scene.control.TableView;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.ItemBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.view.tm.ItemTM;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemManageFormController {
    private final ItemBO itemBO = (ItemBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ITEM);
    public AnchorPane root;
    public JFXButton btnNew;
    public JFXTextField txtItemCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSaveOrUpdate;
    public JFXButton btnDelete;
    public TableView<ItemTM> tblItem;

    public void initialize() {
//
        tblItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tblItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItem.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
//
        initialUi();
//
        tblItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            btnSaveOrUpdate.setText(newValue != null ? "Update" : "Save");
            btnSaveOrUpdate.setDisable(newValue == null);

            if (newValue != null) {
                txtItemCode.setText(newValue.getItemCode());
                txtDescription.setText(newValue.getDescription());
                txtPackSize.setText(newValue.getPackSize());
                txtUnitPrice.setText(newValue.getUnitPrice().toString());
                txtQtyOnHand.setText(newValue.getQtyOnHand() + "");

                setDisable(false,txtItemCode,txtDescription,txtPackSize,txtUnitPrice,txtQtyOnHand);
            }
        });
//
        loadAllItem();

//
        txtDescription.setOnAction(event -> txtPackSize.requestFocus());
        txtPackSize.setOnAction(event -> txtUnitPrice.requestFocus());
        txtUnitPrice.setOnAction(event -> txtQtyOnHand.requestFocus());
        txtQtyOnHand.setOnAction(event -> btnSaveOrUpdate.fire());
//
    }
//
    private void initialUi() {
        clear(txtQtyOnHand,txtUnitPrice,txtPackSize,txtDescription,txtItemCode);
        setDisable(true,txtItemCode,txtDescription,txtPackSize,txtUnitPrice,txtQtyOnHand);
        btnSaveOrUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }
//
    private void loadAllItem() {
        tblItem.getItems().clear();
        try {
            ArrayList<ItemDTO> allItem = itemBO.getAllItem();
            for (ItemDTO dto : allItem) {
                tblItem.getItems().add(new ItemTM(dto.getItemCode(),dto.getDescription(),dto.getPackSize(),dto.getUnitPrice(),dto.getQtyOnHand()));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
    public void newItemOnAction(ActionEvent actionEvent) {
        setDisable(false,txtItemCode,txtDescription,txtPackSize,txtUnitPrice,txtQtyOnHand);
        clear(txtItemCode,txtDescription,txtUnitPrice,txtPackSize,txtQtyOnHand);
        txtItemCode.setText(generateNewId());
        txtItemCode.setEditable(false);
        txtDescription.requestFocus();
        btnSaveOrUpdate.setDisable(false);
        btnSaveOrUpdate.setText("Save");
        tblItem.getSelectionModel().clearSelection();
    }
//
    private String generateNewId() {
        try {
            return itemBO.generateNewId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (tblItem.getItems().isEmpty()) {
            return "I-0001";
        } else {
            String id = getLastItemId();
            int newCustomerId = Integer.parseInt(id.replace("I", "")) + 1;
            return String.format("I-%04d", newCustomerId);
        }
    }
//
    private void setDisable(boolean b, JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.setDisable(b);
        }
    }
//
    private void clear(JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.clear();
        }
    }
    private String getLastItemId() {
        List<ItemTM> tempItemList = new ArrayList<>(tblItem.getItems());
        Collections.sort(tempItemList);
        return tempItemList.get(tempItemList.size() - 1).getItemCode();
    }
//
    @FXML
    public void ItemSaveOrUpdateOnAction(ActionEvent actionEvent) {
        String code = txtItemCode.getText();
        String description = txtDescription.getText();
        String packSize = txtPackSize.getText();
        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText()).setScale(2);

        if (!description.matches("^[A-z0-9 ]{5,50}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid description").show();
            txtDescription.requestFocus();
            return;
        } else if (!unitPrice.toString().matches("^([0-9]+)$|([0-9]+[.][0-9]{2})$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid unit price").show();
            txtUnitPrice.requestFocus();
            return;
        } else if (!packSize.matches("^[A-z0-9 ]{5,20}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid pack size").show();
            txtQtyOnHand.requestFocus();
            return;
        }else if (!txtQtyOnHand.getText().matches("^\\d+$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty on hand").show();
            txtQtyOnHand.requestFocus();
            return;
        }

        if (btnSaveOrUpdate.getText().equalsIgnoreCase("save")) {
            try {
                boolean existItem = itemBO.exitsItem(code);
                if (existItem) {
                    new Alert(Alert.AlertType.ERROR, code + " already exists").show();
                }
                boolean saveItem = itemBO.saveItem(new ItemDTO(code, description, packSize, unitPrice, qtyOnHand));
                if (saveItem) {

                }
                tblItem.getItems().add(new ItemTM(code, description, packSize, unitPrice, qtyOnHand));

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                boolean existItem = itemBO.exitsItem(code);
                if (!existItem) {
                    new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + code).show();
                }

                boolean updateItem = itemBO.updateItem(new ItemDTO(code, description, packSize, unitPrice, qtyOnHand));
                if (updateItem) {

                }
                ItemTM selectedItem = tblItem.getSelectionModel().getSelectedItem();
                selectedItem.setDescription(description);
                selectedItem.setPackSize(packSize);
                selectedItem.setUnitPrice(unitPrice);
                selectedItem.setQtyOnHand(qtyOnHand);

                tblItem.refresh();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        btnNew.fire();
    }
//
    @FXML
    public void ItemRemoveOnAction(ActionEvent actionEvent) {
        String code = tblItem.getSelectionModel().getSelectedItem().getItemCode();
        try {
            boolean existItem = itemBO.exitsItem(code);
            if (!existItem) {
                new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + code).show();
            }

            boolean deleteItem = itemBO.deleteItem(code);
            if (deleteItem) {

            }

            tblItem.getItems().remove(tblItem.getSelectionModel().getSelectedItem());
            tblItem.getSelectionModel().clearSelection();
            initialUi();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item " + code).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//
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
        }
    }
}
