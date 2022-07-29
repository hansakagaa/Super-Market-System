package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.PurchaseOrderBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import lk.ijse.pos.dto.OrderDTO;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDetailsDTO;
import lk.ijse.pos.view.tm.CartTM;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaceOrderFormController {
    private final PurchaseOrderBO purchaseOrderBO = (PurchaseOrderBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PURCHASE_ORDER);
    public AnchorPane root;
    public JFXTextField txtId;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtTitle;
    public JFXTextField txtPostalCode;
    public JFXTextField txtCity;
    public JFXTextField txtProvince;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtTotal;
    public JFXTextField txtDiscount;
    public JFXTextField txtCash;
    public JFXTextField txtBalance;
    public JFXTextField txtOrderQty;
    public ComboBox<String> cmbCustomerId;
    public ComboBox<String> cmbItemCode;
    public Label lblDate;
    public Label lblOrderId;
    public JFXButton btnCancel;
    public JFXButton btnSave;
    public ImageView imgAddCart;
    public ImageView imgNewCustomer;
    public TableView<CartTM> tblList;
    private String orderId;

    public void initialize(){
//
        tblList.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblList.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tblList.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        tblList.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblList.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("total"));
        TableColumn<CartTM, Button> lastCol = (TableColumn<CartTM, Button>) tblList.getColumns().get(6);

        lastCol.setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");

            btnDelete.setOnAction(event -> {
                tblList.getItems().remove(param.getValue());
                tblList.getSelectionModel().clearSelection();
                calculateTotal();
                enableOrDisablePlaceOrderButton();
            });

            return new ReadOnlyObjectWrapper<>(btnDelete);
        });
//
        orderId = generateNewOrderId();
        lblOrderId.setText(orderId);
        lblDate.setText(LocalDate.now().toString());
        cmbCustomerId.setDisable(false);
        txtId.setDisable(true);
        btnSave.setDisable(true);

        setFocusTraversable(txtName,txtAddress,txtTitle,txtCity,txtPostalCode,txtProvince,txtDescription,txtUnitPrice,txtQtyOnHand);
        setEditable(true, txtOrderQty);

        txtOrderQty.setOnAction(event -> navigateToAddCart(null));

        imgAddCart.setDisable(true);
//
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            enableOrDisablePlaceOrderButton();

            if (newValue != null) {
                try {
                    boolean exits = purchaseOrderBO.exitsCustomer(newValue + "");

                    if (!exits) {
                        new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + newValue + "").show();
                    }

                    CustomerDTO dto = purchaseOrderBO.searchCustomer(newValue + "");

                    setEditable(false,txtName,txtAddress,txtTitle,txtPostalCode,txtCity,txtProvince);
                    txtName.setText(dto.getName());
                    txtAddress.setText(dto.getAddress());
                    txtTitle.setText(dto.getTitle());
                    txtPostalCode.setText(dto.getPostalCode());
                    txtCity.setText(dto.getCity());
                    txtProvince.setText(dto.getProvince());

                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                } catch (ClassNotFoundException e) {
                    new Alert(Alert.AlertType.ERROR, ""+e.getMessage()).show();
                }
            } else {
                clear(txtName,txtAddress,txtTitle,txtCity,txtPostalCode,txtProvince);
            }
        });
//
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newItemCode) -> {
            txtOrderQty.setEditable(newItemCode != null);
            imgAddCart.setDisable(newItemCode == null);

            if (newItemCode != null) {
                try {
                    boolean exits = purchaseOrderBO.exitsItem(newItemCode + "");
                    if (!exits) {
                    }
                    ItemDTO dto = purchaseOrderBO.searchItem(newItemCode + "");
                    txtDescription.setText(dto.getDescription());
                    txtPackSize.setText(dto.getPackSize());
                    txtUnitPrice.setText(dto.getUnitPrice().setScale(2).toString());
                    txtQtyOnHand.setText(dto.getQtyOnHand()+"");

                    Optional<CartTM> optOrderDetail = tblList.getItems().stream().filter(detail -> detail.getItemCode().equals(newItemCode)).findFirst();
                    txtQtyOnHand.setText((optOrderDetail.isPresent() ? dto.getQtyOnHand() - optOrderDetail.get().getOrderQty() : dto.getQtyOnHand()) + "");

                    txtOrderQty.requestFocus();

                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                } catch (ClassNotFoundException e) {
                    new Alert(Alert.AlertType.ERROR, ""+e.getMessage()).show();
                }

            } else {
                clear(txtDescription,txtPackSize,txtQtyOnHand,txtUnitPrice,txtOrderQty);
            }
        });
//
        tblList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedOrderDetail) -> {

            if (selectedOrderDetail != null) {
                cmbItemCode.setDisable(true);
                cmbItemCode.setValue(selectedOrderDetail.getItemCode());
                txtQtyOnHand.setText(Integer.parseInt(txtQtyOnHand.getText()) + selectedOrderDetail.getOrderQty() + "");
                txtOrderQty.setText(selectedOrderDetail.getOrderQty() + "");
            } else {
                cmbItemCode.setDisable(false);
                cmbItemCode.getSelectionModel().clearSelection();
                txtOrderQty.clear();
            }
        });
