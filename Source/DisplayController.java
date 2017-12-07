import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

//this class handles anything having to do with visually updating the main window for the game
public class DisplayController{
    private static Stage window;
    private ImageView backgroundImage;
    private double windowHeight = 480;
    private double windowWidth = 800;
    private static GameState gameState;  //static to ensure there's only one to deal with at a time
    private static GameController gameController;
    private Boolean warnedAboutSubDenials = false;
    private DisplayStatus displayStatus;
    private AnchorPane layout;

    void setWindow(Stage stage){
        window = stage;
    }
    static Stage getWindow(){
        return window;
    }
    void setGameController(GameController gameController){
        DisplayController.gameController = gameController;
    }
    void setGameState(GameState gameState){
        DisplayController.gameState = gameState;
    }
    AnchorPane getLayout(){
        return layout;
    }
    //last two only called in DisplayControllerTest

    //formats and displays the in-game menu on the window passed into it, pauses game if it's opened during a day
    private void displayInGameMenu(){
        if(displayStatus == DisplayStatus.DAY) {
            //Equivalent to "if it's the middle of the day", checks if the animation is complete or not
            gameController.pauseCycles();   //if so, pause them until we close the menu (at end of function)
        }

        Stage popupMenu = new Stage();      //new window to popup with menu things
        popupMenu.setHeight(285);
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
        AnchorPane.setTopAnchor(popupMenuGreeting, 5.0);
        AnchorPane.setLeftAnchor(popupMenuGreeting, 75.0);

        int currentScore = gameState.getPlayerScore();
        Label scoreDisplay = new Label("Current score: " + currentScore);
        scoreDisplay.setFont(new Font(20));
        AnchorPane.setTopAnchor(scoreDisplay, 40.0);
        if(Math.abs(currentScore) < 10)
            AnchorPane.setLeftAnchor(scoreDisplay, 55.0);
        else if(Math.abs(currentScore) >= 10 && Math.abs(currentScore) < 100)
            AnchorPane.setLeftAnchor(scoreDisplay, 45.0);
        else if(Math.abs(currentScore) >= 100 && Math.abs(currentScore) < 1000)
            AnchorPane.setLeftAnchor(scoreDisplay, 40.0);
        else if(Math.abs(currentScore) >= 1000 && Math.abs(currentScore) < 10000)
            AnchorPane.setLeftAnchor(scoreDisplay, 35.0);
        else
            AnchorPane.setLeftAnchor(scoreDisplay, 30.0);
        //for every digit, move it over to the left by 5 pixels, centers the label

        Button changeSizeButton = new Button("Change window size");
        AnchorPane.setTopAnchor(changeSizeButton, 60.0);
        AnchorPane.setLeftAnchor(changeSizeButton, 35.0);
        changeSizeButton.setOnMouseClicked(e -> {
            changeWindowSize();
            popupMenu.close();
        });

        Label warningLabel = new Label("Warning: Changing the window size will get rid of any currently visible mission markers!");
        warningLabel.setPrefWidth(255);
        warningLabel.setWrapText(true);
        warningLabel.setTextAlignment(TextAlignment.CENTER);
        AnchorPane.setTopAnchor(warningLabel, 105.0);
        AnchorPane.setLeftAnchor(warningLabel, 0.0);

        Button endDayButton = new Button("End day");
        AnchorPane.setBottomAnchor(endDayButton, 70.0);
        AnchorPane.setLeftAnchor(endDayButton, 85.0);
        endDayButton.setOnMouseClicked(e -> {
            if(displayStatus == DisplayStatus.DAY) {   //if it's during a day
                gameState.scaleUpMissionsAndDay();
                displayDayTransition();
                gameController.stopCycles();
                popupMenu.close();
            }
            else {
                StackPane whyPane = new StackPane();
                Label whyLabel = new Label("There isn't a day running right now.");
                whyLabel.setFont(new Font(25));
                whyLabel.setWrapText(true);
                whyLabel.setTextAlignment(TextAlignment.CENTER);
                Button okayButton = new Button("Okay");
                okayButton.setOnMouseClicked(e2 -> popupMenu.close());

                StackPane.setAlignment(whyLabel, Pos.TOP_CENTER);
                StackPane.setAlignment(okayButton, Pos.BOTTOM_CENTER);
                whyPane.getChildren().addAll(menuBackground, whyLabel, okayButton);
                whyPane.getStylesheets().add("file:Source/InGameMenuFormatting.css");
                popupMenu.setHeight(150.0);
                popupMenu.setScene(new Scene(whyPane));
            }
        });

        Button closeMenuButton = new Button("Close menu");
        AnchorPane.setBottomAnchor(closeMenuButton, 35.0);
        AnchorPane.setLeftAnchor(closeMenuButton, 70.0);
        closeMenuButton.setOnMouseClicked(e -> popupMenu.close());

        Button quitGameButton = new Button("Quit game");
        AnchorPane.setBottomAnchor(quitGameButton, 0.0);
        AnchorPane.setLeftAnchor(quitGameButton, 75.0);
        quitGameButton.setOnMouseClicked(e -> { popupMenu.close(); window.close(); });

        menuPane.getChildren().addAll(menuBackground, popupMenuGreeting, scoreDisplay, changeSizeButton, warningLabel, endDayButton, closeMenuButton, quitGameButton);

        menuPane.getStylesheets().add("file:Source/InGameMenuFormatting.css");
        Scene menuScene = new Scene(menuPane);

        popupMenu.initModality(Modality.APPLICATION_MODAL); //make this window priority while it's open
        popupMenu.setScene(menuScene);
        popupMenu.showAndWait();

        if(displayStatus == DisplayStatus.DAY) {   //if they were paused at the beginning of this function
            gameController.resumeCycles();        //resume them
        }
    }

