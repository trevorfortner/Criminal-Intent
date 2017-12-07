package DialogueTesting;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

class DialogueTestingMissionBankRobbery extends DialogueTestingMission {
    private String[] QuestionsAtBank;
    private String[][] dialogueOptionsAtBank;
    private String[] QuestionsAtHouse;
    private String[][] dialogueOptionsAtHouse;
    private static int dialogueChoice;

    DialogueTestingMissionBankRobbery() {
        missionname = "bank robbery";
        QuestionsAtBank = new String[]{"How would you like to handle this " + missionname + "?", "Who would you like to assign?", "Final Result!"};
        dialogueOptionsAtBank = new String[][]{{"Assign members", "Do it myself", "Ignore", "Cancel"}, {"Cop 1", "Cop 2", "Cop 3", "Cop 4"}, {"Quit Game", "Assign new members", "Retry", "Okay"}};
        QuestionsAtHouse = new String[]{"Boogly Woogly?", "Heeby Jeebees?", "Hamster Jamster?"};
        dialogueOptionsAtHouse = new String[][]{{"Orange", "Magenta", "Different word", "Yup"}, {"Absolutely", "Crayola blue ocean", "Georgio Banana", "Murderous Hamsters"}, {"Quit Game", "Assign new members", "Retry", "Okay"}};
        characterImageView = new ImageView(new Image("file:Images/BankRobbery.png"));
        characterImageView.setFitHeight(100);   //supposedly I can do this while loading the image for the imageview initially
        characterImageView.setFitWidth(100);    //but for me it just lowers the resolution and doesn't change the size
    }

    @Override
    public int talkToAtBank(DialogueTestingCityLandscape storyArea){
        //I've moved most of the responsibility of the dialogue into the DialogueBox object
        //It just created a lot of duplicate code in each Character subclass that was honestly unnecessary
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
        DialogueTestingDialogueBox dialogueBox = new DialogueTestingDialogueBox("Shhhh I'm not here right now.", null, missionname, dialogueWindow);
        dialogueBox.displayNonResponseDialogue();
    }

    public void talkToAtHouse(){
        Stage dialogueWindow = new Stage();
        DialogueTestingDialogueBox dialogueBox = new DialogueTestingDialogueBox(QuestionsAtHouse[0], dialogueOptionsAtHouse[0], missionname, dialogueWindow);
        //doing dialogueOptionsAtBank[0] passes the entire array of [0][0], [0][1], [0][2], [0][3]
        dialogueBox.displayNonResponseDialogue();
        dialogueBox.displayResponseDialogueBox();

        //since this Character (Joe) in this StoryArea (the House) doesn't have a story-altering dialogue choice, it returns void
        //since it returns void, we'll just loop through all the questions and dialogueOptions until the dialogue's over
        for(int i=1; i < QuestionsAtHouse.length; i++) {
            dialogueBox.updateQuestionAndResponses(QuestionsAtHouse[i], dialogueOptionsAtHouse[i]);
            dialogueBox.displayNonResponseDialogue();
            dialogueBox.displayResponseDialogueBox();
        }
    }
}
