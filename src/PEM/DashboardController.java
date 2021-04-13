package PEM;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import loginGUI.DatabaseConnection;
import loginGUI.LoginController;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * This class handles the main dashboard scene window and
 * displays current balances, transaction tables and can add new transactions
 * to database.
 *
 * @author  Serena Li
 * @since   2021-04-13
 */

public class DashboardController implements Initializable {

    @FXML private TableView<TransactionTable> allTransactionsTableView;
    @FXML private TableColumn<TransactionTable, String> allTypeCol;
    @FXML private TableColumn<TransactionTable, String> allDescriptionCol;
    @FXML private TableColumn<TransactionTable, String> allAmountCol;
    @FXML private TableColumn<TransactionTable, String> allDateCol;
    @FXML private TableColumn<TransactionTable, String> allCategoryCol;


    // top buttons and labels
    @FXML private Label currentBalanceLabel;
    @FXML private Label shoppingLabel;
    @FXML private Label etsyLabel;
    @FXML private Label livingLabel;
    @FXML private Label educationLabel;
    @FXML private Label otherLabel;

    // add expense form
    @FXML private CheckBox expenseCheckBox;
    @FXML private CheckBox incomeCheckBox;
    @FXML private TextField descriptionTextField;
    @FXML private TextField amountTextField;
    @FXML private TextField dateTextField;
    @FXML private CheckBox shoppingCheckBox;
    @FXML private CheckBox livingCheckBox;
    @FXML private CheckBox otherCheckBox;
    @FXML private CheckBox etsyCheckBox;
    @FXML private CheckBox educationalCheckBox;
    @FXML private CheckBox jobCheckBox;
    @FXML private Label addExpenseLabel;
    private String expCategory;
    private String transactionType;

    // right hand menu buttons
    @FXML private Button logoutButton;
    @FXML private Button exitButton;

    // transaction tables
    private ObservableList<TransactionTable> oblist = FXCollections.observableArrayList(); // add elements to this list
    private Connection connection = DatabaseConnection.getConnection();
    private DatabaseConnection dbHandler;
    private ResultSet resultSet;

    // get the current user's ID
    int currentUserID = LoginController.getAccountUserID();


    // Menu buttons

    /**
     * Method exits dashboard and returns to login form
     * @param event ActionEvent when button is clicked
     */

