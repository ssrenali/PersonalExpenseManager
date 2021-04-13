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


/**
 * This class handles the reports scene window and displays the line graph
 * and pie chart of all the transactions
 *
 * @author  Serena Li
 * @since   2021-04-13
 */

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
    // series for line chart coordinates
    XYChart.Series expSeries;
    XYChart.Series incSeries;
    XYChart.Series netSeries;


    /**
     * Checks if a String input can be converted to an integer value
     * @param input the String input that is being checked
     */
    public boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }



    /**
     * Closes line graph report window and returns back to the main dashboard
     * @param event Action Event of button when clicked
     */
    public void lineDashboardButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) lineGraphDashboardButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Closes the entire program
     * @param event Action Event of button when clicked
     */
    public void lineExitButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) lineGraphExitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }


    // Checks to see if user's year input is valid, then loads transaction line graph
    // for that particular year
    /**
     *  Checks to see if user's year input is valid, then loads transaction line graph
     * for that particular year
     * @param event Action Event of button when clicked
     */
    public void lineOKButtonOnAction(ActionEvent event) {
        int digits = String.valueOf(lineGraphYear.getText()).length();
        if (digits == 4 && isInteger(lineGraphYear.getText())) {
            loadLineGraphYear();
        } else {
            lineGraphLabel.setText("Year format not valid.");
        }
    }

    /**
     * Loads the transaction line graph for the previous month (past 30 days)
     * @param event Action Event of button when clicked
     */
    public void lineMonthButtonOnAction(ActionEvent event) {
        // clear previous line graph data
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

            // add data points from result set
            while(resultSet.next()) {
                expSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("expense"))));
                incSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("income"))));
                netSeries.getData().add(new XYChart.Data(resultSet.getString("date"),
                        Double.parseDouble(resultSet.getString("net_balance"))));

            }
            // create graph legend
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


    /**
     * Loads the transaction line graph for the previous month (past 7 days)
     * @param event Action Event of button when clicked
     */
    public void lineWeekButtonOnAction(ActionEvent event) {

        expSeries.getData().clear();
        incSeries.getData().clear();
        netSeries.getData().clear();
        expSeries.getData().removeAll(Collections.singleton(transactionLineChart.getData().setAll()));
        expSeries = new XYChart.Series();
        incSeries = new XYChart.Series();
        netSeries = new XYChart.Series();
        try {
            // same logic as lineMonthButtonOnAction but change date range to 7 days instead of 30
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

    /**
     * loads a line graph for all the transactions made by calling loadAllLineGraph() method
     * @param event Action Event of button when clicked
     */
    public void lineAllButtonOnAction(ActionEvent event) {
        expSeries.getData().clear();
        incSeries.getData().clear();
        netSeries.getData().clear();
        expSeries.getData().removeAll(Collections.singleton(transactionLineChart.getData().setAll()));
        loadAllLineGraph();
    }

    /**
     * Creates a line graph for all transactions
     */
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

    /**
     * Creates a line graph for a given year
     */
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
            // get result set for transaction in viewYear
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



    /**
     * Returns to main dashboard
     * @param event Action Event of button when clicked
     */
    public void expDashboardOnAction(ActionEvent event) {
        Stage stage = (Stage) expPieChartExitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Exits entire program
     * @param event Action Event of button when clicked
     */
    public void expExitOnAction(ActionEvent event) {
        Stage stage = (Stage) expPieChartExitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }


    /**
     * Loads the expense pie chart for the previous week (past 7 days)
     * @param event Action Event of button when clicked
     */
    public void expWeekOnAction(ActionEvent event) {
        expPieChartData.clear();
        expPieChartData = FXCollections.observableArrayList();
        try {
            // get result set for expenses in past 7 days
            String queryWeek = "  SELECT  category, sum(amount)" +
                    " FROM    transactions\n" +
                    " WHERE   date BETWEEN CURDATE() - INTERVAL 7 DAY AND CURDATE() and iduser_account  = " + currentUserID +
                    " and transaction_type = 'expense'" +
                    " group by category";

            resultSet = connection.createStatement().executeQuery(queryWeek);

            // add result set data to pie chart
            while(resultSet.next()) {
                expPieChartData.add(new PieChart.Data(resultSet.getString("category"),
                        Double.parseDouble(resultSet.getString("sum(amount)"))));

            }
            // add labels to pie chart cells
            expPieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " $", data.pieValueProperty(), ""
                            )
                    )
            );

            // add pie chart data list to the chart
            expensePieChart.setData(expPieChartData);

        } catch (SQLException ex) {
            ex.printStackTrace();
            ex.getCause();
        }
    }

    /**
     * Loads the expense pie chart for the previous month (past 30 days)
     * @param event Action Event of button when clicked
     */
    public void expMonthOnAction(ActionEvent event) {
        // same logic as expWeekOnAction but change query to get expenses in past 30 days
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

    /**
     * Loads the expense pie chart for the previous month (past 30 days)
     * @param event Action Event of button when clicked
     */
    public void expAllOnAction(ActionEvent event) { loadAllExpensePieChart(); }

    /**
     * Loads the expense pie chart for the given year if year input is valid
     * @param event Action Event of button when clicked
     */
    public void expOKOnAction (ActionEvent event) {
        int digits = String.valueOf(expPieChartYear.getText()).length();
        if (digits == 4 && isInteger(expPieChartYear.getText())) {
            loadExpensePieChartYear();
        } else {
            expYearLabel.setText("Year format not valid.");
        }

    }

    // loads expenses in pie chart of a given year
    /**
     * Method that loads the expense pie chart for a given year
     */
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
    /**
     * displays all expenses in pie chart
     */
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


    // income pie chart methods
    // returns to dashboard
    /**
     * Returns to main dashboard
     * @param event Action Event of button when clicked
     */
    public void incDashboardOnAction(ActionEvent event){
        Stage stage = (Stage) incPieChartExitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Exits the entire program
     * @param event Action Event of button when clicked
     */
    public void incExitOnAction(ActionEvent event) {
        Stage stage = (Stage) incPieChartExitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }


    /**
     * Loads the income pie chart for the previous week (past 7 days)
     * @param event Action Event of button when clicked
     */
    public void incWeekOnAction(ActionEvent event) {
        // same logic as expWeekOnAction but query gets transaction type as income instead of expense
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

    /**
     * Loads the income pie chart for the previous month (past 30 days)
     * @param event Action Event of button when clicked
     */
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

    /**
     * Loads the income pie chart for all dates
     * @param event Action Event of button when clicked
     */
    public void incAllOnAction(ActionEvent event)  { loadAllIncomePieChart();}


    /**
     * Checks if year input is valid, then displays pie chart of that year's income
     * @param event Action Event of button when clicked
     */
    public void incOKOnAction(ActionEvent event) {
        int digits = String.valueOf(incPieChartYear.getText()).length();
        if (digits == 4 && isInteger(incPieChartYear.getText())) {
            loadIncomePieChartYear();
        } else {
            incYearLabel.setText("Year format not valid.");
        }

    }


    /**
     * Loads pie chart for all income
     */
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


    /**
     * Loads pie chart for a given year
     */
    public void loadIncomePieChartYear() {
        // gets year from text field
        viewYear = incPieChartYear.getText();
        incPieChartData = FXCollections.observableArrayList();
        try {
            // query to get income from that specific year
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
            // creating legend
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

    /**
     * Called to initialize after reports.fxml root element has been completely processed and displays
     * initial set up of scene
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // load all initial graphs
        loadAllExpensePieChart();
        loadAllIncomePieChart();
        loadAllLineGraph();
        


    }
}
