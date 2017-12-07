import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.stream.Collectors;

//this class handles anything having to do with text communication with the user in a new pop-up window
//such as when a mission is clicked on, or when a subordinate wants to ask for the day off
public class DialogueBoxController {
    private String question;
    private String[] possibleResponses;
    private Stage dialogueWindow;
    private Scene sceneToDisplay;
    private ArrayList<ImageView> subImageViews;
    private static int response;
    private static boolean dayOffResponse;

    //sets up the dialogue box to have priority, not be resizeable, and close upon pressing the ESC key
    DialogueBoxController(String question, String[] possibleResponses, String missionName, Stage dialogueWindow){
        this.question = question;
        this.possibleResponses = possibleResponses;
        this.dialogueWindow = dialogueWindow;
        subImageViews = new ArrayList<>();

        //Block clicking and everything in other windows
        dialogueWindow.initModality(Modality.APPLICATION_MODAL);
        dialogueWindow.setTitle("Handling " + missionName);
        dialogueWindow.setResizable(false);

        dialogueWindow.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if((e.getCode() == KeyCode.ESCAPE)){
                dialogueWindow.close();
            }
        });
    }

    //this method's so that I don't have to create multiple of these objects
    //I can just update the values within one for any Mission that needs a dialogue box
    void updateQuestionAndResponses(String question, String[] possibleResponses){
        this.question = question;
        this.possibleResponses = possibleResponses;
    }

    //displays a message and a "click here to continue" button, commonly used for "Successful mission!" or error messages
    void displayNonResponseDialogue(){
        Label messageLabel = new Label(question);
        messageLabel.setId("chat-label");
        Button continueButton = new Button("Click here or press ESC to close this window");

        StackPane layout = new StackPane(); //StackPane because this dialogue doesn't really require finite placement of multiple things
        StackPane.setAlignment(messageLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(continueButton, Pos.BOTTOM_CENTER);
        layout.getChildren().addAll(messageLabel, continueButton);
        layout.setMinSize(300,120);
        addEscEventHandlerToLayout(layout);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:Source/DialogueBoxFormatting.css");

        continueButton.setOnMouseClicked(e-> dialogueWindow.close());

        if(dialogueWindow == null){
            //During play-testing, I once crashed because dialogueWindow was a NullPointer when trying to display an error message
            //Have not been able to replicate the circumstances since, but I added this just in case
            Stage newDialogueWindow = new Stage();

            newDialogueWindow.initModality(Modality.APPLICATION_MODAL);
            newDialogueWindow.setTitle("Error");
            newDialogueWindow.setResizable(false);

            newDialogueWindow.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if((e.getCode() == KeyCode.ESCAPE)){
                    newDialogueWindow.close();
                }
            });
            dialogueWindow = newDialogueWindow;
        }
        dialogueWindow.setScene(sceneToDisplay);
        dialogueWindow.showAndWait();
    }

    //returns either:
    // - the spot in the arrayList to find the chosen subordinate
    // - arrayListSize+1 to indicate "done"
    // - arrayListSize+2 to indicate "cancel"
    int displaySubChoiceDialogueBox(ArrayList<Subordinate> subordinates, int maxSubordinates, int missionValue){
        response = subordinates.size()+2;   //making it default to the cancel button option

        subImageViews.clear();
        setupSubChoiceBox(subordinates, maxSubordinates, missionValue);

        dialogueWindow.sizeToScene();   //there's some sort of bug in JavaFX apparently where using setResizable(false) on a window
        dialogueWindow.showAndWait();   //makes it pop up with extra margins at first, then shrink.  Stage::sizeToScene() fixes it
        return response;
    }

    //displays a dialogue window where the user chooses from three options, returns which option they chose
    int displayUserResponseDialogueBox(){
        response = 3;   //setting it default to show Cancel or Continue, they're both in the third slot when passed in
        Label optionLabel = new Label(question);
        optionLabel.setId("chat-label");
        Button dialogueOption1Button = new Button(possibleResponses[0]);
        Button dialogueOption2Button = new Button(possibleResponses[1]);
        Button dialogueOption3Button = new Button(possibleResponses[2]);

        dialogueOption1Button.setOnMouseClicked(e-> {dialogueWindow.close(); response = 1;});
        dialogueOption2Button.setOnMouseClicked(e-> {dialogueWindow.close(); response = 2;});
        dialogueOption3Button.setOnMouseClicked(e-> {dialogueWindow.close(); response = 3;});

        StackPane layout = new StackPane(); //StackPane since this layout doesn't really require finite placement
        StackPane.setAlignment(dialogueOption1Button, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(dialogueOption2Button, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(dialogueOption3Button, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(optionLabel, Pos.TOP_CENTER);
        layout.getChildren().addAll(optionLabel, dialogueOption1Button, dialogueOption2Button, dialogueOption3Button);
        layout.setMinSize(300,100);
        addEscEventHandlerToLayout(layout);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:Source/DialogueBoxFormatting.css");

        dialogueWindow.setScene(sceneToDisplay);
        dialogueWindow.showAndWait();
        return response;
    }

    //adds an EventHandler to the Pane (visuals), not the Stage (window) so the window will close upon pressing ESC
    private void addEscEventHandlerToLayout(Pane layout){
        layout.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if((e.getCode() == KeyCode.ESCAPE)){
                dialogueWindow.close();
            }
        });
    }

    //setups up the sub choice dialogue box, for the first time it appears after pressing "Assign Members"
    private void setupSubChoiceBox(ArrayList<Subordinate> subordinates, int maxSubordinates, int missionValue){
        AnchorPane layout = new AnchorPane();

        int i = 0;  //tracking int used to place images side-by-side
        for(Subordinate sub : subordinates) {
            final int iFinal = i;     //the lambda expression gets picky if I just put i in it, says it has to be final
            String subImageURL = sub.getSubImageView(DisplayController.getWindow().getHeight()).getImage().impl_getUrl();
            //needs to be DisplayController's window height or else it'll shrink the images in the primary stage
            ImageView subImageViewCopy = new ImageView(new Image(subImageURL, 30, 30, false, true));
            //have to form copies of the sub images, because otherwise they move from bottom-left to the dialogue box
            AnchorPane.setLeftAnchor(subImageViewCopy, (5 + (35.0) * i));
            AnchorPane.setBottomAnchor(subImageViewCopy, 46.5);
            layout.getChildren().add(subImageViewCopy);
            subImageViewCopy.setOnMouseClicked(e -> {
                dialogueWindow.close();
                response = iFinal;
            });
            subImageViews.add(subImageViewCopy);

            Rectangle staminaAmountRect = new Rectangle(30 * (1-(5-sub.getStamina())*0.2) , 5);
            staminaAmountRect.setStroke(Color.BLACK);
            staminaAmountRect.setFill(Color.LIMEGREEN.brighter());
            AnchorPane.setLeftAnchor(staminaAmountRect, (5 + 35.0 * i));
            AnchorPane.setBottomAnchor(staminaAmountRect, 75.0);
            layout.getChildren().add(staminaAmountRect);    //add this one first to have it overlaid by the border rectangle
            staminaAmountRect.setOnMouseClicked(e -> {
                dialogueWindow.close();
                response = iFinal;
            });

            Rectangle staminaBorderRect = new Rectangle(30, 5);
            staminaBorderRect.setStroke(Color.BLACK);
            staminaBorderRect.setFill(Color.TRANSPARENT);
            AnchorPane.setLeftAnchor(staminaBorderRect, (5 + 35.0 * i));
            AnchorPane.setBottomAnchor(staminaBorderRect, 75.0);
            layout.getChildren().add(staminaBorderRect);
            staminaBorderRect.setOnMouseClicked(e -> {
                dialogueWindow.close();
                response = iFinal;
            });

            //forms the fade from sky-blue to white in the rectangle below sub images
            Stop[] stops = new Stop[]{new Stop(0, Color.SKYBLUE.brighter()), new Stop(1, Color.WHITE)};
            LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

            Rectangle rect = new Rectangle(30, 13, linearGradient);
            AnchorPane.setLeftAnchor(rect, (5 + (35.0) * i));
            AnchorPane.setBottomAnchor(rect, 34.5);
            layout.getChildren().add(rect);
            rect.setOnMouseClicked(e -> {
                dialogueWindow.close();
                response = iFinal;
            });

            Label strengthLabel = new Label(Integer.toString(sub.getStrength()));
            AnchorPane.setLeftAnchor(strengthLabel, (10 + (35.0) * i));
            AnchorPane.setBottomAnchor(strengthLabel, 34.5);
            layout.getChildren().add(strengthLabel);
            strengthLabel.setOnMouseClicked(e -> {
                dialogueWindow.close();
                response = iFinal;
            });

            int depth = 70; //Setting the uniform variable for the glow width and height
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.BLUE);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);

            subImageViewCopy.setOnMouseEntered(e -> subImageViewCopy.setEffect(borderGlow));
            rect.setOnMouseEntered(e -> subImageViewCopy.setEffect(borderGlow));
            strengthLabel.setOnMouseEntered(e -> subImageViewCopy.setEffect(borderGlow));
            subImageViewCopy.setOnMouseExited(e -> subImageViewCopy.setEffect(null));
            rect.setOnMouseExited(e -> subImageViewCopy.setEffect(null));
            strengthLabel.setOnMouseExited(e -> subImageViewCopy.setEffect(null));

            i++;
        }

        Label optionLabel = new Label(question);
        optionLabel.setId("chat-label");
        AnchorPane.setTopAnchor(optionLabel, 0.0);
        AnchorPane.setLeftAnchor(optionLabel, 30.0);

        Button DoneButton = new Button("Done");
        AnchorPane.setBottomAnchor(DoneButton, 0.0);
        AnchorPane.setLeftAnchor(DoneButton, 70.0);
        DoneButton.setOnMouseClicked(e-> {dialogueWindow.close(); response = subordinates.size()+1;});

        Button CancelButton = new Button("Cancel");
        AnchorPane.setBottomAnchor(CancelButton, 0.0);
        AnchorPane.setRightAnchor(CancelButton, 70.0);    //right instead of left
        CancelButton.setOnMouseClicked(e-> {dialogueWindow.close(); response = subordinates.size()+2;});

        Label maxSubordinatesAndMissionValueLabel = new Label("Max people: " + maxSubordinates + "\t\tMission value: " + missionValue);
        maxSubordinatesAndMissionValueLabel.setId("chat-label");
        AnchorPane.setTopAnchor(maxSubordinatesAndMissionValueLabel, 20.0);
        AnchorPane.setLeftAnchor(maxSubordinatesAndMissionValueLabel, 0.0);

        layout.getChildren().addAll(optionLabel, maxSubordinatesAndMissionValueLabel, DoneButton, CancelButton);
        layout.setMinSize(300,130);
        addEscEventHandlerToLayout(layout);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:Source/DialogueBoxFormatting.css");

        dialogueWindow.setScene(sceneToDisplay);
    }

    //goes through the list of subImageViews to see if they match with the subordinates passed to this function
    //if there's a subImageView that doesn't match the subordinates given to this function, it removes that ImageView
    void updateSubChoiceBox(ArrayList<Subordinate> subordinates){
        AnchorPane layout = (AnchorPane) dialogueWindow.getScene().getRoot();
        //this will only be called when there's a SubChoiceDialogueBox() open, which uses AnchorPanes

        ArrayList<String> subordinateImageStrings = subordinates.stream().map(sub -> sub.getSubImageView(DisplayController.getWindow().getHeight()).getImage().impl_getUrl()).collect(Collectors.toCollection(ArrayList::new));
        //goes through the list of subordinates, and adds all their image urls to this new ArrayList

        ImageView imageViewToRemove = null;
        for(ImageView subImageView : subImageViews) {
            if(!subordinateImageStrings.contains(subImageView.getImage().impl_getUrl())) {
                int subImageIndex = layout.getChildren().indexOf(subImageView);
                layout.getChildren().remove(subImageIndex, subImageIndex+5);    //+3 to get rid of the ImageView, the rectangle under it, and the label on the rectangle
                imageViewToRemove = subImageView;   //can't remove it from the list here because the for loop is going through the list
            }
        }
        if(imageViewToRemove != null){
            subImageViews.remove(imageViewToRemove);
        }
    }

    Boolean displaySubAskingForDayOff(Subordinate sub){
        dayOffResponse = false;     //defaulting it to not give them the day off

        AnchorPane layout = new AnchorPane(); //StackPane since this layout doesn't really require finite placement

        Label optionLabel = new Label(question);
        AnchorPane.setTopAnchor(optionLabel, 0.0);
        AnchorPane.setLeftAnchor(optionLabel, 5.0);
        optionLabel.setId("chat-label");

        Button okayButton = new Button("Okay");
        AnchorPane.setBottomAnchor(okayButton, 0.0);
        AnchorPane.setLeftAnchor(okayButton, 70.0);
        okayButton.setOnMouseClicked(e-> {dialogueWindow.close(); dayOffResponse = true;});

        Button noButton = new Button("No");
        AnchorPane.setBottomAnchor(noButton, 0.0);
        AnchorPane.setRightAnchor(noButton, 70.0);
        noButton.setOnMouseClicked(e-> {dialogueWindow.close(); dayOffResponse = false;});

        layout.getChildren().addAll(optionLabel, okayButton, noButton);
        layout.setMinSize(300,145);
        addEscEventHandlerToLayout(layout);

        String subImageURL = sub.getSubImageView(DisplayController.getWindow().getHeight()).getImage().impl_getUrl();
        //needs to be DisplayController's window height or else it'll shrink the images in the primary stage
        ImageView subImageViewCopy = new ImageView(new Image(subImageURL, 30, 30, false, true));
        //have to form copies of the sub images, because otherwise they move from bottom-left to the dialogue box
        AnchorPane.setLeftAnchor(subImageViewCopy, 140.0);
        AnchorPane.setBottomAnchor(subImageViewCopy, 59.5);
        layout.getChildren().add(subImageViewCopy);

        Rectangle staminaAmountRect = new Rectangle(30 * (1-(5-sub.getStamina())*0.2) , 5);
        staminaAmountRect.setStroke(Color.BLACK);
        staminaAmountRect.setFill(Color.LIMEGREEN.brighter());
        AnchorPane.setLeftAnchor(staminaAmountRect, 140.0);
        AnchorPane.setBottomAnchor(staminaAmountRect, 88.0);
        layout.getChildren().add(staminaAmountRect);    //add this one first to have it overlaid by the border rectangle

        Rectangle staminaBorderRect = new Rectangle(30, 5);
        staminaBorderRect.setStroke(Color.BLACK);
        staminaBorderRect.setFill(Color.TRANSPARENT);
        AnchorPane.setLeftAnchor(staminaBorderRect, 140.0);
        AnchorPane.setBottomAnchor(staminaBorderRect, 88.0);
        layout.getChildren().add(staminaBorderRect);

        //forms the fade from sky-blue to white in the rectangle below sub images
        Stop[] stops = new Stop[]{new Stop(0, Color.SKYBLUE.brighter()), new Stop(1, Color.WHITE)};
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        Rectangle rect = new Rectangle(30, 26, linearGradient);
        AnchorPane.setLeftAnchor(rect, 140.0);
        AnchorPane.setBottomAnchor(rect, 34.5);
        layout.getChildren().add(rect);

        Label nameAndStrengthLabel = new Label(sub.getName()+"\n"+sub.getStrength());
        nameAndStrengthLabel.setId("name-strength-label");  //call css to change font size from 10 to 9 and weight from bold to normal
        AnchorPane.setLeftAnchor(nameAndStrengthLabel, 140.0);
        AnchorPane.setBottomAnchor(nameAndStrengthLabel, 34.5);
        layout.getChildren().add(nameAndStrengthLabel);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:Source/DialogueBoxFormatting.css");

        dialogueWindow.setScene(sceneToDisplay);
        dialogueWindow.showAndWait();
        return dayOffResponse;
    }

    void displaySubLeaving(Subordinate sub){
        AnchorPane layout = new AnchorPane();

        Label optionLabel = new Label(question);
        AnchorPane.setTopAnchor(optionLabel, 0.0);
        if(question.contains("died"))  //sub dying from mission message
            AnchorPane.setLeftAnchor(optionLabel, 5.0);
        else
            AnchorPane.setLeftAnchor(optionLabel, 40.0);
        optionLabel.setId("chat-label");

        Button continueButton = new Button("Click here or press ESC to close this window");
        continueButton.setOnMouseClicked(e-> dialogueWindow.close());
        if(question.contains("died"))  //sub dying from mission message
            AnchorPane.setLeftAnchor(continueButton, 35.0);
        else
            AnchorPane.setLeftAnchor(continueButton, 27.5);
        AnchorPane.setBottomAnchor(continueButton, 0.0);

        layout.getChildren().addAll(optionLabel, continueButton);
        layout.setMinSize(300,145);
        addEscEventHandlerToLayout(layout);

        String subImageURL = sub.getSubImageView(DisplayController.getWindow().getHeight()).getImage().impl_getUrl();
        //needs to be DisplayController's window height or else it'll shrink the images in the primary stage
        ImageView subImageViewCopy = new ImageView(new Image(subImageURL, 30, 30, false, true));
        //have to form copies of the sub images, because otherwise they move from bottom-left to the dialogue box
        AnchorPane.setLeftAnchor(subImageViewCopy, 137.0);
        AnchorPane.setBottomAnchor(subImageViewCopy, 63.5);
        layout.getChildren().add(subImageViewCopy);

        //forms the fade from sky-blue to white in the rectangle below sub images
        Stop[] stops = new Stop[]{new Stop(0, Color.SKYBLUE.brighter()), new Stop(1, Color.WHITE)};
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        Rectangle rect = new Rectangle(30, 26, linearGradient);
        AnchorPane.setLeftAnchor(rect, 137.0);
        AnchorPane.setBottomAnchor(rect, 38.5);
        layout.getChildren().add(rect);

        Label nameAndStrengthLabel = new Label(sub.getName()+"\n"+sub.getStrength());
        nameAndStrengthLabel.setId("name-strength-label");  //call css to change font size from 10 to 9 and weight from bold to normal
        AnchorPane.setLeftAnchor(nameAndStrengthLabel, 137.0);
        AnchorPane.setBottomAnchor(nameAndStrengthLabel, 38.5);
        layout.getChildren().add(nameAndStrengthLabel);

        Line XbackSlash = new Line(30,0,0,40);
        XbackSlash.setId("line");   //couldn't get the css to just recognize any line, so this works I guess.  Sets width to 3 and color to red
        AnchorPane.setLeftAnchor(XbackSlash, 135.0);
        AnchorPane.setBottomAnchor(XbackSlash, 53.0);
        layout.getChildren().add(XbackSlash);

        Line XforwardSlash = new Line(0,0,30,40);
        XforwardSlash.setId("line");
        AnchorPane.setLeftAnchor(XforwardSlash, 135.0);
        AnchorPane.setBottomAnchor(XforwardSlash, 53.0);
        layout.getChildren().add(XforwardSlash);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:Source/DialogueBoxFormatting.css");

        dialogueWindow.setScene(sceneToDisplay);
        dialogueWindow.showAndWait();
    }
}