    public void logoutButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }

    // exits entire program
    /**
     * Method exits entire program
     * @param event ActionEvent when button is clicked
     */

    public void exitButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    // add transaction form
    /**
     * Method submits a transaction to database when submit button is clicked
     * @param event ActionEvent when button is clicked
     */
    public void submitTransactionButtonOnAction(ActionEvent event) {
        // initialize transaction type base on user's selection
        if (incomeCheckBox.isSelected()) {
            transactionType = "income";
        } else if (expenseCheckBox.isSelected()) {
            transactionType = "expense";
        } else {
            transactionType = "null";
        }

        // see which category selected
        if (shoppingCheckBox.isSelected()) {
            expCategory = "shopping";
        } else if(livingCheckBox.isSelected()) {
            expCategory = "living";
        } else if (otherCheckBox.isSelected()) {
            expCategory = "other";
        } else if (etsyCheckBox.isSelected()) {
            expCategory = "etsy";
        } else if (educationalCheckBox.isSelected()) {
            expCategory = "educational";
        } else if (jobCheckBox.isSelected()) {
            expCategory = "job";
        } else {
            // set to null if no category is selected
            expCategory = "null";
        }

        boolean cond1 = (!descriptionTextField.getText().isBlank() &&
                (!amountTextField.getText().isBlank() && Double.parseDouble(amountTextField.getText()) != 0));
        boolean cond2 = (!dateTextField.getText().isBlank() && !expCategory.equals("null"));

        // if all fields are entered, then add transaction to database
        if ((cond1 && cond2) && !transactionType.equals("null")) {
                System.out.println(transactionType);
                addTransaction();
                // make form blank again
                descriptionTextField.clear();
                amountTextField.clear();
                dateTextField.clear();
                shoppingCheckBox.setSelected(false);
                livingCheckBox.setSelected(false);
                otherCheckBox.setSelected(false);
                etsyCheckBox.setSelected(false);
                educationalCheckBox.setSelected(false);
                expenseCheckBox.setSelected(false);
                incomeCheckBox.setSelected(false);

        } else {
            addExpenseLabel.setText("Please fill in all fields");
        }
        // refresh table
        populateTransactionTable();

    }

    /**
     * Method that adds a transaction to the transactions database
     */
    public void addTransaction() {

        // database connection
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String expDescription = descriptionTextField.getText();
        String expAmount = amountTextField.getText();
        String expDate = dateTextField.getText();


        // create query that inserts values to database
            String insertFields = "INSERT INTO transactions(transaction_type, description, amount, date, category, iduser_account) VALUES('";
            String insertValues =  transactionType + "', '" + expDescription + "','" + expAmount + "','" + expDate + "','" + expCategory + "','" + Integer.toString(currentUserID) + "')";
            String insertToRegister = insertFields + insertValues;

            try{
                Statement statement = connectDB.createStatement();
                statement.executeUpdate(insertToRegister);
                // added to database
                if (transactionType.equals("income")) {
                    addExpenseLabel.setText("Income has been added!");
                    //updateTable();
                } else {
                    addExpenseLabel.setText("Expense has been added!");
                }
                // update total current balance and specific categories
                setCurrentBalance();
                setCategoryBalance("shopping", shoppingLabel);
                setCategoryBalance("etsy", etsyLabel);
                setCategoryBalance("living", livingLabel);
                setCategoryBalance("educational", educationLabel);
                setCategoryBalance("other", otherLabel);

            }catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
    }


    // Report button

    /**
     * Method opens new scene to view transaction reports
     * @param event ActionEvent when button is clicked
     */
    public void viewReportsButtonOnAction (ActionEvent event) {
        try{
            // make user registration window pop up
            Parent root = FXMLLoader.load(getClass().getResource("reports.fxml"));
            Stage registerStage = new Stage();
            registerStage.setTitle("Personal Expense Manager Reports");
            registerStage.setScene(new Scene(root, 1000, 700));
            registerStage.show();

        } catch(Exception ex){
            ex.printStackTrace();
            ex.getCause();
        }
    }

    // displays transactions in a table
    /**
     * Method populates the transaction table of all income and expenses
     */
    private void populateTransactionTable() {
        // clear table to refresh everytime something is added
        oblist.clear();
        try {
            // select query list
            String query="SELECT * FROM transactions WHERE iduser_account = " + currentUserID + " ORDER BY date DESC";
            // run query and put result in resultset

            resultSet = connection.createStatement().executeQuery(query);
            //loop through resultset and extract data and append to list
            while(resultSet.next()) {
                // create a transactions object and add data to it
                // append to list
                TransactionTable transaction = new TransactionTable();
                // get data from query and insert into table
                transaction.setType(resultSet.getString("transaction_type"));
                transaction.setDescription(resultSet.getString("description"));
                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setDate(resultSet.getString("date"));
                transaction.setCategory(resultSet.getString("category"));
                transaction.setId(resultSet.getInt("iduser_account"));

                oblist.add(transaction);
            }
            // set property to all transaction tableview columns
            allTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            allDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            allAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
            allDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            allCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

            // set data to tableview
            allTransactionsTableView.setItems(oblist);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }

    // calculates net balance (income - expenses) and displays it
    /**
     * Method calculates and updates the current balance and displays it on the dashboard
     */
    public void setCurrentBalance() {
        try {
            String query = " select (sum( case when transaction_type = 'income' then amount else 0 end )" +
                    " - sum(case when transaction_type = 'expense' then amount else 0 end) ) current_balance" +
                    " from transactions where iduser_account = " + currentUserID;

            resultSet = connection.createStatement().executeQuery(query);
            //loop through resultset and extract data and append to list
            while (resultSet.next()) {
                currentBalanceLabel.setText("$" + resultSet.getString("current_balance"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }


    /**
     * Method that finds net balance of a specific category and updates its display label
     * @param category String of category that net balance calculations will be executed.
     * @param balance  the Label of the net balance that will be updated and displayed
     */
    public void setCategoryBalance(String category, Label balance) {
        try {
            String query = " select (sum( case when transaction_type = 'income' and category = '" + category + "' then amount else 0 end )" +
                    " - sum(case when transaction_type = 'expense' and category = '" + category + "' then amount else 0 end) ) current_balance" +
                    " from transactions where iduser_account = " + currentUserID;

            resultSet = connection.createStatement().executeQuery(query);
            //loop through resultset and extract data and append to list
            while (resultSet.next()) {
                balance.setText("$" + resultSet.getString("current_balance"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }

    /**
     * Called to initialize after dashboard.fxml root element has been completely processed and displays
     * initial set up of scene
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initial setup of dashboard
        //sets the current balances and displays transactions on dashboard table
        dbHandler = new DatabaseConnection();
        populateTransactionTable();
        setCurrentBalance();
        setCategoryBalance("shopping", shoppingLabel);
        setCategoryBalance("etsy", etsyLabel);
        setCategoryBalance("living", livingLabel);
        setCategoryBalance("educational", educationLabel);
        setCategoryBalance("other", otherLabel);
    }



}
