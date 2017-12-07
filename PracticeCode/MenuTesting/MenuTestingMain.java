package MenuTesting;

//This class is for implementing menus and improving dialogue boxes

import javafx.application.Application;
import javafx.stage.Stage;

public class MenuTestingMain extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(480);
        primaryStage.setWidth(800);
        primaryStage.setResizable(false);

        MenuTestingMenu menu = new MenuTestingMenu();
        menu.displayMainMenu(primaryStage);
    }
}
