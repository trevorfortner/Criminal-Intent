package MenuTesting;

import DialogueTesting.DialogueTestingCityLandscape;
import DialogueTesting.DialogueTestingStoryArea;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

class MenuTestingMenu {
    private double windowHeight = 480;
    private double windowWidth = 800;
    private static DialogueTestingStoryArea storyArea;  //static to ensure there's only one

    private void displayInGameMenu(Stage window){
        Stage popupMenu = new Stage();      //new window to popup with menu things
        popupMenu.setHeight(250);
        popupMenu.setWidth(260);
        popupMenu.setResizable(false);
        popupMenu.setTitle("Settings");
        popupMenu.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if((e.getCode() == KeyCode.M)){
                popupMenu.close();
            }
        });

        AnchorPane menuPane = new AnchorPane();
        ImageView menuBackground = new ImageView(new Image("file:Images/PopupMenuBackground.jpg"));
        //source: http://www.sawyoo.com/postpic/2011/08/italian-dinner-menu-background_426936.jpg

        Label popupMenuGreeting = new Label("Paused!");
        popupMenuGreeting.setFont(new Font(30));
        AnchorPane.setTopAnchor(popupMenuGreeting, 10.0);
        AnchorPane.setLeftAnchor(popupMenuGreeting, 75.0);

        Button changeSizeButton = new Button("Change window size");
        AnchorPane.setTopAnchor(changeSizeButton, 60.0);
        AnchorPane.setLeftAnchor(changeSizeButton, 35.0);
        changeSizeButton.setOnMouseClicked(e -> {
            changeWindowSize(window);
            popupMenu.close();
        });

        Label warningLabel = new Label("Warning: Changing the window size will take you back to the main menu!");
        warningLabel.setPrefWidth(255);
        warningLabel.setWrapText(true);
        warningLabel.setTextAlignment(TextAlignment.CENTER);
        AnchorPane.setTopAnchor(warningLabel, 105.0);
        AnchorPane.setLeftAnchor(warningLabel, 5.0);


        //space for some middle buttons to be put in


        Button closeMenuButton = new Button("Close menu");
        AnchorPane.setBottomAnchor(closeMenuButton, 10.0);
        AnchorPane.setLeftAnchor(closeMenuButton, 70.0);
        closeMenuButton.setOnMouseClicked(e -> popupMenu.close());

        menuPane.getChildren().addAll(menuBackground, popupMenuGreeting, changeSizeButton, warningLabel, closeMenuButton);

        menuPane.getStylesheets().add("file:PracticeCode/MenuTesting/InGameMenuFormatting.css");
        Scene menuScene = new Scene(menuPane);

        popupMenu.setScene(menuScene);
        popupMenu.showAndWait();
    }

    void displayMainMenu(Stage window){
        AnchorPane menuPane = new AnchorPane();
        ImageView menuBackground = new ImageView(new Image("file:Images/Criminal.jpg", windowWidth, windowHeight, false, true));
        //source: http://cdnimg.in/wp-content/uploads/2015/09/magic_book_wallpaper.jpg?cfaea8

        Label mainMenuGreeting = new Label("Placeholder for title");     //placeholder name
        mainMenuGreeting.setTextFill(Color.LIGHTGREEN);
        mainMenuGreeting.setFont(new Font("Playbill", windowWidth/8));
        AnchorPane.setTopAnchor(mainMenuGreeting, 10.0);
        AnchorPane.setLeftAnchor(mainMenuGreeting, windowWidth/4.5);

        Label credit = new Label("Created by Trevor Fortner");
        credit.setFont(new Font(15));
        credit.setTextFill(Color.LIGHTGREY);
        AnchorPane.setBottomAnchor(credit, 10.0);
        AnchorPane.setLeftAnchor(credit, 10.0);

        Button startGameButton = new Button("Start game");
        startGameButton.setFont(new Font("Onyx", windowHeight/16));
        AnchorPane.setTopAnchor(startGameButton, windowHeight/2.4);
        AnchorPane.setRightAnchor(startGameButton, 20.0);
        startGameButton.setOnMouseClicked(e -> setupFirstArea(window));

        Button windowSizeButton = new Button("Change window size");
        windowSizeButton.setFont(new Font("Onyx", windowHeight/16));
        AnchorPane.setTopAnchor(windowSizeButton, windowHeight/1.92);
        AnchorPane.setRightAnchor(windowSizeButton, 20.0);
        windowSizeButton.setOnMouseClicked(e -> changeWindowSize(window));

        menuPane.getChildren().addAll(menuBackground, mainMenuGreeting, credit, startGameButton, windowSizeButton);

        Scene menuScene = new Scene(menuPane, windowWidth, windowHeight);
        menuScene.getStylesheets().add("file:PracticeCode/MenuTesting/MainMenuFormatting.css");

        window.setScene(menuScene);
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);
        window.setResizable(false);
        window.setTitle("Game Launcher");
        window.show();
    }

    private void setupFirstArea(Stage window){
        if (storyArea == null){         //making sure only one instance of the StoryArea exists
            storyArea = new DialogueTestingCityLandscape(window, windowWidth, windowHeight);    //if not, create it
            window.addEventHandler(KeyEvent.KEY_PRESSED, e -> {         //adding the EventHandler here lets
                if ((e.getCode() == KeyCode.M)) {                       //me ensure there's only one thing
                    displayInGameMenu(window);                          //happening whenever M is pressed
                }                                                       //(otherwise, 2+ eventhandlers would
            });                                                         //react when M was pressed)
        }

        storyArea.setWindowWidth(windowWidth);  //update, in case the values have changed
        storyArea.setWindowHeight(windowHeight);
        storyArea.setup();          //setup (or re-setup) the storyarea with updated numbers
        storyArea.playthrough();
        //once it's in the StoryArea, it'll just pass it down the line of StoryAreas and their setup()s and playthrough()s
    }


    //This method might benefit from the use of a Visitor pattern to determine which StoryArea/Menu is calling it, and recreate that.
    private void changeWindowSize(Stage MainGameWindow){
        Stage windowSizeMenu = new Stage();
        windowSizeMenu.setTitle("Changing window size");

        ImageView menuBackground = new ImageView(new Image("file:Images/PopupMenuBackground.jpg", 400, 250, false, true));
        //source: http://www.sawyoo.com/postpic/2011/08/italian-dinner-menu-background_426936.jpg

        Label windowSizeGreeting = new Label("Which size would you prefer?");
        AnchorPane.setTopAnchor(windowSizeGreeting, 10.0);
        AnchorPane.setLeftAnchor(windowSizeGreeting, 10.0);

        ComboBox<String> sizeChoiceBox = new ComboBox<>();
        sizeChoiceBox.getItems().addAll("800x480", "1080x600", "1200x720"); //making any bigger bugged out on my laptop. booooo lame screen
        sizeChoiceBox.setPromptText("Please choose from this menu");
        AnchorPane.setLeftAnchor(sizeChoiceBox, 20.0);
        AnchorPane.setTopAnchor(sizeChoiceBox, 40.0);

        Button confirmButton = new Button("Confirm");
        AnchorPane.setRightAnchor(confirmButton, 100.0);
        AnchorPane.setBottomAnchor(confirmButton, 10.0);
        confirmButton.setOnMouseClicked(e -> {
            MainGameWindow.setResizable(true);
            if(sizeChoiceBox.getValue()!=null) {
                String choice = sizeChoiceBox.getValue();
                switch (choice) {
                    case "800x480":
                        windowWidth = 800;
                        windowHeight = 480;
                        break;
                    case "1080x600":
                        windowWidth = 1080;
                        windowHeight = 600;
                        break;
                    case "1200x720":
                        windowWidth = 1200;
                        windowHeight = 720;
                        break;
                }
                displayMainMenu(MainGameWindow);
                windowSizeMenu.close();
            }
            else{
                sizeChoiceBox.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 200, 200, false, CycleMethod.NO_CYCLE, new Stop(0, Color.DARKORANGE), new Stop(1, Color.ORANGERED)), new CornerRadii(10), Insets.EMPTY)));
            }
        });

        Button cancelButton = new Button("Cancel");
        AnchorPane.setRightAnchor(cancelButton, 10.0);
        AnchorPane.setBottomAnchor(cancelButton, 10.0);
        cancelButton.setOnMouseClicked(e -> windowSizeMenu.close());

        AnchorPane menuPane = new AnchorPane();
        menuPane.getChildren().addAll(menuBackground, windowSizeGreeting, sizeChoiceBox, confirmButton, cancelButton);

        Scene menuScene = new Scene(menuPane);
        menuScene.getStylesheets().add("file:PracticeCode/MenuTesting/WindowSizeFormatting.css");

        windowSizeMenu.setScene(menuScene);
        windowSizeMenu.showAndWait();
    }
}