//
        loadAllCustomerIds();
        loadAllItemCodes();
//
        if (cmbCustomerId.getItems().isEmpty()){
            navigateToNewCustomer(null);
        }
//
        txtId.setOnAction(event -> txtName.requestFocus());
        txtName.setOnAction(event -> txtAddress.requestFocus());
        txtAddress.setOnAction(event -> txtTitle.requestFocus());
        txtTitle.setOnAction(event -> txtPostalCode.requestFocus());
        txtPostalCode.setOnAction(event -> txtCity.requestFocus());
        txtCity.setOnAction(event -> txtProvince.requestFocus());
//
    }
//
    private void setEditable(boolean b, JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.setEditable(b);
        }
    }

    private void clear(JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.clear();
        }
    }
    private void setFocusTraversable(JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.setFocusTraversable(false);
        }
    }


    private void loadAllCustomerIds() {
        try {
            ArrayList<CustomerDTO> dtoS = purchaseOrderBO.getAllCustomer();
            for (CustomerDTO dto : dtoS) {
                cmbCustomerId.getItems().add(dto.getId());
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load customer ids").show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
//
    private void loadAllItemCodes() {
        try {
            ArrayList<ItemDTO> dtoS = purchaseOrderBO.getAllItem();
            for (ItemDTO dto : dtoS) {
                cmbItemCode.getItems().add(dto.getItemCode());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, ""+e.getMessage()).show();
        }
    }
//
    private String generateNewOrderId() {
        try {
            return purchaseOrderBO.getOrderId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new order id").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "OI-001";
    }
//
    private void enableOrDisablePlaceOrderButton() {
        btnSave.setDisable(!((cmbCustomerId.getSelectionModel().getSelectedItem() != null || txtId.getText() != null) && !tblList.getItems().isEmpty()));

    }
//
    private void calculateTotal() {
        BigDecimal total = new BigDecimal(0);

        for (CartTM detail : tblList.getItems()) {
            total = total.add(detail.getTotal());
        }
        txtTotal.setText(total+" /=");
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
    public void navigateToNewCustomer(MouseEvent event) {
        cmbCustomerId.setDisable(true);
        cmbCustomerId.setVisible(false);
        txtId.setDisable(false);
        txtId.setText(generateNewCustomerId());

        txtName.requestFocus();
        txtName.setEditable(true);
        txtName.clear();
        txtAddress.setEditable(true);
        txtAddress.clear();
        txtTitle.setEditable(true);
        txtTitle.clear();
        txtCity.setEditable(true);
        txtCity.clear();
        txtProvince.setEditable(true);
        txtProvince.clear();
        txtPostalCode.setEditable(true);
        txtPostalCode.clear();
    }
//
    private String generateNewCustomerId() {
        try {
//            String newId = customerDAO.generateNewId();
            return purchaseOrderBO.getCustomerId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new id " + e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (cmbCustomerId.getItems().isEmpty()) {
            return "C-0001";
        } else {
            String id = getLastCustomerId();
            int newCustomerId = Integer.parseInt(id.replace("C", "")) + 1;
            return String.format("C-%04d", newCustomerId);
        }
    }
//
    private String getLastCustomerId() {
        List<String> tempCustomersList = new ArrayList<>(cmbCustomerId.getItems());
        Collections.sort(tempCustomersList);
        return tempCustomersList.get(tempCustomersList.size() - 1);
    }
//
    @FXML
    public void navigateToAddCart(MouseEvent event) {
        if (!txtOrderQty.getText().matches("\\d+") || Integer.parseInt(txtOrderQty.getText()) <= 0 ||
                Integer.parseInt(txtOrderQty.getText()) > Integer.parseInt(txtQtyOnHand.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty").show();
            txtOrderQty.requestFocus();
            txtOrderQty.selectAll();
            return;
        }

        String itemCode = cmbItemCode.getSelectionModel().getSelectedItem();
        String description = txtDescription.getText();
        String packSize = txtPackSize.getText();
        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText()).setScale(2);
        int qty = Integer.parseInt(txtOrderQty.getText());
        BigDecimal total = unitPrice.multiply(new BigDecimal(qty)).setScale(2);

        boolean exists = tblList.getItems().stream().anyMatch(detail -> detail.getItemCode().equals(itemCode));

        if (exists) {
            CartTM cartTM = tblList.getItems().stream().filter(detail -> detail.getItemCode().equals(itemCode)).findFirst().get();

            cartTM.setOrderQty(qty);
            total = new BigDecimal(cartTM.getOrderQty()).multiply(unitPrice).setScale(2);
            cartTM.setTotal(total);
            tblList.getSelectionModel().clearSelection();

            tblList.refresh();
        } else {
            tblList.getItems().add(new CartTM(itemCode, description, packSize, qty, unitPrice, total));
        }
        cmbItemCode.setDisable(false);
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        calculateTotal();
        enableOrDisablePlaceOrderButton();
    }
//
    @FXML
    public void cancelOrderOnAction(ActionEvent actionEvent){
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblList.getItems().clear();
        txtId.setDisable(true);
        cmbCustomerId.setDisable(false);
        cmbCustomerId.setVisible(true);
        clear(txtOrderQty,txtId,txtName,txtAddress,txtTitle,txtCity,txtProvince,txtPostalCode);
        calculateTotal();
    }
//
    @FXML
    public void saveOrderOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String id = cmbCustomerId.getValue();
        CustomerDTO customerDTO = new CustomerDTO(txtId.getText(), txtTitle.getText(), txtName.getText(), txtAddress.getText(), txtCity.getText(), txtProvince.getText(), txtPostalCode.getText());
        if (id == null){
            id = customerDTO.getId();
        }
        boolean exits = purchaseOrderBO.exitsCustomer(id);
        if (!exits){
            if (saveCustomer(customerDTO)) {
                new Alert(Alert.AlertType.INFORMATION, "Customer has been save successfully").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Customer has been save Unsuccessfully").show();
                return;
            }
        }

        boolean saveOrder = saveOrder(new OrderDTO(orderId, LocalDate.now(), id,
                tblList.getItems().stream().map(tm -> new OrderDetailsDTO(orderId, tm.getItemCode(), tm.getOrderQty(), tm.getUnitPrice()/**/)).collect(Collectors.toList())));

        if (saveOrder) {
            new Alert(Alert.AlertType.INFORMATION, "Order has been placed successfully").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Order has not been placed successfully").show();
        }
        orderId = generateNewOrderId();
        lblOrderId.setText(orderId);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblList.getItems().clear();
        txtId.setDisable(true);
        cmbCustomerId.setDisable(false);
        cmbCustomerId.setVisible(true);
        clear(txtOrderQty,txtId,txtName,txtAddress,txtTitle,txtCity,txtProvince,txtPostalCode);
        calculateTotal();
    }
//
    private boolean saveOrder(OrderDTO dto) {
        /*Transaction*/
        try {
            return purchaseOrderBO.purchaseOrder(dto);

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, ""+e.getMessage()).show();
        }
        return false;
    }
//
    private boolean saveCustomer(CustomerDTO dto)  {

        if (!dto.getName().matches("^[A-z ]{5,30}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid name. (5-30) characters only.").show();
            txtName.requestFocus();txtName.selectAll();
            return false;
        } else if (!dto.getAddress().matches("^[A-z \\d]{5,30}$")) {
            new Alert(Alert.AlertType.ERROR, "Address should be at (5-30) A-z characters only").show();
            txtAddress.requestFocus();txtAddress.selectAll();
            return false;
        } else if (!dto.getTitle().matches("^[A-z \\d]{3,5}$")) {
            new Alert(Alert.AlertType.ERROR, "Title should be at (3-5) characters only").show();
            txtTitle.requestFocus();txtTitle.selectAll();
            return false;
        } else if (!dto.getCity().matches("^[A-z \\d]{5,20}$")) {
            new Alert(Alert.AlertType.ERROR, "City should be at (5-20) A-z characters only").show();
            txtCity.requestFocus();txtCity.selectAll();
            return false;
        } else if (!dto.getProvince().matches("^[A-z \\d]{5,20}$")) {
            new Alert(Alert.AlertType.ERROR, "Province should be at (5-20) A-z characters only").show();
            txtProvince.requestFocus();txtProvince.selectAll();
            return false;
        } else if (!dto.getPostalCode().matches("^[A-z \\d]{5,9}$")) {
            new Alert(Alert.AlertType.ERROR, "Postal Code should be at (5-9) characters only").show();
            txtPostalCode.requestFocus();txtPostalCode.selectAll();
            return false;
        }

        try {
            boolean save = purchaseOrderBO.saveCustomer(new CustomerDTO(dto.getId(), dto.getTitle(), dto.getName(), dto.getAddress(), dto.getCity(), dto.getProvince(), dto.getPostalCode()));

            if (!save) {
                return false;
            }
        }catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to update the customer " + e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
//
    @FXML
    public void orderQtyKeyReleased(KeyEvent keyEvent) {
        if (!txtOrderQty.getText().matches("\\d+") || Integer.parseInt(txtOrderQty.getText()) <= 0 ||
                Integer.parseInt(txtOrderQty.getText()) > Integer.parseInt(txtQtyOnHand.getText())) {
            txtOrderQty.requestFocus();
            txtOrderQty.selectAll();
        }
    }
//
    @FXML
    public void cashBalanceKeyReleased(KeyEvent keyEvent) {
        if (!txtCash.getText().matches("\\d+") || Integer.parseInt(txtCash.getText()) <= 0) {
            txtCash.requestFocus();
            txtCash.selectAll();
            return;
        }
        BigDecimal cash = new BigDecimal(txtCash.getText());
        BigDecimal total = new BigDecimal(0);

        for (CartTM detail : tblList.getItems()) {
            total = total.add(detail.getTotal());
        }
        BigDecimal balance = cash.subtract(total);
        txtBalance.setText(String.valueOf(balance));
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