    //formats and displays the main menu on the window passed into it
    void displayMainMenu(){
        AnchorPane menuPane = new AnchorPane();
        layout = menuPane;
        ImageView menuBackground = new ImageView(new Image("file:Images/Criminal.jpg", windowWidth, windowHeight, false, true));
        //source: http://cdnimg.in/wp-content/uploads/2015/09/magic_book_wallpaper.jpg?cfaea8
        backgroundImage = menuBackground;

        Label mainMenuGreeting = new Label("Criminal Intent");     //placeholder name
        mainMenuGreeting.setTextFill(Color.ORANGERED);
        mainMenuGreeting.setFont(new Font("Playbill", windowWidth/8));
        AnchorPane.setTopAnchor(mainMenuGreeting, 10.0);
        AnchorPane.setLeftAnchor(mainMenuGreeting, windowWidth/4);

        Label credit = new Label("Created by Trevor Fortner");
        credit.setFont(new Font(15));
        credit.setTextFill(Color.LIGHTGREY);
        AnchorPane.setBottomAnchor(credit, 10.0);
        AnchorPane.setLeftAnchor(credit, 10.0);

        Button startGameButton = new Button("Start game");
        startGameButton.setFont(new Font("Onyx", windowHeight/16));
        AnchorPane.setTopAnchor(startGameButton, windowHeight/2.4);
        AnchorPane.setRightAnchor(startGameButton, 20.0);
        startGameButton.setOnMouseClicked(e -> gameController.newGame());

        Button windowSizeButton = new Button("Change window size");
        windowSizeButton.setFont(new Font("Onyx", windowHeight/16));
        AnchorPane.setTopAnchor(windowSizeButton, windowHeight/1.92);
        AnchorPane.setRightAnchor(windowSizeButton, 20.0);
        windowSizeButton.setOnMouseClicked(e -> changeWindowSize());

        menuPane.getChildren().addAll(menuBackground, mainMenuGreeting, credit, startGameButton, windowSizeButton);

        Scene menuScene = new Scene(menuPane, windowWidth, windowHeight);
        menuScene.getStylesheets().add("file:Source/MainMenuFormatting.css");

        window.setScene(menuScene);
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);
        window.setResizable(false);
        window.setTitle("Game Launcher");
        window.show();
    }

    //changes the size of the primary window, called when "Change window size" pressed in main or in-game menu
    private void changeWindowSize(){
        Stage windowSizeMenu = new Stage();
        windowSizeMenu.setTitle("Changing window size");

        ImageView menuBackground = new ImageView(new Image("file:Images/PopupMenuBackground.jpg", 400, 250, false, true));
        //source: http://www.sawyoo.com/postpic/2011/08/italian-dinner-menu-background_426936.jpg

        Label windowSizeGreeting = new Label("Which size would you prefer?");
        AnchorPane.setTopAnchor(windowSizeGreeting, 10.0);
        AnchorPane.setLeftAnchor(windowSizeGreeting, 10.0);

        ComboBox<String> sizeChoiceBox = new ComboBox<>();
        sizeChoiceBox.getItems().addAll("800x480", "1080x600", "1200x720"); //making any bigger bugged out on my laptop
        sizeChoiceBox.setPromptText("Please choose from this menu");
        AnchorPane.setLeftAnchor(sizeChoiceBox, 20.0);
        AnchorPane.setTopAnchor(sizeChoiceBox, 43.0);

        Button confirmButton = new Button("Confirm");
        AnchorPane.setRightAnchor(confirmButton, 100.0);
        AnchorPane.setBottomAnchor(confirmButton, 10.0);
        confirmButton.setOnMouseClicked(e -> {
            window.setResizable(true);
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
                if(displayStatus == DisplayStatus.DAY) {
                    resetDayVisuals(layout);
                }
                else if(displayStatus == DisplayStatus.TRANSITION){
                    displayDayTransition(); //don't do GameState.scaleUpMissionsAndDay() before
                }
                else if(displayStatus == DisplayStatus.SUBSELECT){
                    displaySubordinateSelectionScreen();
                }
                else {  //the only status not covered by DisplayStatus is the main menu, this menu isn't available during the tutorial
                    displayMainMenu();
                }
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
        menuScene.getStylesheets().add("file:Source/WindowSizeFormatting.css");

        windowSizeMenu.setScene(menuScene);
        windowSizeMenu.showAndWait();
    }

    //asks the user if they want a tutorial, if so it calls giveTutorial(), if not it calls setupFirstDay()
    void askAboutTutorial(GameState gameState){
        DisplayController.gameState = gameState;

        AnchorPane layout = new AnchorPane();

        backgroundImage = new ImageView(new Image("file:Images/Mansion.jpg", windowWidth, windowHeight, false, true));
        layout.getChildren().add(backgroundImage);

        Rectangle rect = new Rectangle(windowWidth/1.5, windowHeight/2);
        rect.setId("rectangle");    //calls the css for linearGradient fill color and arc width/height
        AnchorPane.setLeftAnchor(rect, windowWidth/6);
        AnchorPane.setTopAnchor(rect, windowHeight/5);

        Label tutorialQuestion = new Label("Hi player! Would you like an introduction to this game?");
        tutorialQuestion.setId("message-label");    //calls css to use the "messagelabel" defaults on it
        tutorialQuestion.setFont(new Font(windowWidth/12));     //can't css these because they scale based on window width
        tutorialQuestion.setMaxWidth(windowWidth/1.5);          //css does do font family, text alignment, fill, and wrapping
        AnchorPane.setLeftAnchor(tutorialQuestion, windowWidth/5.75);
        AnchorPane.setTopAnchor(tutorialQuestion, windowHeight/5);

        layout.getChildren().addAll(rect, tutorialQuestion);

        Label yesLabel = new Label("Yes please");
        yesLabel.setFont(new Font("Onyx", windowHeight/10));    //specify different font from css
        AnchorPane.setBottomAnchor(yesLabel, windowHeight/3.8);
        AnchorPane.setLeftAnchor(yesLabel, windowWidth/4);
        yesLabel.setOnMouseClicked(e -> giveTutorial());
        layout.getChildren().add(yesLabel);

        Label noLabel = new Label("No thanks");
        noLabel.setFont(new Font("Onyx", windowHeight/10));
        AnchorPane.setBottomAnchor(noLabel, windowHeight/3.8);
        AnchorPane.setRightAnchor(noLabel, windowWidth/4);
        noLabel.setOnMouseClicked(e -> setupFirstDay());
        layout.getChildren().add(noLabel);

        layout.getStylesheets().add("file:Source/TutorialFormatting.css");

        window.setScene(new Scene(layout));
    }

    //formats and displays the tutorial, including the chain of messages for the user to read
    private void giveTutorial(){
        AnchorPane layout = new AnchorPane();

        layout.getChildren().add(backgroundImage);

        Rectangle rect = new Rectangle(windowWidth/1.5, windowHeight/2);
        rect.setId("rectangle");
        AnchorPane.setLeftAnchor(rect, windowWidth/6);
        AnchorPane.setTopAnchor(rect, windowHeight/5);
        layout.getChildren().add(rect);

        Label tutorialText = new Label("In this game, you're a famous mob boss, aiming to earn as much money as possible.");
        tutorialText.setFont(new Font("Playbill", windowWidth/14));
        tutorialText.setId("message-label");
        tutorialText.setMaxWidth(windowWidth/1.5);
        tutorialText.setWrapText(true);
        tutorialText.setTextAlignment(TextAlignment.CENTER);
        AnchorPane.setLeftAnchor(tutorialText, windowWidth/5.9);
        AnchorPane.setTopAnchor(tutorialText, windowHeight/5);
        layout.getChildren().addAll(tutorialText);

        Label okayLabel = new Label("Okay");
        okayLabel.setFont(new Font("Onyx", windowHeight/10));
        AnchorPane.setBottomAnchor(okayLabel, windowHeight/3.8);
        AnchorPane.setRightAnchor(okayLabel, windowWidth/2.2);
        okayLabel.setOnMouseClicked(e -> {
            tutorialText.setText("You will go about this using a group of \"subordinates\", which can normally be found at the bottom-left.");
            updateSubordinateImages(layout);       //show what subordinates will look like, in bottom-left as usual
            okayLabel.setOnMouseClicked(e2 -> {
                tutorialText.setText("Each subordinate will have a name and power level, which indicates how easily they'll complete missions.");
                okayLabel.setOnMouseClicked(e3 -> {
                    tutorialText.setText("Each subordinate will also have their stamina displayed above their head. This will affect their strength.");
                    okayLabel.setOnMouseClicked(e4 -> {
                        layout.getChildren().clear();       //get rid of all the stuff from showing subordinates
                        layout.getChildren().addAll(backgroundImage, rect, tutorialText, okayLabel);    //return to state before
                        tutorialText.setText("Their levels will increase upon completing missions. Missions will pop up periodically on the map.");
                        okayLabel.setOnMouseClicked(e5 -> {
                            layout.getChildren().clear();       //get rid of all the stuff from showing subordinates
                            layout.getChildren().addAll(backgroundImage, rect, tutorialText, okayLabel);    //return to state before

                            tutorialText.setText("Click on a mission to assign a subordinates to deal with it. Here's what a mission will look like.");

                            ImageView missionMarker = gameState.getMissionList().get(0).getMissionMarker(windowHeight / 3);
                            //smaller mission marker to show the user what it'll look like
                            AnchorPane.setLeftAnchor(missionMarker, windowWidth / 4.5);
                            AnchorPane.setBottomAnchor(missionMarker, windowHeight / 4.35);
                            layout.getChildren().add(missionMarker);

                            Label missionTime = new Label("0:07");      //mock mission countdown timer for the marker
                            missionTime.setFont(new Font(windowHeight / 30));
                            AnchorPane.setLeftAnchor(missionTime, windowWidth / 3.6);     //so it'll fit inside the mission image
                            AnchorPane.setBottomAnchor(missionTime, windowHeight / 3.25); //so it'll fit inside the mission image
                            layout.getChildren().add(missionTime);

                            okayLabel.setOnMouseClicked(e6 -> {
                                tutorialText.setText("The timer will count down for you to deal with these missions, so you'll have to manage your time wisely.");
                                okayLabel.setOnMouseClicked(e7 -> {
                                    layout.getChildren().removeAll(missionMarker, missionTime);
                                    tutorialText.setText("Assigning too few people to a mission could lead to a subordinate dying or getting caught by police.");
                                    okayLabel.setOnMouseClicked(e8 -> {
                                        layout.getChildren().removeAll(missionMarker, missionTime);
                                        tutorialText.setText("Succesfully completing a mission will increase your score. Failing one will decrease your score.");
                                        okayLabel.setOnMouseClicked(e9 -> {
                                            tutorialText.setText("After this tutorial, you'll be able to press M to open the in-game menu and ESC to close most pop-ups.");
                                            okayLabel.setOnMouseClicked(e10 -> {
                                                tutorialText.setText("So that's the basics, would you like to try a practice day, reread the tutorial, or hop right in?");

                                                Label practiceDayLabel = new Label("Practice Day");
                                                practiceDayLabel.setFont(new Font("Onyx", windowHeight / 10));
                                                AnchorPane.setBottomAnchor(practiceDayLabel, windowHeight / 3.8);
                                                AnchorPane.setLeftAnchor(practiceDayLabel, windowWidth / 2.25);
                                                practiceDayLabel.setOnMouseClicked(e11 -> {
                                                    setupFirstDay();
                                                    gameState.scaleDownMissionsAndDay();
                                                });
                                                //treat it like a normal day, but then decrease the day counter and mission levels back to starter levels
                                                layout.getChildren().add(practiceDayLabel);

                                                Label rereadLabel = new Label("Reread Tutorial");
                                                rereadLabel.setFont(new Font("Onyx", windowHeight / 10));
                                                AnchorPane.setBottomAnchor(rereadLabel, windowHeight / 3.8);
                                                AnchorPane.setLeftAnchor(rereadLabel, windowWidth / 5.8);
                                                rereadLabel.setOnMouseClicked(e11 -> giveTutorial());
                                                layout.getChildren().add(rereadLabel);

                                                okayLabel.setText("Start Game");
                                                AnchorPane.setRightAnchor(okayLabel, windowWidth / 5.8);
                                                okayLabel.setOnMouseClicked(e11 -> setupFirstDay());
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        layout.getChildren().add(okayLabel);

        layout.getStylesheets().add("file:Source/TutorialFormatting.css");

        window.setScene(new Scene(layout));
    }

    //prepares DisplayController for handling days from here on out, adds in-game menu EventHandler for when user presses M key
    private void setupFirstDay(){
        //adding an eventHandler for an in-game menu to pop up when the M key is pressed
        //adding it here lets me ensure there's only one added, otherwise pressing M might open two or three menus
        window.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if ((e.getCode() == KeyCode.M)) {
                displayInGameMenu();
            }
        });

        backgroundImage = new ImageView(new Image("file:Images/PixelCity.png", windowWidth, windowHeight, false, true));
        gameController.nextDay();
    }

    //turns the primary stage into just the background image, subordinate images, and the mission visual (i.e. a vault for a bank robbery)
    void setupWithMissionVisual(Mission mission, AnchorPane layout){
        backgroundImage = new ImageView(new Image("file:Images/PixelCity.png", windowWidth, windowHeight, false, true));

        layout.getChildren().clear();
        layout.getChildren().add(backgroundImage);
        layout.getChildren().add(mission.getMissionVisual(windowHeight));

        updateSubordinateImages(layout);

        AnchorPane.setLeftAnchor(mission.getMissionVisual(windowHeight), windowWidth/4);
        AnchorPane.setBottomAnchor(mission.getMissionVisual(windowHeight), 0.0);
    }

    //upon MissionResult received from mission, this removes the visual then either:
    // - replaces the marker (since it disappears upon setupWithMissionVisual)
    // - closes the game (if the user fails the mission and chooses Quit Game)
    // - removes the marker (if the user attempts to complete the mission or ignores it)
    void reactToMissionResults(AnchorPane layout, MissionResult missionResult, Mission mission, ImageView imageView, Label countdownLabel, Label zeroMinutesLabel){
        layout.getChildren().remove(mission.getMissionVisual(windowHeight));

        if(missionResult == MissionResult.KEEP_MARKER){         //cancel chosen
            layout.getChildren().addAll(imageView, countdownLabel, zeroMinutesLabel);   //re-add everything to do with the mission marker
            updateSubordinateImages(layout);       //re-add the subordinate images
        }
        if(missionResult == MissionResult.CLOSE_GAME){        //quit game chosen
            window.close();
        }
        if(missionResult == MissionResult.REMOVE_MARKER){        //remove mission marker, don't quit game
            updateSubordinateImages(layout);       //re-add just the subordinate images
        }
    }

    //formats and displays the screen that appears between days
    //also handles returning subordinates from resting days and having them ask for the day off
    void displayDayTransition(){
        displayStatus = DisplayStatus.TRANSITION;
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);   //in case the window size changes from in-game menu option
        window.setResizable(false);

        AnchorPane layout = new AnchorPane();
        this.layout = layout;
        backgroundImage = new ImageView(new Image("file:Images/DayTransition.jpg", windowWidth, windowHeight, false, true));

        Label dayNumLabel = new Label("End of day " + gameState.getCurrentDay());
            //it starts at zero and will be incremented later in GameController
        dayNumLabel.setFont(new Font("Playbill", windowWidth/8));
        AnchorPane.setTopAnchor(dayNumLabel, 5.0);
        AnchorPane.setLeftAnchor(dayNumLabel, windowWidth/3.6);
        if(gameState.getCurrentDay()+1 == 0){    //if it's a tutorial day (only way currentDay + 1 will be zero)
            dayNumLabel.setText("End of practice day. Good luck!");
            AnchorPane.setLeftAnchor(dayNumLabel, windowWidth/40);  //adjust to account for longer message
            gameState.decreaseScore(gameState.getPlayerScore());  //reset the score
        }

        Label currentScoreLabel = new Label("Current score: " + gameState.getPlayerScore());
        currentScoreLabel.setFont(new Font("Playbill", windowWidth/15));
        AnchorPane.setTopAnchor(currentScoreLabel, windowHeight/4);
        AnchorPane.setLeftAnchor(currentScoreLabel, windowWidth/3);

        Label nextDayLabel = new Label("Continue");
        nextDayLabel.setFont(new Font("Onyx", windowHeight/16));
        AnchorPane.setTopAnchor(nextDayLabel, windowHeight/2.4);
        AnchorPane.setRightAnchor(nextDayLabel, 20.0);
        nextDayLabel.setOnMouseClicked(e -> {
            gameState.getTeamList().forEach(Subordinate::reduceStamina);
            //decrease everyone in the team list's stamina before bringing back the people who were resting

            ArrayList<Subordinate> restingSubsCopy = new ArrayList<>(gameState.getRestingSubordinates());
            //duplicate restingSubordinates, because otherwise it'd be modifying the list we're iterating through
            for(Subordinate restingSub : restingSubsCopy){
                restingSub.returnStamina();
                gameState.bringBackSubFromRestDay(restingSub);  //bring back any subs who were on a rest day
            }

            ArrayList<Subordinate> teamListCopy = new ArrayList<>(gameState.getTeamList());
            //same reason for copying as before
            for(Subordinate sub : teamListCopy){
                Random random = new Random();
                int randomInt = random.nextInt(3)+1;
                //so they can't ask for the day off unless they have less than 3 stamina
                //Random.nextInt(bound) returns between 0 and bound-1, so doing +1 to get between 1 to 3
                int randomExcuseInt = random.nextInt(sub.getExcuses().length);
                if(sub.getStamina() < randomInt){
                    Stage dialogueWindow = new Stage();
                    DialogueBoxController dialogueBox = new DialogueBoxController("Hey boss, can I have the day off?\n"+sub.getExcuses()[randomExcuseInt], new String[0], sub.getName()+" asking for day off", dialogueWindow);
                    Boolean givenDayOff = dialogueBox.displaySubAskingForDayOff(sub);
                    if(givenDayOff){
                        sub.resetDenialTracker();
                        gameState.restSubordinate(sub);
                    }
                    else{
                        sub.incremementDenialTracker();
                        if(sub.getDenialTracker() == 3 && !warnedAboutSubDenials){    //just warn them once, when it's close to being possible
                            warnedAboutSubDenials = true;
                            dialogueBox.updateQuestionAndResponses("Warning: Denying a subordinate\na day off too many times\nin a row may make them quit!", new String[0]);
                            dialogueBox.displayNonResponseDialogue();
                        }
                        else if(sub.getDenialTracker() > 4){
                            int quitNumber = random.nextInt(10);    //generate a number from 0-9
                            if(sub.getDenialTracker() > quitNumber){    //if it's 10 days in a row denied a break, they'll definitely quit
                                dialogueBox.updateQuestionAndResponses("That's it, I'm out of here!\nFind a new lap dog!", new String[0]);
                                dialogueBox.displaySubLeaving(sub); //same function to show them visually who's leaving
                                gameState.getTeamList().remove(sub);
                            }
                        }
                    }
                }
            }

            if(!gameState.getPossibleSubordinates().isEmpty() && gameState.getCurrentDay() % 2 == 0 && gameState.getCurrentDay() != 0)
                displaySubordinateSelectionScreen();    //have the user select a new subordinate every other day, but not after tutorial
            else
                gameController.nextDay();
        });

        Label quitGameLabel = new Label("Quit Game");
        quitGameLabel.setFont(new Font("Onyx", windowHeight/16));
        AnchorPane.setTopAnchor(quitGameLabel, windowHeight/1.92);
        AnchorPane.setRightAnchor(quitGameLabel, 20.0);
        quitGameLabel.setOnMouseClicked(e -> window.close());

        layout.getChildren().addAll(backgroundImage, dayNumLabel, currentScoreLabel, nextDayLabel, quitGameLabel);

        layout.getStylesheets().add("file:Source/DayTransitionFormatting.css");

        Scene scene = new Scene(layout);
        window.setScene(scene);
    }

    //formats and displays the screen for selecting new subordinates to hire
    private void displaySubordinateSelectionScreen(){
        displayStatus = DisplayStatus.SUBSELECT;
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);   //in case the window size changes from in-game menu option
        window.setResizable(false);

        AnchorPane layout = new AnchorPane();
        this.layout = layout;

        backgroundImage = new ImageView(new Image("file:Images/Mansion.jpg", windowWidth, windowHeight, false, true));

        Rectangle backgroundRect = new Rectangle(windowWidth/1.5, windowHeight/1.5);
        backgroundRect.setId("rectangle");    //css does the linear gradient from darkred to red, curves corners a bit
        AnchorPane.setLeftAnchor(backgroundRect, windowWidth/6);
        AnchorPane.setTopAnchor(backgroundRect, windowHeight/7.5);

        Label chooseLabel = new Label("Hire a new subordinate!");
        chooseLabel.setFont(new Font(windowWidth/10));
        chooseLabel.setId("message-label");     //css centers and wraps text, sets font to Playbill, sets color to azure
        AnchorPane.setLeftAnchor(chooseLabel, windowWidth/4.7);
        AnchorPane.setTopAnchor(chooseLabel, windowHeight/8);

        Label moneyLabel = new Label("Currently available: $" + gameState.getPlayerScore());     //need second label to change font size
        moneyLabel.setFont(new Font(windowWidth/20));
        moneyLabel.setId("message-label");     //css centers and wraps text, sets font to Playbill, sets color to azure
        AnchorPane.setLeftAnchor(moneyLabel, windowWidth/2.8);
        AnchorPane.setTopAnchor(moneyLabel, windowHeight/3.8);

        layout.getChildren().addAll(backgroundImage, backgroundRect, chooseLabel, moneyLabel);

        for(int i = 0; i < gameState.getPossibleSubordinates().size() && i < 7; i++){
            Subordinate possSub = gameState.getPossibleSubordinates().get(i);
            AnchorPane.setLeftAnchor(possSub.getSubImageView(windowHeight*1.4), (windowWidth/5.8+(windowHeight/6.8+5)*i));
            AnchorPane.setTopAnchor(possSub.getSubImageView(windowHeight*1.4), windowHeight/2.775);
            layout.getChildren().add(possSub.getSubImageView(windowHeight*1.4));
            possSub.getSubImageView(windowHeight*1.4).setOnMouseClicked(e -> handleAddingNewSubAndDoNextDay(possSub));
            //made handleAddingNewSubAndDoNextDay() its own function because it's called 3 times and calls two other objects

            //only need one stamina rect because they'll always be full stamina when being hired
            Rectangle staminaRect = new Rectangle(windowHeight / 7.15 , windowHeight / 50);
            staminaRect.setStroke(Color.BLACK);
            staminaRect.setFill(Color.LIMEGREEN.brighter());
            AnchorPane.setLeftAnchor(staminaRect, (windowWidth/5.8+(windowHeight/6.8+5)*i));
            AnchorPane.setTopAnchor(staminaRect, windowHeight/2.875);
            staminaRect.setOnMouseClicked(e -> handleAddingNewSubAndDoNextDay(possSub));
            layout.getChildren().add(staminaRect);

            Rectangle labelBackgroundRect = new Rectangle(windowHeight/7.15, windowHeight/6.67);
            labelBackgroundRect.setId("sub-rectangle");       //css does linear gradient from skyblue to white
            AnchorPane.setLeftAnchor(labelBackgroundRect, (windowWidth/5.8+(windowHeight/6.8+5)*i));
            AnchorPane.setTopAnchor(labelBackgroundRect, windowHeight/2);
            labelBackgroundRect.setOnMouseClicked(e -> handleAddingNewSubAndDoNextDay(possSub));
            layout.getChildren().add(labelBackgroundRect);

            Label nameAndStrengthLabel = new Label(possSub.getName() + "\n" + possSub.getStrength() + "\n$" + possSub.getCost());
            nameAndStrengthLabel.setFont(new Font(windowHeight/30));
            nameAndStrengthLabel.setId("sub-label");    //css sets color to black
            AnchorPane.setLeftAnchor(nameAndStrengthLabel, (windowWidth/5.6+(windowHeight/6.8+5)*i));
            AnchorPane.setTopAnchor(nameAndStrengthLabel, windowHeight/2);
            nameAndStrengthLabel.setOnMouseClicked(e -> handleAddingNewSubAndDoNextDay(possSub));
            layout.getChildren().add(nameAndStrengthLabel);

            int depth = 70; //Setting the uniform variable for the glow width and height
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.BLUE);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);

            possSub.getSubImageView(windowHeight*1.4).setOnMouseEntered(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(borderGlow));
            staminaRect.setOnMouseEntered(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(borderGlow));
            labelBackgroundRect.setOnMouseEntered(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(borderGlow));
            nameAndStrengthLabel.setOnMouseEntered(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(borderGlow));

            possSub.getSubImageView(windowHeight*1.4).setOnMouseExited(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(null));
            staminaRect.setOnMouseExited(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(null));
            labelBackgroundRect.setOnMouseExited(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(null));
            nameAndStrengthLabel.setOnMouseExited(e -> possSub.getSubImageView(windowHeight*1.4).setEffect(null));
        }

        Label noThanksLabel = new Label("Continue");
        noThanksLabel.setFont(new Font("Onyx", windowHeight/10));
        AnchorPane.setBottomAnchor(noThanksLabel, windowHeight/6);
        AnchorPane.setLeftAnchor(noThanksLabel, windowWidth/4.5);
        noThanksLabel.setOnMouseClicked(e -> gameController.nextDay());
        layout.getChildren().add(noThanksLabel);

        Label quitGameLabel = new Label("Quit Game");
        quitGameLabel.setFont(new Font("Onyx", windowHeight/10));
        AnchorPane.setBottomAnchor(quitGameLabel, windowHeight/6);
        AnchorPane.setRightAnchor(quitGameLabel, windowWidth/4.5);
        quitGameLabel.setOnMouseClicked(e -> window.close());
        layout.getChildren().add(quitGameLabel);

        layout.getStylesheets().add("file:Source/NewSubFormatting.css");

        Scene scene = new Scene(layout);
        window.setScene(scene);
    }

    //upon choice of new subordinate, notifies user they don't have enough money or confirms they hired the sub, then starts new day
    //could be private, but isn't solely to be able to test it
    void handleAddingNewSubAndDoNextDay(Subordinate possSub){
        Stage dialogueWindow = new Stage();

        if(gameState.getPlayerScore() >= possSub.getCost()) {
            gameState.decreaseScore(possSub.getCost());      //reduce the score by the sub's cost
            gameState.addNewSubFromPossibleList(possSub);
            DialogueBoxController dialogueBox = new DialogueBoxController(possSub.getName()+" recruited! New score: "+gameState.getPlayerScore(), new String[0], "hire new sub", dialogueWindow);
            dialogueBox.displayNonResponseDialogue();
            gameController.nextDay();
            possSub.getSubImageView(windowHeight * 1.4).setEffect(null);  //would retain glow from mouseover otherwise
            AnchorPane.clearConstraints(possSub.getSubImageView(windowHeight));
        }
        else{
            DialogueBoxController dialogueBox = new DialogueBoxController("You don't have enough money to buy this subordinate!", new String[0], "hire new sub", dialogueWindow);
            dialogueBox.displayNonResponseDialogue();
        }
    }

    //places the mission marker (with countdown timer) on the map at the location specified by which mission it is
    void placeMissionMarker(Mission mission, ImageView imageView, Label zeroMinutesLabel, Label countdownLabel){
        imageView.setFitHeight(windowHeight/4);     //change size here if desired so it'll still scale with window size
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        zeroMinutesLabel.setFont(new Font(windowHeight/18));
        countdownLabel.setFont(new Font(windowHeight/18));

        //zeroMinutesLabel and countdownLabel have added values to make them fit within the mission marker
        //zero and countdown have different added values for RightAnchor to make them appear next to each other
        AnchorPane.setRightAnchor(zeroMinutesLabel, (windowWidth/mission.getxLocationFactor()) + windowHeight/8.3); //needed to scale based on window size
        AnchorPane.setRightAnchor(countdownLabel, (windowWidth/mission.getxLocationFactor()) + windowHeight/11);    //numbers from testing/estimating
        AnchorPane.setRightAnchor(imageView, (windowWidth/mission.getxLocationFactor()));
        AnchorPane.setTopAnchor(zeroMinutesLabel, (windowHeight/mission.getyLocationFactor()) + windowHeight/18.4);
        AnchorPane.setTopAnchor(countdownLabel, (windowHeight/mission.getyLocationFactor()) + windowHeight/18.4);
        AnchorPane.setTopAnchor(imageView, (windowHeight/mission.getyLocationFactor()));

        int depth = 70; //Setting the uniform variable for the glow width and height

        DropShadow borderGlow= new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.BLUE);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);

        imageView.setOnMouseEntered(e -> imageView.setEffect(borderGlow));
        zeroMinutesLabel.setOnMouseEntered(e -> imageView.setEffect(borderGlow));   //have to be added because they're on top
        countdownLabel.setOnMouseEntered(e -> imageView.setEffect(borderGlow));

        imageView.setOnMouseExited(e -> imageView.setEffect(null));
        zeroMinutesLabel.setOnMouseExited(e -> imageView.setEffect(null));
        countdownLabel.setOnMouseExited(e -> imageView.setEffect(null));
    }

    //updates the list of subordinates on the bottom-left of the primary stage based upon the currently available subordinates
    //does NOT change the images displayed when choosing "Assign Members" on a mission, that's done in DialogueBoxController
    void updateSubordinateImages(AnchorPane layout){
        int i=0;
        for(Subordinate sub : gameState.getTeamList()){      //put all subordinates in horizontal line at bottom-left
            if(layout.getChildren().contains(sub.getSubImageView(windowHeight))){   //if the sub is already there visually
                int subSpot = layout.getChildren().indexOf(sub.getSubImageView(windowHeight));  //get where the sub is in the layout list
                layout.getChildren().remove(subSpot, subSpot+5);
                //since the sub image will always be added with its corresponding rectangle and label right after
                //remove the things where the sub is and the 4 things after
                //+5 because just doing +4 makes it leave the name and strength label
            }

            //rest is just re-adding them, but will include people who might not have been there before, in order
            AnchorPane.setLeftAnchor(sub.getSubImageView(windowHeight), (5 + (windowHeight / 10 + 5) * i));
            //first 5 gets off left wall, second puts 5 pixels between each
            //windowHeight/10 so it'll scale with the window size, but also so it'll match the ImageView
            //see Subordinate.getSubImageView
            AnchorPane.setBottomAnchor(sub.getSubImageView(windowHeight), windowHeight / 14.9);
            sub.getSubImageView(windowHeight).setOnMouseEntered(null);  //without these, they might resize from being in the assigning box
            sub.getSubImageView(windowHeight).setOnMouseExited(null);
            layout.getChildren().add(sub.getSubImageView(windowHeight));

            Rectangle staminaAmountRect = new Rectangle((windowHeight / 9.95) * (1-(5-sub.getStamina())*0.2) , windowHeight / 70);
            staminaAmountRect.setStroke(Color.BLACK);
            staminaAmountRect.setFill(Color.LIMEGREEN.brighter());
            AnchorPane.setLeftAnchor(staminaAmountRect, (5 + (windowHeight / 10 + 5) * i));
            AnchorPane.setBottomAnchor(staminaAmountRect, windowHeight / 6.2);
            layout.getChildren().add(staminaAmountRect);    //add this one first to have it overlayed by the border rectangle

            Rectangle staminaBorderRect = new Rectangle(windowHeight / 9.95 , windowHeight / 70);
            staminaBorderRect.setStroke(Color.BLACK);
            staminaBorderRect.setFill(Color.TRANSPARENT);
            AnchorPane.setLeftAnchor(staminaBorderRect, (5 + (windowHeight / 10 + 5) * i));
            AnchorPane.setBottomAnchor(staminaBorderRect, windowHeight / 6.2);
            layout.getChildren().add(staminaBorderRect);

            //forms the fade from skyblue to white in the rectangle below sub images
            Stop[] stops = new Stop[]{new Stop(0, Color.SKYBLUE.brighter()), new Stop(1, Color.WHITE)};
            LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

            Rectangle rect = new Rectangle(windowHeight / 9.95, windowHeight / 15, linearGradient);
            AnchorPane.setLeftAnchor(rect, (5 + (windowHeight / 10 + 5) * i));
            AnchorPane.setBottomAnchor(rect, 0.0);
            layout.getChildren().add(rect);

            Label nameAndStrengthLabel = new Label(sub.getName() + "\n" + sub.getStrength());
            nameAndStrengthLabel.setFont(new Font(windowHeight / 45));
            nameAndStrengthLabel.setLineSpacing(0);
            AnchorPane.setLeftAnchor(nameAndStrengthLabel, (10 + (windowHeight / 10 + 5) * i));
            AnchorPane.setBottomAnchor(nameAndStrengthLabel, 0.0);
            layout.getChildren().add(nameAndStrengthLabel);

            i++;
        }

        for(Subordinate subOnMission : gameState.getAwayOnMissionSubordinates()){
            //check that nobody in the "away on mission" list is shown in the layout
            if(layout.getChildren().contains(subOnMission.getSubImageView(windowHeight))){   //if the sub is on the layout
                int subSpot = layout.getChildren().indexOf(subOnMission.getSubImageView(windowHeight));  //get where the sub is in the layout list
                layout.getChildren().remove(subSpot, subSpot+5);
            }
        }
    }

    //resets the display to just the image of the city (the background image) and available subordinates
    void resetDayVisuals(AnchorPane layout){
        displayStatus = DisplayStatus.DAY;
        this.layout = layout;
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);   //in case the window size changes from in-game menu option
        window.setResizable(false);
        backgroundImage = new ImageView(new Image("file:Images/PixelCity.png", windowWidth, windowHeight, false, true));

        layout.getChildren().clear();
        layout.getChildren().add(backgroundImage);
        updateSubordinateImages(layout);
    }
}