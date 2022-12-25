package Application.server;

import OCSF.ConnectionToClient;
import common.Reports.InventoryReport;
import common.Reports.OrderReport;
import common.connectivity.Message;
import common.connectivity.MessageFromServer;
import common.connectivity.User;
import common.orders.Order;
import common.orders.Product;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Lior Jigalo
 * This class handles messages from the client.
 */
public class MessageHandler {
    private static MysqlController mysqlcontroller = MysqlController.getSQLInstance();

    /**
     * @param clientMessage message from the client.
     * @param client        client to respond to.
     * This method handles the message from the client.
     */
    public static void handleMessage(Object clientMessage, ConnectionToClient client){
        // if got null return unknown task
        if (clientMessage == null){
            sendMessageToClient(client, new Message(null, MessageFromServer.UNKNOWN_TASK));
            return;
        }
        Message message = (Message) clientMessage;
        System.out.println(message);
        ArrayList<Product> productList = null;
        ArrayList<String> machines = null;
        switch(message.getTask().name()){ // TODO: add disconnect message to set client connection status to disconnected
            case "REQUEST_LOGIN":
                ArrayList<String> userLogInCredentials = (ArrayList<String>) message.getData();
                if (mysqlcontroller.isLoggedIn(userLogInCredentials)){
                    sendMessageToClient(client, new Message(null, MessageFromServer.LOGIN_FAILED_ALREADY_LOGGED_IN));
                    break;
                }
                User userData = mysqlcontroller.logUserIn(userLogInCredentials);
                if (userData == null){
                    sendMessageToClient(client, new Message(null, MessageFromServer.LOG_IN_ERROR_USER_DOES_NOT_EXIST));
                    break;
                }
                sendMessageToClient(client ,new Message(userData, MessageFromServer.LOGIN_SUCCESSFUL));
                break;

            case "REQUEST_LOGOUT":
                if (mysqlcontroller.logUserOut((ArrayList<String>)message.getData())){
                    sendMessageToClient(client, new Message(null, MessageFromServer.LOGOUT_SUCCESSFUL));
                    break;
                }
                sendMessageToClient(client, new Message(null, MessageFromServer.LOGOUT_FAILED_NOT_LOGGED_IN));
                break;

            case "REQUEST_MACHINE_INVENTORY_REPORT":
            case "REQUEST_MACHINE_PRODUCTS":
                productList = mysqlcontroller.getMachineProducts((String)message.getData(), false);
                if(productList != null){
                    sendMessageToClient(client, new Message(productList, MessageFromServer.IMPORT_MACHINE_PRODUCTS_SUCCESSFUL));
                    break;
                }
                sendMessageToClient(client, new Message("error importing machines products", MessageFromServer.ERROR_IMPORTING_MACHINE_PRODUCTS));
                break;

            case "REQUEST_ALL_MACHINE_PRODUCTS":
                productList = mysqlcontroller.getMachineProducts("we need all machines products, hence true -->", true);
                if(productList != null){
                    sendMessageToClient(client, new Message(productList, MessageFromServer.IMPORT_MACHINE_PRODUCTS_SUCCESSFUL));
                    break;
                }
                sendMessageToClient(client, new Message("error importing all machines products", MessageFromServer.ERROR_IMPORTING_ALL_MACHINE_PRODUCTS));
                break;

            case "REQUEST_ADD_USER":
                String result = mysqlcontroller.dataExists((User) message.getData());
                if(!result.equals("")){
                    sendMessageToClient(client, new Message(result, MessageFromServer.ERROR_ADDING_USER_EXISTS));
                    break;
                }
                if(mysqlcontroller.addUser((User) message.getData())){
                    sendMessageToClient(client, new Message(null, MessageFromServer.USER_ADDED_SUCCESSFULLY));
                    break;
                }
                sendMessageToClient(client, new Message(null, MessageFromServer.ERROR_ADDING_USER));
                break;

            case "REQUEST_DELETE_USER":
                if(mysqlcontroller.deleteUser((String)message.getData())){
                    sendMessageToClient(client, new Message(null, MessageFromServer.DELETE_USER_SUCCESSFUL));
                    break;
                }
                sendMessageToClient(client, new Message(null, MessageFromServer.ERROR_DELETING_USER));
                break;

            case "REQUEST_MACHINE_IDS":
                machines = mysqlcontroller.getMachineIds((String)message.getData());
                if (machines == null){
                    sendMessageToClient(client, new Message(null, MessageFromServer.ERROR_IMPORTING_MACHINE_IDS));
                    break;
                }
                sendMessageToClient(client, new Message(machines, MessageFromServer.IMPORT_MACHINE_ID_SUCCESSFUL));
                break;

            case "REQUEST_WAREHOUSE_PRODUCTS":
                productList = mysqlcontroller.getWarehouseProducts();
                if(productList != null){
                    sendMessageToClient(client, new Message(productList, MessageFromServer.IMPORT_WAREHOUSE_PRODUCTS_SUCCESSFUL));
                    break;
                }
                sendMessageToClient(client, new Message(productList, MessageFromServer.ERROR_IMPORTING_WAREHOUSE_PRODUCTS));
                break;

            case "REQUEST_ALL_MACHINE_LOCATIONS":
                ArrayList<String> locations = mysqlcontroller.getAllMachineLocations();
                if(locations != null){
                    sendMessageToClient(client, new Message(locations, MessageFromServer.IMPORT_MACHINE_LOCATIONS_SUCCESSFUL));
                    break;
                }
                sendMessageToClient(client, new Message("Error importing machine locations", MessageFromServer.ERROR_IMPORTING_MACHINE_LOCATIONS));
                break;

            case "REQUEST_MACHINE_MONTHLY_INVENTORY_REPORT":
                ArrayList<String> monthYearMachine = (ArrayList<String>) message.getData();
                InventoryReport report =  mysqlcontroller.getMonthlyInventoryReport(monthYearMachine);
                if(report != null){
                    sendMessageToClient(client, new Message(report, MessageFromServer.IMPORT_INVENTORY_REPORT_SUCCESSFUL));
                    break;
                }
                sendMessageToClient(client, new Message("Error importing inventory report", MessageFromServer.ERROR_IMPORTING_INVENTORY_REPORT));
                break;

            case "REQUEST_ADD_NEW_ORDER":
                if (mysqlcontroller.AddNewOrder((Order) message.getData()))
                    sendMessageToClient(client, new Message("Error adding your order", MessageFromServer.ERROR_ADDING_NEW_ORDER));
                sendMessageToClient(client, new Message("Order added successfully", MessageFromServer.ADD_NEW_ORDER_SUCCESSFUL));
                break;

            case "REQUEST_ORDER_BY_ORDER_ID_AND_CUSTOMER_ID":
                Order order = mysqlcontroller.getOrderByOrderIdAndCustomerID((ArrayList<String>) message.getData());
                if (order == null)
                    sendMessageToClient(client, new Message("Error importing your order", MessageFromServer.ERROR_IMPORTING_ORDER));
                sendMessageToClient(client, new Message(order, MessageFromServer.IMPORT_ORDER_BY_ORDER_ID_AND_CUSTOMER_ID_SUCCESSFUL));
                break;

            case "REQUEST_GENERATE_MONTHLY_INVENTORY_REPORT":
                if (mysqlcontroller.generateMonthlyInventoryReport((ArrayList<String>) message.getData()))
                    sendMessageToClient(client, new Message("report generated successfully", MessageFromServer.SUCCESSFULLY_GENERATED_MONTHLY_INVENTORY_REPORT));
                sendMessageToClient(client, new Message("report generated successfully", MessageFromServer.ERROR_GENERATING_MONTHLY_INVENTORY_REPORT));
                break;

            case "REQUEST_ALL_MACHINES_ORDERS_MONTHLY_REPORT":
                OrderReport orderReport = mysqlcontroller.getOrderReportFromAllMachines((ArrayList<String>) message.getData());
                if (orderReport == null)
                    sendMessageToClient(client, new Message("Error to get order report", MessageFromServer.ERROR_IMPORTING_ALL_MACHINES_MONTHLY_REPORT));
                sendMessageToClient(client, new Message(orderReport, MessageFromServer.IMPORT_ALL_MACHINES_MONTHLY_REPORT_SUCCESSFUL));
                break;


            default:
                sendMessageToClient(client, new Message(null, MessageFromServer.UNKNOWN_TASK));
        }
    }

    /**
     * @param client    the client to send the message to.
     * @param message   the message to send to the client.
     * This method sends a message to the client passed as a parameter.
     */
    private static void sendMessageToClient(ConnectionToClient client, Message message){
        try {
            client.sendToClient(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
