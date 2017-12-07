package DialogueTesting;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

class DialogueTestingMissionHomeInvasion extends DialogueTestingMission {
    private String[] QuestionsAtBank;
    private String[][] dialogueOptionsAtBank;
    private String chatAfterRobber;
    private static int dialogueChoice;

    DialogueTestingMissionHomeInvasion() {
        missionname = "home invasion";
        QuestionsAtBank = new String[]{"How would you like to handle this " + missionname + "?", "Who would you like to assign?", "Final Result!"};
        dialogueOptionsAtBank = new String[][]{{"Assign members", "Do it myself", "Ignore", "Cancel"}, {"Cop 1", "Cop 2", "Cop 3", "Cop 4"}, {"Quit Game", "Assign new members", "Retry", "Okay"}};
        chatAfterRobber = "I've never seen a robbery live before, that was neato.";
        characterImageView = new ImageView(new Image("file:Images/House.jpg"));
        characterImageView.setFitHeight(80);   //supposedly I can do this while loading the image for the imageview initially
        characterImageView.setFitWidth(100);   //but for me it just lowers the resolution and doesn't change the size
    }

    @Override
    public int talkToAtBank(DialogueTestingCityLandscape storyArea) {
        Stage dialogueWindow = new Stage();
        DialogueTestingDialogueBox dialogueBox = new DialogueTestingDialogueBox(QuestionsAtBank[0], dialogueOptionsAtBank[0], missionname, dialogueWindow);
        //doing dialogueOptionsAtBank[0] passes the entire array of [0][0], [0][1], [0][2], [0][3]

        for(int i=0; i < QuestionsAtBank.length-1; i++) {
            dialogueBox.updateQuestionAndResponses(QuestionsAtBank[i], dialogueOptionsAtBank[i]);
            dialogueBox.displayNonResponseDialogue();
            dialogueBox.displayResponseDialogueBox();
            /*switch(i){
                case 0: storyArea.moveRobberFirstTime(this); break;
                case 1: storyArea.moveRobberSecondTime(this); break;
                case 2: storyArea.moveRobberThirdTime(this); break;
                case 3: storyArea.moveRobberFourthTime(this); break;
            }*/
        }

        dialogueBox.updateQuestionAndResponses(QuestionsAtBank[QuestionsAtBank.length-1], dialogueOptionsAtBank[QuestionsAtBank.length-1]);
        dialogueBox.displayNonResponseDialogue();
        dialogueChoice = dialogueBox.displayReturnableResponseDialogueBox();
        //storyArea.moveRobberLastTime();

        return dialogueChoice;
    }

    @Override
    public void talkToAtBankAfterRobbery(){
        Stage dialogueWindow = new Stage();
        DialogueTestingDialogueBox dialogueBox = new DialogueTestingDialogueBox(chatAfterRobber, null, missionname, dialogueWindow);
        dialogueBox.displayNonResponseDialogue();
    }
}
