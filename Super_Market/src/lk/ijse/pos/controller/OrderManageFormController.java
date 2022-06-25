package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.OrderManageBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailsDTO;
import lk.ijse.pos.view.tm.ItemTM;
import lk.ijse.pos.view.tm.OrderTM;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderManageFormController {
    private final OrderManageBO orderManage = (OrderManageBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ORDER_MANAGE);

    public AnchorPane root;
    public Label lblOrderId;
    public Label lblItemCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtTotal;
    public JFXTextField txtOrderQty;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtPresTotal;
    public JFXTextField txtNewTotal;
    public JFXTextField txtCash;
    public JFXTextField txtBalance;
    public JFXTextField txtOrderId;
    public JFXButton btnCancel;
    public JFXButton btnDelete;
    public JFXButton btnRemove;
    public JFXButton btnAdd;
    public JFXButton btnSave;
    public ComboBox<String> cmbCustomerId;
    public TableView<OrderTM> tblOrderId;
    public TableView<ItemTM> tblOrderDetails;
    private int orderQty = 0;
    private int qtyOnHand = 0;

    public void  initialize(){
//
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblOrderDetails.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("orderQty"));

        tblOrderId.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderID"));
//
        initialItem(true);
        tblOrderId.setDisable(true);
        tblOrderDetails.setDisable(true);
        lblItemCode.setText("");
        lblOrderId.setText("");
        btnDelete.setDisable(true);
//
        loadAllCustomerIds();
//
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, customerId) -> {
            //  enableOrDisableSaveButton();
            if (customerId != null) {
                tblOrderId.setDisable(false);
                tblOrderDetails.setDisable(true);
                tblOrderDetails.getItems().clear();
                initialItem();
                initialItem(true);
                try {
                    boolean exits = orderManage.exitsCustomer(customerId + "");
                    if (!exits) {
                        new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + customerId + "").show();
                    }
                    CustomerDTO customerDTO = orderManage.searchCustomer(customerId + "");

                    txtName.setText(customerDTO.getName());
                    txtAddress.setText(customerDTO.getAddress());
                    loadAllOrders(customerId + "");

                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, "Failed to find the customer " + customerId + "" + e).show();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                tblOrderId.setDisable(true);
                tblOrderDetails.getItems().clear();
                tblOrderDetails.setDisable(true);
                clear(txtName,txtAddress);
                initialItem(true);
                initialItem();
            }
        });
//
        tblOrderId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, orders) -> {
            if (orders != null) {
                initialItem();
                initialItem(true);
                tblOrderDetails.setDisable(false);
                lblOrderId.setText(orders.getOrderID());
                txtOrderId.setText(orders.getOrderID());
                loadAllItems(orders.getOrderID());
                btnDelete.setDisable(false);
                txtPresTotal.setText(String.valueOf(calculateAllTotal()));
            }else {
                tblOrderDetails.setDisable(true);
                initialItem();
                initialItem(true);
                btnDelete.setDisable(true);
                clear(txtNewTotal,txtPresTotal,txtCash,txtBalance);
            }
        });
//
        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, item) -> {
            if(item != null){
                initialItem(false);
                lblItemCode.setText(item.getItemCode());
                txtDescription.setText(item.getDescription());
                txtPackSize.setText(item.getPackSize());
                txtUnitPrice.setText(item.getUnitPrice().toString());
                txtQtyOnHand.setText(item.getQtyOnHand()+"");
                txtOrderQty.setText(item.getOrderQty()+"");
                calculateItemTotal();
                orderQty = item.getOrderQty();
                qtyOnHand = item.getQtyOnHand();
            }else {
                initialItem(true);
            }
        });
//
        txtOrderId.setOnAction(event -> setOrder());
        txtOrderQty.setOnAction(event -> btnAdd.fire());

    }
//
    private void initialItem(boolean b){
        setDisable(b,txtDescription,txtPackSize,txtUnitPrice,txtQtyOnHand,txtTotal,txtOrderQty);
        btnRemove.setDisable(b);
        btnAdd.setDisable(true);
    }
