package PEM;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import loginGUI.DatabaseConnection;
import loginGUI.LoginController;
import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private TextField lineGraphYear;
    @FXML private Button lineGraphDashboardButton;
    @FXML private Button lineGraphExitButton;
    @FXML private LineChart<String, Number> transactionLineChart;
    @FXML private Label lineGraphLabel;

    @FXML private TextField expPieChartYear;
    @FXML private Button expPieChartExitButton;
    @FXML private PieChart expensePieChart;
    @FXML private Label expYearLabel;

    @FXML private PieChart incomePieChart;
    @FXML private TextField incPieChartYear;
    @FXML private Button incPieChartExitButton;
    @FXML private Label incYearLabel;


    private ObservableList<PieChart.Data> expPieChartData; // add elements to this list
    private ObservableList<PieChart.Data> incPieChartData;
    private Connection connection = DatabaseConnection.getConnection();
    private ResultSet resultSet;
    private String viewYear;
    private int currentUserID = LoginController.getAccountUserID();
    XYChart.Series expSeries;
    XYChart.Series incSeries;
    XYChart.Series netSeries;



    //Method to check if a String input can be converted into an integer value
    public boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }



    //Closes line graph report window and returns back to the main dashboard
    public void lineDashboardButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) lineGraphDashboardButton.getScene().getWindow();
        stage.close();
    }
    // Closes the entire program
    public void lineExitButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) lineGraphExitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    // Checks to see if user's year input is valid, then loads transaction line graph
    // for that particular year
    public void lineOKButtonOnAction(ActionEvent event) {
        int digits = String.valueOf(lineGraphYear.getText()).length();
        if (digits == 4 && isInteger(lineGraphYear.getText())) {
            loadLineGraphYear();
        } else {
            lineGraphLabel.setText("Year format not valid.");
        }
    }
    // Loads the transaction line graph for the previous month (past 30 days)
    public void lineMonthButtonOnAction(ActionEvent event) {
        expSeries.getData().clear();
        incSeries.getData().clear();
        netSeries.getData().clear();
        expSeries.getData().removeAll(Collections.singleton(transactionLineChart.getData().setAll()));
        expSeries = new XYChart.Series();
        incSeries = new XYChart.Series();
        netSeries = new XYChart.Series();
        try {
            String query = "select date, sum(case when transaction_type = 'expense' then amount else 0 end) as expense, sum(case when transaction_type = 'income' then amount else 0 end) as income, (sum( case when transaction_type = 'income' then amount else 0 end )\n" +
                    "- sum(case when transaction_type = 'expense' then amount else 0 end) ) as net_balance " +
                    "FROM transactions WHERE iduser_account = " + currentUserID + " and date BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() group by date order by date ASC";

            resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                expSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("expense"))));
                incSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("income"))));
                netSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("net_balance"))));

            }

            expSeries.setName("Expenses");
            incSeries.setName("Income");
            netSeries.setName("Net Balance");
            // Setting the data to Line chart
            transactionLineChart.getData().addAll(expSeries, incSeries, netSeries);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    // loads the transaction line graph for the previous week (past 7 days)
    public void lineWeekButtonOnAction(ActionEvent event) {
        expSeries.getData().clear();
        incSeries.getData().clear();
        netSeries.getData().clear();
        expSeries.getData().removeAll(Collections.singleton(transactionLineChart.getData().setAll()));
        expSeries = new XYChart.Series();
        incSeries = new XYChart.Series();
        netSeries = new XYChart.Series();
        try {
            String query = "select date, sum(case when transaction_type = 'expense' then amount else 0 end) as expense, sum(case when transaction_type = 'income' then amount else 0 end) as income, (sum( case when transaction_type = 'income' then amount else 0 end )\n" +
                    "- sum(case when transaction_type = 'expense' then amount else 0 end) ) as net_balance " +
                    "FROM transactions WHERE iduser_account = " + currentUserID + " and date BETWEEN CURDATE() - INTERVAL 7 DAY AND CURDATE() group by date order by date ASC";

            resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                expSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("expense"))));
                incSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("income"))));
                netSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("net_balance"))));

            }

            expSeries.setName("Expenses");
            incSeries.setName("Income");
            netSeries.setName("Net Balance");
            // Setting the data to Line chart
            transactionLineChart.getData().addAll(expSeries, incSeries, netSeries);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    // loads a line graph for all the transactions made by calling loadAllLineGraph() method
    public void lineAllButtonOnAction(ActionEvent event) {
        expSeries.getData().clear();
        incSeries.getData().clear();
        netSeries.getData().clear();
        expSeries.getData().removeAll(Collections.singleton(transactionLineChart.getData().setAll()));
        loadAllLineGraph();
    }
    // creates line graph for all transactions made
    public void loadAllLineGraph() {
         expSeries = new XYChart.Series();
         incSeries = new XYChart.Series();
         netSeries = new XYChart.Series();
        try {
            String query = "select date, sum(case when transaction_type = 'expense' then amount else 0 end) as expense, sum(case when transaction_type = 'income' then amount else 0 end) as income," +
                    " (sum( case when transaction_type = 'income' then amount else 0 end ) - sum(case when transaction_type = 'expense' then amount else 0 end) ) as net_balance" +
                    " FROM transactions WHERE iduser_account = " + currentUserID + " group by date order by date ASC";

            resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                expSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("expense"))));
                incSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("income"))));
                netSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("net_balance"))));

            }

            expSeries.setName("Expenses");
            incSeries.setName("Income");
            netSeries.setName("Net Balance");
            // Setting the data to Line chart
            transactionLineChart.getData().addAll(expSeries, incSeries, netSeries);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    // creates line graph for a given year
    public void loadLineGraphYear () {
        viewYear = lineGraphYear.getText();
        expSeries.getData().clear();
        incSeries.getData().clear();
        netSeries.getData().clear();
        expSeries.getData().removeAll(Collections.singleton(transactionLineChart.getData().setAll()));
        expSeries = new XYChart.Series();
        incSeries = new XYChart.Series();
        netSeries = new XYChart.Series();
        try {
            String query = "select date, sum(case when transaction_type = 'expense' then amount else 0 end) as expense, sum(case when transaction_type = 'income' then amount else 0 end) as income, (sum( case when transaction_type = 'income' then amount else 0 end )\n" +
                    "- sum(case when transaction_type = 'expense' then amount else 0 end) ) as net_balance " +
                    "FROM transactions WHERE iduser_account = " + currentUserID + " and year(date) = " + viewYear + " group by date order by date ASC";

            resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                expSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("expense"))));
                incSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("income"))));
                netSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("net_balance"))));

            }

            expSeries.setName("Expenses");
            incSeries.setName("Income");
            netSeries.setName("Net Balance");
            // Setting the data to Line chart
            transactionLineChart.getData().addAll(expSeries, incSeries, netSeries);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }



    // expense pie chart methods
    //Closes report scene and returns back to the main dashboard
    public void expDashboardOnAction(ActionEvent event) {
        Stage stage = (Stage) expPieChartExitButton.getScene().getWindow();
        stage.close();
    }
    //Exits entire program
    public void expExitOnAction(ActionEvent event) {
        Stage stage = (Stage) expPieChartExitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    // creates pie chart for expenses made the past week (past 7 days)
    public void expWeekOnAction(ActionEvent event) {
        expPieChartData.clear();
        expPieChartData = FXCollections.observableArrayList();
        try {
            String queryWeek = "  SELECT  category, sum(amount)" +
                    " FROM    transactions\n" +
                    " WHERE   date BETWEEN CURDATE() - INTERVAL 7 DAY AND CURDATE() and iduser_account  = " + currentUserID +
                    " and transaction_type = 'expense'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(queryWeek);

            while(resultSet.next()) {
                expPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));

            }
            expPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );
            expensePieChart.setData(expPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    // creates pie chart for expenses made the past month (past 30 days)
    public void expMonthOnAction(ActionEvent event) {
        expPieChartData.clear();
        expPieChartData = FXCollections.observableArrayList();
        try {
            String queryMonth = "  SELECT  category, sum(amount)" +
                    " FROM    transactions\n" +
                    " WHERE   date BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() and iduser_account  = " + currentUserID +
                    " and transaction_type = 'expense'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(queryMonth);

            while(resultSet.next()) {
                expPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));

            }
            expPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );

            expensePieChart.setData(expPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    // creates pie chart for all expenses made by calling loadAllExpensePieChart() method
    public void expAllOnAction(ActionEvent event) { loadAllExpensePieChart(); }
    // first checks if year input is valid, then displays that year's
    // expenses in a pie chart
    public void expOKOnAction (ActionEvent event) {
        int digits = String.valueOf(expPieChartYear.getText()).length();
        if (digits == 4 && isInteger(expPieChartYear.getText())) {
            loadExpensePieChartYear();
        } else {
            expYearLabel.setText("Year format not valid.");
        }

    }

    // loads expenses in pie chart of a given year
    public void loadExpensePieChartYear() {
        expPieChartData.clear();
        viewYear = expPieChartYear.getText();
        expPieChartData = FXCollections.observableArrayList();
        try {
            String query = "select category, sum(amount)" +
                    " from transactions" +
                    " where year(date) = " + viewYear + " and iduser_account  = " + currentUserID +
                    " and transaction_type = 'expense'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                expPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));



            }
            expPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );
            expensePieChart.setData(expPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    // displays all exenses in a pie chart
    public void loadAllExpensePieChart() {
        //expPieChartData.clear();
        expPieChartData = FXCollections.observableArrayList();
        try {
            String queryMonth = " SELECT  category, sum(amount)" +
                    " FROM transactions" + " WHERE   iduser_account  = " + currentUserID +
                    " and transaction_type = 'expense' group by category;";

            resultSet = connection.createStatement().executeQuery(queryMonth);

            while(resultSet.next()) {
                expPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));

            }
            expPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );

            expensePieChart.setData(expPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }


    // income pie chart
    public void incDashboardOnAction(ActionEvent event){
        Stage stage = (Stage) incPieChartExitButton.getScene().getWindow();
        stage.close();
    }
    public void incExitOnAction(ActionEvent event) {
        Stage stage = (Stage) incPieChartExitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    public void incWeekOnAction(ActionEvent event) {
        //incPieChartData.clear();
        incPieChartData = FXCollections.observableArrayList();
        try {
            String queryWeek = "  SELECT  category, sum(amount)" +
                    " FROM    transactions" +
                    " WHERE   date BETWEEN CURDATE() - INTERVAL 7 DAY AND CURDATE() and iduser_account  = " + currentUserID +
                    " and transaction_type = 'income'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(queryWeek);

            while(resultSet.next()) {
                incPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));

            }
            incPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );
            incomePieChart.setData(incPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    public void incMonthOnAction(ActionEvent event) {
        incPieChartData.clear();
        incPieChartData = FXCollections.observableArrayList();
        try {
            String queryWeek = "  SELECT  category, sum(amount)" +
                    " FROM    transactions" +
                    " WHERE   date BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() and iduser_account  = " + currentUserID +
                    " and transaction_type = 'income'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(queryWeek);

            while(resultSet.next()) {
                incPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));

            }
            incPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );
            incomePieChart.setData(incPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    public void incAllOnAction(ActionEvent event)  { loadAllIncomePieChart();}
    public void incOKOnAction(ActionEvent event) {
        int digits = String.valueOf(incPieChartYear.getText()).length();
        if (digits == 4 && isInteger(incPieChartYear.getText())) {
            loadIncomePieChartYear();
        } else {
            incYearLabel.setText("Year format not valid.");
        }

    }

    public void loadAllIncomePieChart() {
        if (incPieChartData != null) {
            incPieChartData.clear();
        }
        incPieChartData = FXCollections.observableArrayList();
        try {
            String queryWeek = "  SELECT  category, sum(amount)" +
                    " FROM transactions WHERE iduser_account  = " + currentUserID +
                    " and transaction_type = 'income'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(queryWeek);

            while(resultSet.next()) {
                incPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));

            }
            incPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );
            incomePieChart.setData(incPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }
    public void loadIncomePieChartYear() {
        viewYear = incPieChartYear.getText();
        //incPieChartData.clear();
        incPieChartData = FXCollections.observableArrayList();
        try {
            String query = "select category, sum(amount)" +
                    " from transactions" +
                    " where year(date) = " + viewYear + " and iduser_account  = " + currentUserID +
                    " and transaction_type = 'income'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                incPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));
            }
            incPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );
            incomePieChart.setData(incPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAllExpensePieChart();
        loadAllIncomePieChart();
        loadAllLineGraph();




    }
}
