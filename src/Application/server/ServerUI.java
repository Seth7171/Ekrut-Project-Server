package Application.server;

import Presentation.serverGUI.ServerUIController;
import javafx.application.Application;
import javafx.stage.Stage;


public class ServerUI extends Application {
    private ServerUIController serverUI;
    private ServerController serverController;
    private MysqlController mysqlController;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        serverUI = new ServerUIController();
        serverUI.start(primaryStage);
    }

    public void startDataBase(){
        // mysqlController = new MysqlController();
    }

    public void startServer(){
        // serverController = new ServerController();
        // serverController = ServerController.getServerInstance();
    }

    // TODO: fix disconnect exceptions
    // TODO: fix architecture problems with server instantiations and constructors
    // TODO: get database name
}
