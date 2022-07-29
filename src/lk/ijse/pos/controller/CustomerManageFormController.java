package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.CustomerBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.view.tm.CustomerTM;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerManageFormController {
    private final CustomerBO customerBO = (CustomerBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CUSTOMER);

    public AnchorPane root;
    public JFXButton btnNew;
    public JFXTextField txtId;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtTitle;
    public JFXTextField txtPostalCode;
    public JFXTextField txtCity;
    public JFXTextField txtProvince;
    public JFXButton btnSaveOrUpdate;
    public JFXButton btnDelete;
    public TableView<CustomerTM> tblCustomer;

    public void initialize() throws SQLException, ClassNotFoundException {
//
        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblCustomer.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("city"));
        tblCustomer.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("province"));
        tblCustomer.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("postalCode"));
//
        initialUi();
//
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            btnSaveOrUpdate.setText(newValue != null ? "Update" : "Save");
            btnSaveOrUpdate.setDisable(newValue == null);

            if (newValue != null) {
                txtId.setText(newValue.getId());
                txtName.setText(newValue.getName());
                txtAddress.setText(newValue.getAddress());
                txtTitle.setText(newValue.getTitle());
                txtCity.setText(newValue.getCity());
                txtProvince.setText(newValue.getProvince());
                txtPostalCode.setText(newValue.getPostalCode());

                setDisable(false,txtId,txtName,txtAddress,txtTitle,txtCity,txtProvince,txtPostalCode);
            }
        });
//
        loadAllCustomer();
//
        txtName.setOnAction(event -> txtAddress.requestFocus());
        txtAddress.setOnAction(event -> txtTitle.requestFocus());
        txtTitle.setOnAction(event -> txtPostalCode.requestFocus());
        txtPostalCode.setOnAction(event -> txtCity.requestFocus());
        txtCity.setOnAction(event -> txtProvince.requestFocus());
        txtProvince.setOnAction(event -> btnSaveOrUpdate.fire());
//
    }
//
    private void initialUi() {
        setDisable(true,txtId,txtName,txtAddress,txtTitle,txtPostalCode,txtCity,txtProvince);
        clear(txtId,txtName,txtAddress,txtTitle,txtPostalCode,txtCity,txtProvince);
        btnSaveOrUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }
//
    private void loadAllCustomer() throws SQLException, ClassNotFoundException {
        tblCustomer.getItems().clear();
        try {
            ArrayList<CustomerDTO> AllCustomer = customerBO.getAllCustomer();
            for (CustomerDTO customer : AllCustomer) {
                tblCustomer.getItems().add(new CustomerTM(customer.getId(),customer.getTitle(),customer.getName(),customer.getAddress(),customer.getCity(),customer.getProvince(),customer.getPostalCode()));
            }
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, ""+e.getMessage()).show();
        }
    }
//
    @FXML
    public void navigateToHome(MouseEvent event) throws IOException {
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/cashier-form.fxml"))));
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }
//
    @FXML
    public void newCustomerOnAction(ActionEvent actionEvent) {

        setDisable(false,txtName,txtAddress,txtTitle,txtCity,txtProvince,txtPostalCode);

        clear(txtId,txtName,txtAddress,txtTitle,txtProvince,txtPostalCode,txtCity);
        txtId.setText(generateNewId());
        txtName.requestFocus();

        btnSaveOrUpdate.setDisable(false);
        btnSaveOrUpdate.setText("Save");
        tblCustomer.getSelectionModel().clearSelection();
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
//
    private String generateNewId() {
        try {
            return customerBO.generateNewId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new id " + e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (tblCustomer.getItems().isEmpty()) {
            return "C-0001";
        } else {
            String id = getLastCustomerId();
            int newCustomerId = Integer.parseInt(id.replace("C", "")) + 1;
            return String.format("C-%04d", newCustomerId);
        }
    }
//
    private String getLastCustomerId() {
        List<CustomerTM> tempCustomersList = new ArrayList<>(tblCustomer.getItems());
        Collections.sort(tempCustomersList);
        return tempCustomersList.get(tempCustomersList.size() - 1).getId();
    }
//
    @FXML
    public void customerSaveOrUpdateOnAction(ActionEvent actionEvent) {
        String id = txtId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String title = txtTitle.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String postalCode = txtPostalCode.getText();

        if (!name.matches("^[A-z ]{5,30}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid name. (5-30) characters only.").show();
            txtName.requestFocus();txtName.selectAll();
            return;
        } else if (!address.matches("^[A-z \\d]{5,30}$")) {
            new Alert(Alert.AlertType.ERROR, "Address should be at (5-30) A-z characters only").show();
            txtAddress.requestFocus();txtAddress.selectAll();
            return;
        } else if (!title.matches("^[A-z \\d]{3,5}$")) {
            new Alert(Alert.AlertType.ERROR, "Title should be at (3-5) characters only").show();
            txtTitle.requestFocus();txtTitle.selectAll();
            return;
        } else if (!city.matches("^[A-z \\d]{5,20}$")) {
            new Alert(Alert.AlertType.ERROR, "City should be at (5-20) A-z characters only").show();
            txtCity.requestFocus();txtCity.selectAll();
            return;
        } else if (!province.matches("^[A-z \\d]{5,20}$")) {
            new Alert(Alert.AlertType.ERROR, "Province should be at (5-20) A-z characters only").show();
            txtProvince.requestFocus();txtProvince.selectAll();
            return;
        } else if (!postalCode.matches("^[A-z \\d]{5,9}$")) {
            new Alert(Alert.AlertType.ERROR, "Postal Code should be at (5-9) characters only").show();
            txtPostalCode.requestFocus();txtPostalCode.selectAll();
            return;
        }

        if (btnSaveOrUpdate.getText().equalsIgnoreCase("save")) {
            try {
                boolean existCustomer = customerBO.exitsCustomer(id);
                if (existCustomer) {
                    new Alert(Alert.AlertType.ERROR, id + " already exists").show();
                }
                boolean saveCustomer = customerBO.saveCustomer(new CustomerDTO(id, title, name, address, city, province, postalCode));
                if (saveCustomer){

                }
                tblCustomer.getItems().add(new CustomerTM(id,title,name,address,city,province,postalCode));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed " + e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }

        } else {
            try {
                boolean existCustomer = customerBO.exitsCustomer(id);
                if (!existCustomer) {
                    new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + id).show();
                }
                boolean updateCustomer = customerBO.updateCustomer(new CustomerDTO(id, title, name, address, city, province, postalCode));
                if (updateCustomer){
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed " + id + e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }

            CustomerTM selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
            selectedCustomer.setTitle(title);
            selectedCustomer.setName(name);
            selectedCustomer.setAddress(address);
            selectedCustomer.setCity(city);
            selectedCustomer.setProvince(province);
            selectedCustomer.setPostalCode(postalCode);
            tblCustomer.refresh();
        }

        btnNew.fire();
    }
//
    @FXML
    public void customerRemoveOnAction(ActionEvent actionEvent) {
        String id = tblCustomer.getSelectionModel().getSelectedItem().getId();
        try {
            boolean existCustomer = customerBO.exitsCustomer(id);
            if (!existCustomer) {
                new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + id).show();
            }
            boolean deleteCustomer = customerBO.deleteCustomer(id);
            if (deleteCustomer) {

            }
            tblCustomer.getItems().remove(tblCustomer.getSelectionModel().getSelectedItem());
            tblCustomer.getSelectionModel().clearSelection();
            initialUi();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer " + id).show();
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