//
    private void initialItem(){
        lblItemCode.setText("");
        clear(txtDescription,txtPackSize,txtUnitPrice,txtQtyOnHand,txtTotal,txtOrderQty);
    }
//
    private void clearUi(){
        cmbCustomerId.getSelectionModel().clearSelection();
        clear(txtName,txtAddress,txtOrderId,txtNewTotal,txtPresTotal,txtCash,txtBalance);
        tblOrderId.getItems().clear();
        lblOrderId.setText("");
        tblOrderDetails.getItems().clear();
        initialItem();
    }
//
    private void setDisable(boolean b, JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.setEditable(b);
        }
    }
//
    private void clear(JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.clear();
        }
    }
//
    private void setFocusTraversable(JFXTextField... field){
        for (JFXTextField textField : field) {
            textField.setFocusTraversable(false);
        }
    }
//
    private void calculateItemTotal() {
        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtOrderQty.getText());
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(qty));
        txtTotal.setText(total+"");
    }
//
    private BigDecimal calculateAllTotal(){
        BigDecimal total = new BigDecimal(0);
        for (ItemTM detail : tblOrderDetails.getItems()) {
            total = total.add(BigDecimal.valueOf(detail.getOrderQty()).multiply(detail.getUnitPrice()));
        }
        return total;
    }
//
    private void loadAllItems(String orderID) {
        tblOrderDetails.getItems().clear();
        List<OrderDetailsDTO> dto = new ArrayList<>();
        try {
            ArrayList<OrderDetailsDTO> orderDetails = orderManage.getOrderDetails(orderID);
            for (OrderDetailsDTO details : orderDetails) {
                dto.add(new OrderDetailsDTO(orderID, details.getItemCode(), details.getOrderQty(), details.getDiscount()));
            }

            for (OrderDetailsDTO detail : dto) {
                ItemDTO item = orderManage.searchItem(detail.getItemCode());
                tblOrderDetails.getItems().add(new ItemTM(detail.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand(), detail.getOrderQty()));
            }
        }catch (NullPointerException e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, ""+ e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, " "+e.getMessage()).show();
        }
    }
//
    private void loadAllOrders(String id){
        tblOrderId.getItems().clear();
        try {
            boolean exitsCusId = orderManage.exitsCusId(id);
            if (!exitsCusId) {
                return;
            }
            ArrayList<OrderDTO> dtoS = orderManage.getOrderFromCusId(id);
            for (OrderDTO dto : dtoS) {

                tblOrderId.getItems().add(new OrderTM(dto.getOrderID()));
            }
        } catch (NullPointerException e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, ""+e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, " "+e.getMessage()).show();
        }
    }
