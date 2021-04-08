import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.*;

public class mainGraphic extends Application {

    private static IEmergencyService emergencyService;
    private final TableView<ServiceRecordAdapter> tableView = new TableView<>();

    static {
        try {
            emergencyService = new EmergencyService("Records");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ObservableList<ServiceRecordAdapter> data;
    public mainGraphic() {
        List<ServiceRecordInfo> temp=emergencyService.getAllRecords();
        prepareData(temp);
    }

    private void prepareData(List<ServiceRecordInfo> temp) {
        List<ServiceRecordAdapter> list = new ArrayList<>();

        for (ServiceRecordInfo serviceRecordInfo : temp) {
            list.add(new ServiceRecordAdapter(serviceRecordInfo));
        }

        data = FXCollections.observableArrayList(list);
    }

    private static void alert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alert");
        alert.setContentText(message);

        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setScene(configureMainMenu());
        primaryStage.show();
    }

    private Scene configureMainMenu(){
        tableView.setEditable(true);
        Callback<TableColumn<ServiceRecordAdapter,String>, TableCell<ServiceRecordAdapter,String>> editableTextCellFactory = p -> new TextFieldTableCell<>(new StringConverter<String>() {

            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });

        TableColumn<ServiceRecordAdapter, String> userName = new TableColumn<>("Caller name");
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<ServiceRecordAdapter, String> userMobile = new TableColumn<>("Caller mobile");
        userMobile.setCellValueFactory(new PropertyValueFactory<>("userMobile"));

        TableColumn<ServiceRecordAdapter, Date> recordTime = new TableColumn<>("Time");
        recordTime.setCellValueFactory(new PropertyValueFactory<>("recordTime"));

        Callback<TableColumn<ServiceRecordAdapter, Boolean>, TableCell<ServiceRecordAdapter, Boolean>> booleanCellFactory = p -> new BooleanCell();

        TableColumn<ServiceRecordAdapter, Boolean> fire = new TableColumn<>("Fire");
        fire.setCellValueFactory(new PropertyValueFactory<>("fire"));
        fire.setCellFactory(booleanCellFactory);
        fire.setOnEditCommit(x-> x.getRowValue().setFire(x.getNewValue())); //lamda to save (on commit) new description to a file

        TableColumn<ServiceRecordAdapter, Boolean> ambulance = new TableColumn<>("Ambulance");
        ambulance.setCellValueFactory(new PropertyValueFactory<>("ambulance"));
        ambulance.setCellFactory(booleanCellFactory);
        ambulance.setOnEditCommit(x-> x.getRowValue().setAmbulance(x.getNewValue())); //lamda to save (on commit) new description to a file

        TableColumn<ServiceRecordAdapter, Boolean> police = new TableColumn<>("Police");
        police.setCellValueFactory(new PropertyValueFactory<>("police"));
        police.setCellFactory(booleanCellFactory);
        police.setOnEditCommit(x-> x.getRowValue().setPolice(x.getNewValue())); //lamda to save (on commit) new description to a file

        TableColumn<ServiceRecordAdapter, String> description = new TableColumn<>("Description");
        description.setCellValueFactory(new PropertyValueFactory<>("recordDescription"));
        description.setCellFactory(editableTextCellFactory);
        description.setOnEditCommit(x-> x.getRowValue().setRecordDescription(x.getNewValue())); //lamda to save (on commit) new description to a file


        tableView.getColumns().addAll(userName,userMobile,recordTime,fire,ambulance,police,description);
        tableView.setItems(data);
        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(tableView);

        Button addNewRecordButton = new Button();
        addNewRecordButton.setText("Add");
        addNewRecordButton.setMinWidth(100);
        addNewRecordButton.setOnAction(e -> {
            addMenuInitialisation();
            tableView.refresh();
        });


        Button deleteRecordButton = new Button();
        deleteRecordButton.setText("Delete");
        deleteRecordButton.setMinWidth(100);
        deleteRecordButton.setOnAction(e -> {
            ServiceRecordAdapter selected = tableView.getSelectionModel().getSelectedItem();
            if(selected==null){
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete");
            alert.setContentText("Would you like to delete the record with this number: "+selected.getUserMobile()+"?");
            Optional<ButtonType> result = alert.showAndWait();
            if(!result.isPresent() || result.get()!=ButtonType.OK){
                return;
            }
            ServiceRecordInfo current = selected.wrapped;
            emergencyService.deleteRecord(current);
            data.remove(selected);
            tableView.refresh();
        });

        VBox filter = new VBox(10);
        Label filterLabel = new Label("Enter mobile or service");
        TextField filterField = new TextField();
        filterField.setMinSize(100,20);
        filterField.setMaxSize(200,50);

        Button refreshButton = new Button();
        refreshButton.setText("Refresh");
        refreshButton.setMinWidth(100);
        refreshButton.setOnAction(e -> {
            String filterValue = filterField.getText();
            List<ServiceRecordInfo> list;

            if(filterValue.isEmpty()){
                list= emergencyService.getAllRecords();
            } else if(isMobileValid(filterValue)){
                list= emergencyService.getByMobile(filterValue);
            } else if(filterValue.toLowerCase(Locale.ROOT).equals(ServiceType.Fire.toString().toLowerCase(Locale.ROOT))){
                list = emergencyService.getByService(ServiceType.Fire);
            } else if(filterValue.toLowerCase(Locale.ROOT).equals(ServiceType.Ambulance.toString().toLowerCase(Locale.ROOT))){
                list = emergencyService.getByService(ServiceType.Ambulance);
            } else if(filterValue.toLowerCase(Locale.ROOT).equals(ServiceType.Police.toString().toLowerCase(Locale.ROOT))){
                list = emergencyService.getByService(ServiceType.Police);
            } else{
                return;
            }

            prepareData(list);
            tableView.setItems(data);
            tableView.refresh();
        });

        filter.getChildren().addAll(filterLabel,filterField,refreshButton);

        Button exitButton = new Button();
        exitButton.setText("Exit");
        exitButton.setMinWidth(100);
        exitButton.setOnAction(e -> Platform.exit());

        VBox buttonsGroup=new VBox(10);
        buttonsGroup.getChildren().addAll(filter,addNewRecordButton,deleteRecordButton,exitButton);

        borderPane.setRight(buttonsGroup);

        return new Scene(borderPane,750, 500);
    }

    private void addMenuInitialisation(){
        Stage stage = new Stage();
        stage.setTitle("Add new record");

        VBox addGroup=new VBox(10);
        addGroup.setPadding(new Insets(5));
        GridPane grid = new GridPane();

        Label mobileLabel = new Label("Caller mobile");
        TextField mobileField = new TextField();
        mobileField.setMinSize(100,20);
        mobileField.setMaxSize(400,30);

        Label nameLabel = new Label("Caller name");
        TextField nameField = new TextField();
        nameField.setMinSize(100,20);
        nameField.setMaxSize(400,30);

        Label servicesLabel = new Label("Services requested");
        servicesLabel.setMinWidth(120);
        HBox checkBoxGroup = new HBox(10);
        CheckBox fire = new CheckBox("Fire");
        CheckBox ambulance = new CheckBox("Ambulance");
        CheckBox police = new CheckBox("Police");
        checkBoxGroup.getChildren().addAll(fire,ambulance,police);

        Label descriptionLabel = new Label("Call notes");
        TextArea descriptionField = new TextArea();
        descriptionField.setMinSize(200,30);

        Button submit = new Button();
        submit.setText("Submit");
        submit.setOnAction(x->{
            String name = nameField.getText();
            String mobile = mobileField.getText();
            String description = descriptionField.getText();
            Date date = new Date();
            List<ServiceType> services = new ArrayList<>();
            if(fire.isSelected()){
                services.add(ServiceType.Fire);
            }
            if(ambulance.isSelected()){
                services.add(ServiceType.Ambulance);
            }
            if(police.isSelected()){
                services.add(ServiceType.Police);
            }

            if(name.equals("") || !isMobileValid(mobile) || services.isEmpty() || description.equals("")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Something is not filled");
                alert.setContentText("Some of the fields are filled incorrectly.");
                alert.showAndWait();
            } else {
                IServiceRecord temp = new ServiceRecord(name,mobile,date,services,description);
                try {
                    ServiceRecordAdapter rec = new ServiceRecordAdapter(emergencyService.addRecord(temp));
                    data.add(rec);
                    stage.close();
                } catch (IOException e) {
                    alert(e.getMessage());
                }
            }
        });

        grid.add(mobileLabel,0,0);
        grid.add(mobileField,1,0);
        grid.add(nameLabel,0,1);
        grid.add(nameField,1,1);
        grid.add(servicesLabel,0,2);
        grid.add(checkBoxGroup,1,2);
        grid.add(descriptionLabel,0,3);
        grid.add(descriptionField,1,3);

        addGroup.getChildren().addAll(grid,submit);
        stage.setScene(new Scene(addGroup,400,300));
        stage.show();
    }

    private static boolean isMobileValid(String mobile){
        return !mobile.equals("") && mobile.matches("\\+?[0-9][0-9\\-]+") && mobile.length() <= 25;
    }

    public class ServiceRecordAdapter{
        private final ServiceRecordInfo wrapped;

//        public ServiceRecordAdapter(){
//            IServiceRecord temp= new ServiceRecord();
//            wrapped = new KeyValue();
//        }

        public ServiceRecordAdapter(ServiceRecordInfo serviceRecordInfoIn){
            wrapped= serviceRecordInfoIn;
        }

        public String getUserName() {
            return wrapped.getRecord().getCallerName();
        }

        public String getUserMobile() {
            return wrapped.getRecord().getPhoneNumber();
        }

        public Date getRecordTime() {
            return wrapped.getRecord().getRecordTime();
        }

        public List<ServiceType> getRecordServices() {
            return wrapped.getRecord().getRequestedServices();
        }

        public boolean getFire() {
            return wrapped.getRecord().getRequestedServices().contains(ServiceType.Fire);
        }

        public void setFire(boolean value){
            if(value){
                if(!getFire()){
                    wrapped.getRecord().getRequestedServices().add(ServiceType.Fire);
                }
            } else {
                if(getFire()){
                    wrapped.getRecord().getRequestedServices().remove(ServiceType.Fire);
                }
            }
            save();
        }

        public boolean getAmbulance() {
            return wrapped.getRecord().getRequestedServices().contains(ServiceType.Ambulance);
        }

        public void setAmbulance(boolean value){
            if(value){
                if(!getAmbulance()){
                    wrapped.getRecord().getRequestedServices().add(ServiceType.Ambulance);
                }
            } else {
                if(getAmbulance()){
                    wrapped.getRecord().getRequestedServices().remove(ServiceType.Ambulance);
                }
            }
            save();
        }

        public boolean getPolice() {
            return wrapped.getRecord().getRequestedServices().contains(ServiceType.Police);
        }

        public void setPolice(boolean value){
            if(value){
                if(!getPolice()){
                    wrapped.getRecord().getRequestedServices().add(ServiceType.Police);
                }
            } else {
                if(getPolice()){
                    wrapped.getRecord().getRequestedServices().remove(ServiceType.Police);
                }
            }
            save();
        }

        public String getRecordDescription() {
            return wrapped.getRecord().getRecordDescription();
        }

        public void setRecordDescription(String newDescription) {
            wrapped.getRecord().updateRecordDescription(newDescription);
            save();

        }

        private void save() {
            try {
                emergencyService.updateRecord(wrapped);
            } catch (IOException e){
                alert(e.getMessage());
            }
        }
    }

    class BooleanCell extends TableCell<ServiceRecordAdapter, Boolean> {
        private final CheckBox checkBox;
        public BooleanCell() {
            checkBox = new CheckBox();
            checkBox.setDisable(true);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if(isEditing())
                    commitEdit(newValue != null && newValue);
            });
            this.setGraphic(checkBox);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.setEditable(true);
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (isEmpty()) {
                return;
            }
            checkBox.setDisable(false);
            checkBox.requestFocus();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            checkBox.setDisable(true);
        }

        public void commitEdit(Boolean value) {
            super.commitEdit(value);
            checkBox.setDisable(true);
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!isEmpty()) {
                checkBox.setSelected(item);
            }
        }
    }
}
