package DialogueTesting;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

class DialogueTestingDialogueBox {
    private String question;
    private String[] possibleResponses;
    private Stage dialogueWindow;
    private Scene sceneToDisplay;
    private static int response;

    DialogueTestingDialogueBox(String question, String[] possibleResponses, String missionname, Stage dialogueWindow){
        this.question = question;
        this.possibleResponses = possibleResponses;
        this.dialogueWindow = dialogueWindow;

        //Block clicking and everything in other windows
        dialogueWindow.initModality(Modality.APPLICATION_MODAL);
        dialogueWindow.setTitle("Handling " + missionname);
        dialogueWindow.setMinWidth(250);
        //dialogueWindow.setMaxWidth(300);
    }

    //this method's so that I don't have to create multiple of these objects
    //I can just update the values within one for the entire character
    void updateQuestionAndResponses(String question, String[] possibleResponses){
        this.question = question;
        this.possibleResponses = possibleResponses;
    }

    void displayNonResponseDialogue(){
        Label characterChatLabel = new Label();
        characterChatLabel.setText(question);
        characterChatLabel.setId("chat-label");
        Button continueButton = new Button("Click here to continue");

        StackPane layout = new StackPane();
        StackPane.setAlignment(characterChatLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(continueButton, Pos.BOTTOM_CENTER);
        layout.getChildren().addAll(characterChatLabel, continueButton);
        layout.setMinSize(300,100);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:PracticeCode/DialogueTesting/DialogueBoxFormatting.css");

        continueButton.setOnMouseClicked(e-> dialogueWindow.close());

        dialogueWindow.setScene(sceneToDisplay);
        dialogueWindow.showAndWait();
    }

    void displayResponseDialogueBox(){
        Label optionLabel = new Label();
        optionLabel.setText(question);
        Button dialogueOption1Button = new Button(possibleResponses[0]);
        Button dialogueOption2Button = new Button(possibleResponses[1]);
        Button dialogueOption3Button = new Button(possibleResponses[2]);
        Button dialogueOption4Button = new Button(possibleResponses[3]);

        StackPane layout = new StackPane();
        StackPane.setAlignment(dialogueOption1Button, Pos.CENTER_LEFT);
        StackPane.setAlignment(dialogueOption2Button, Pos.CENTER_RIGHT);
        StackPane.setAlignment(dialogueOption3Button, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(dialogueOption4Button, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(optionLabel, Pos.TOP_CENTER);
        layout.getChildren().addAll(optionLabel, dialogueOption1Button, dialogueOption2Button, dialogueOption3Button, dialogueOption4Button);
        layout.setMinSize(300,100);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:PracticeCode/DialogueTesting/DialogueBoxFormatting.css");

        dialogueOption1Button.setOnMouseClicked(e-> dialogueWindow.close());
        dialogueOption2Button.setOnMouseClicked(e-> dialogueWindow.close());
        dialogueOption3Button.setOnMouseClicked(e-> dialogueWindow.close());
        dialogueOption4Button.setOnMouseClicked(e-> dialogueWindow.close());

        dialogueWindow.setScene(sceneToDisplay);
        dialogueWindow.showAndWait();
    }

    int displayReturnableResponseDialogueBox(){
        Label optionLabel = new Label();
        optionLabel.setText(question);
        Button dialogueOption1Button = new Button(possibleResponses[0]);
        Button dialogueOption2Button = new Button(possibleResponses[1]);
        Button dialogueOption3Button = new Button(possibleResponses[2]);
        Button dialogueOption4Button = new Button(possibleResponses[3]);

        StackPane layout = new StackPane();
        StackPane.setAlignment(dialogueOption1Button, Pos.CENTER_LEFT);
        StackPane.setAlignment(dialogueOption2Button, Pos.CENTER_RIGHT);
        StackPane.setAlignment(dialogueOption3Button, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(dialogueOption4Button, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(optionLabel, Pos.TOP_CENTER);
        layout.getChildren().addAll(optionLabel, dialogueOption1Button, dialogueOption2Button, dialogueOption3Button, dialogueOption4Button);
        layout.setMinSize(300,100);

        sceneToDisplay = new Scene(layout);
        sceneToDisplay.getStylesheets().add("file:PracticeCode/DialogueTesting/DialogueBoxFormatting.css");

        dialogueOption1Button.setOnMouseClicked(e-> {dialogueWindow.close(); response = 1;});
        dialogueOption2Button.setOnMouseClicked(e-> {dialogueWindow.close(); response = 2;});
        dialogueOption3Button.setOnMouseClicked(e-> {dialogueWindow.close(); response = 3;});
        dialogueOption4Button.setOnMouseClicked(e-> {dialogueWindow.close(); response = 4;});

        dialogueWindow.setScene(sceneToDisplay);
        dialogueWindow.showAndWait();
        return response;
    }
}
