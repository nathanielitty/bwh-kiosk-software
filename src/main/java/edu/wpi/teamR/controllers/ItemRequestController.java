package edu.wpi.teamR.controllers;

import edu.wpi.teamR.Main;
import edu.wpi.teamR.datahandling.ShoppingCart;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import edu.wpi.teamR.requestdb.*;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import oracle.ucp.common.FailoverStats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class ItemRequestController {

//    @FXML
//    private ComboBox<String> itemFilterByComboBox;
    @FXML
    private TextField itemMaxField;
    @FXML
    private TextField itemMinField;
    @FXML
    private TableView<AvailableItem> itemTable;
    @FXML
    private TableColumn<AvailableItem, String> itemNameColumn;
    @FXML
    private TableColumn<AvailableItem, String> itemDetailsColumn;
    @FXML
    private TableColumn<AvailableItem, Double> itemPriceColumn;
    @FXML
    private TableColumn<AvailableItem, Void> itemQuantityColumn;
    @FXML
    private TableColumn<AvailableItem, Void> itemAddToCartColumn;
    @FXML
    private Text itemTentoTwenty;
    @FXML
    private Text itemThirtytoForty;
    @FXML
    private Text itemTwentytoThirty;
    @FXML
    private MFXComboBox<RequestType> itemTypeBox;
    @FXML
    private Text itemZerotoTen;
    @FXML
    private Text itemThirtyPlus;

    private RequestType type = RequestType.Meal;
    private Double lowerBound = null;
    private Double upperBound = null;
    private SortOrder sortOrder = SortOrder.unsorted;
    @FXML
    private Button minMaxGoButton;

    private  ArrayList<AvailableItem> items;

    private ObservableList<AvailableItem> obsItems;



    @FXML public void initialize(){
        ObservableList<RequestType> itemTypeList = FXCollections.observableArrayList();
        for (RequestType type : RequestType.values()) {itemTypeList.add(type);} //add all request types to combo box
        itemTypeBox.setItems(itemTypeList);

//        itemFilterByComboBox.setItems(FXCollections.observableArrayList("Unsorted", "Price: Low to High", "Price: High to Low"));

        itemZerotoTen.setOnMouseEntered(event -> textHover(itemZerotoTen));
        itemZerotoTen.setOnMouseExited(event -> textStopHover(itemZerotoTen));
        itemZerotoTen.setOnMouseClicked(event -> {this.lowerBound = 0.0; this.upperBound = 10.0; regenerateTable();});

        itemTentoTwenty.setOnMouseEntered(event -> textHover(itemTentoTwenty));
        itemTentoTwenty.setOnMouseExited(event -> textStopHover(itemTentoTwenty));
        itemTentoTwenty.setOnMouseClicked(event -> {this.lowerBound = 10.0; this.upperBound = 20.0; regenerateTable();});

        itemTwentytoThirty.setOnMouseEntered(event -> textHover(itemTwentytoThirty));
        itemTwentytoThirty.setOnMouseExited(event -> textStopHover(itemTwentytoThirty));
        itemTwentytoThirty.setOnMouseClicked(event -> {this.lowerBound = 20.0; this.upperBound = 30.0; regenerateTable();});


        itemThirtytoForty.setOnMouseEntered(event -> textHover(itemThirtytoForty));
        itemThirtytoForty.setOnMouseExited(event -> textStopHover(itemThirtytoForty));
        itemThirtytoForty.setOnMouseClicked(event -> {this.lowerBound = 30.0; this.upperBound = 40.0; regenerateTable();});


        itemThirtyPlus.setOnMouseEntered(event -> textHover(itemThirtyPlus));
        itemThirtyPlus.setOnMouseExited(event -> textStopHover(itemThirtyPlus));
        itemThirtyPlus.setOnMouseClicked(event -> {this.lowerBound = 30.0; this.upperBound = null; regenerateTable();});


        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        itemDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("imageReference"));

//        itemFilterByComboBox.setOnAction(event -> {
//            String comboType = itemFilterByComboBox.getSelectionModel().getSelectedItem();
//            switch(comboType){
//                case "Price: Low to High":
//                    this.sortOrder = SortOrder.lowToHigh;
//                    break;
//                case "Price: High to Low":
//                    this.sortOrder = SortOrder.highToLow;
//                    break;
//                default: //unsorted
//                    this.sortOrder = SortOrder.unsorted;
//            }
//            regenerateTable();
//        });

        itemTypeBox.setOnAction(event -> {
            RequestType comboType = itemTypeBox.getSelectionModel().getSelectedItem();
            this.type = comboType;
            regenerateTable();
        });

        minMaxGoButton.setOnAction(event -> {
            this.lowerBound = Double.valueOf(itemMinField.getText());
            this.upperBound = Double.valueOf(itemMaxField.getText());
            regenerateTable();
        });

        addButtonToTable();

        try {
            items = RequestDatabase.getInstance().getAvailableItemsByTypeWithinRangeSortedByPrice(this.type, this.upperBound, this.lowerBound, this.sortOrder);
            obsItems = FXCollections.observableArrayList(items);
            itemTable.setItems(obsItems);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void addButtonToTable() {
        Callback<TableColumn<AvailableItem, Void>, TableCell<AvailableItem, Void>> cellFactory = new Callback<TableColumn<AvailableItem, Void>, TableCell<AvailableItem, Void>>() {
            @Override
            public TableCell<AvailableItem, Void> call(final TableColumn<AvailableItem, Void> param) {
                return new TableCell<AvailableItem, Void>() {

                    private final Button btn = new Button();
                    {
                        btn.getStyleClass().add("food_furniture-clear-button");
                        btn.setText("Add to Cart");
                        btn.setOnAction((ActionEvent event) -> {
                            AvailableItem data = getTableView().getItems().get(getIndex());

                            try {
                                ShoppingCart cartInstance = ShoppingCart.getInstance();
                                cartInstance.addItem(data, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
//                        btn.setOnMouseClicked(event -> {
//                            btn.set("primaryLightGrey");
//                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        itemAddToCartColumn.setCellFactory(cellFactory);
    }

    private void textHover(Text text){
        text.setFill(Color.WHITE);
    }

    private void textStopHover(Text text){
        text.setFill(Color.valueOf("#BDBDBD"));
    }

    private void regenerateTable(){
        try {
            items = RequestDatabase.getInstance().getAvailableItemsByTypeWithinRangeSortedByPrice(this.type, this.upperBound, this.lowerBound, this.sortOrder);
            obsItems = FXCollections.observableArrayList(items);
            itemTable.setItems(obsItems);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