//
    private void loadAllCustomerIds() {
        try {
            ArrayList<CustomerDTO> dtoS = orderManage.getAllCustomer();
            for (CustomerDTO dto : dtoS) {
                cmbCustomerId.getItems().add(dto.getId());
            }

        } catch (SQLException e) {
        new Alert(Alert.AlertType.ERROR, "Failed to load customer ids").show();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    }

//
    private void setOrder() {
        if (!txtOrderId.getText().matches("^(OI-)[0-9]{3}$")) {
            txtOrderId.requestFocus();
            txtOrderId.selectAll();
            return;
        }else {
            loadAllItems(txtOrderId.getText());
        }
        lblItemCode.setText("");
        clear(txtDescription,txtPackSize,txtUnitPrice,txtQtyOnHand,txtTotal,txtOrderQty);
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
    public void deleteOrderOnAction(ActionEvent actionEvent) {
        try {
            boolean delete = orderManage.deleteOrder(lblOrderId.getText());
            if (delete) {

            }
            tblOrderId.getItems().remove(tblOrderId.getSelectionModel().getSelectedItem());
            tblOrderId.getSelectionModel().clearSelection();

            lblOrderId.setText("");
            tblOrderDetails.getItems().clear();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item " + lblOrderId.getText()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//
    @FXML
    public void itemRemoveOnAction(ActionEvent actionEvent) {
        try {
            boolean delete = orderManage.deleteOrderDetails(lblOrderId.getText(), lblItemCode.getText());
            if (delete) {

            }
            tblOrderDetails.getItems().remove(tblOrderDetails.getSelectionModel().getSelectedItem());
            tblOrderDetails.getSelectionModel().clearSelection();

            initialItem();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item " + lblItemCode.getText()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//
    @FXML
    public void itemAddOnAction(ActionEvent actionEvent) {

        boolean exists = tblOrderDetails.getItems().stream().anyMatch(detail -> detail.getItemCode().equals(lblItemCode.getText()));

        if (exists) {
            ItemTM itemTM = tblOrderDetails.getItems().stream().filter(detail -> detail.getItemCode().equals(lblItemCode.getText())).findFirst().get();
            itemTM.setOrderQty(Integer.parseInt(txtOrderQty.getText()));
            itemTM.setQtyOnHand(Integer.parseInt(txtQtyOnHand.getText()));
            tblOrderDetails.getSelectionModel().clearSelection();
            tblOrderDetails.refresh();
        } else {
            tblOrderDetails.getItems().add(new ItemTM(lblItemCode.getText(), txtDescription.getText(), txtPackSize.getText(), new BigDecimal(txtUnitPrice.getText()), Integer.parseInt(txtQtyOnHand.getText()), Integer.parseInt(txtOrderQty.getText())));
        }
        setFocusTraversable(txtPresTotal,txtNewTotal,txtCash,txtBalance);
        txtNewTotal.setText(String.valueOf(calculateAllTotal()));
        txtBalance.setText(String.valueOf(Double.parseDouble(txtPresTotal.getText())-Double.parseDouble(txtNewTotal.getText())));
        initialItem();
    }
//
    @FXML
    public void cancelOrderEditOnAction(ActionEvent actionEvent) {
        clearUi();
    }
//
    @FXML
    public void saveOrderEditOnAction(ActionEvent actionEvent) {
        boolean saveOrderDetails = saveOrderDetails(
                tblOrderDetails.getItems().stream().map(tm -> new OrderDetailsDTO(lblOrderId.getText(), tm.getItemCode(), tm.getOrderQty(), tm.getUnitPrice()/**/)).collect(Collectors.toList())
        );
        if (saveOrderDetails) {
            new Alert(Alert.AlertType.INFORMATION, "Order Update has been placed successfully").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Order Update has not been placed successfully").show();
        }
        clearUi();
    }
//
    private boolean saveOrderDetails(List<OrderDetailsDTO> details) {
        try {
            for (OrderDetailsDTO detail : details) {
                boolean updateOrderDetails = orderManage.updateOrderDetails(detail);
                if (!updateOrderDetails) {
                    return false;
                }

                ItemDTO item = findItem(detail.getItemCode());
                item.setQtyOnHand(item.getQtyOnHand() + (orderQty-detail.getOrderQty()));

                boolean update = orderManage.updateItem(item);
                if (!update) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (SQLIntegrityConstraintViolationException e){
            new Alert(Alert.AlertType.ERROR, ""+e.getMessage()).show();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR," "+ e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,"  "+ e.getMessage()).show();
        }
        return false;
    }
//
    private ItemDTO findItem(String itemCode) {
        try {
            return orderManage.searchItem(itemCode);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find the Item " + itemCode, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
//
    @FXML
    public void orderQtyKeyReleased(KeyEvent keyEvent) {
        if (!txtOrderQty.getText().matches("\\d+") || Integer.parseInt(txtOrderQty.getText()) <= 0 ||
                Integer.parseInt(txtOrderQty.getText()) > orderQty) {
            btnAdd.setDisable(true);
            txtOrderQty.requestFocus();
            txtOrderQty.selectAll();
        }else {
            btnAdd.setDisable(false);
            calculateItemTotal();
            txtQtyOnHand.setText(qtyOnHand+(orderQty-Integer.parseInt(txtOrderQty.getText()))+"");
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
        BigDecimal total = new BigDecimal(txtNewTotal.getText());
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
