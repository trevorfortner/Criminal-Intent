import com.sun.javafx.stage.StageHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

//this class manages everything to do with Missions, which pop up on the map randomly and can have Subordinates assigned to them
public class Mission {
    private String missionName;
    private int difficulty;
    private int maxSubordinates;
    private double xLocationFactor;
    //x and y factors to use to find where the marker will go on the map when a specific mission pops up (since it scales with window size)
    private double yLocationFactor;
    private final ImageView missionMarker = new ImageView(new Image("file:Images/MissionMarker.png"));
    //every mission will have the same marker, it's what will show up on the map to indicate there's a mission available
    private ImageView missionVisual;
    //image that will represent the mission visually for when the marker is clicked (i.e. image of a vault for a bank robbery)
    private ArrayList<Subordinate> assignedSubordinates;
    //keeps track of which subordinates are assigned to the mission, updated in chooseSubordinates()
    private String[] questionsForUser;  //questions to get user input
    private final String[][] reactionOptions = new String[][]{{"Assign members", "Ignore", "Cancel"}, {"Quit Game", "Send backup", "Continue"}};
    //possible responses to the questions (in the same order as the questions, 3 possible responses for each)
    private int missionValue;
    //how much the score will increase when the user passes a mission (decrease is a set value)
    private GameState gameState;
    private GameController gameController;
    private MissionResult result;
    //result of running this mission, either CLOSE_GAME, REMOVE_MARKER, or KEEP_MARKER

    Mission(String missionName, int difficulty, int baseMissionValue, double xLocationFactor, double yLocationFactor, ImageView missionVisual) {
        this.missionName = missionName;
        questionsForUser = new String[]{"How would you like to handle this " + missionName + "?", "Who would you like to assign?"};
        //have to initialize it here because it uses missionName, otherwise it'd show "How would you like to handle this null?"
        this.difficulty = difficulty;
        this.xLocationFactor = xLocationFactor;
        this.yLocationFactor = yLocationFactor;
        this.missionVisual = missionVisual;
        assignedSubordinates = new ArrayList<>();
        this.missionValue = baseMissionValue;
    }

    MissionResult getResult() {
        return result;
    }
    int getDifficulty() {
        return difficulty;
    }
    double getxLocationFactor() {
        return xLocationFactor;
    }
    double getyLocationFactor() {
        return yLocationFactor;
    }
    void setAssignedSubordinates(ArrayList<Subordinate> subordinates) {
        this.assignedSubordinates = subordinates;
    }
    //only used within MissionTest, for the sake of convenience

    //both returns an ImageView that scales on window size for other classes to place in their visuals
    ImageView getMissionMarker(double windowHeight) {
        missionMarker.setFitHeight(windowHeight / 2);     //change size here if desired so it'll still scale with window size
        missionMarker.setPreserveRatio(true);
        missionMarker.setSmooth(true);
        missionMarker.setCache(true);
        return missionMarker;
    }
    ImageView getMissionVisual(double windowHeight) {
        missionVisual.setFitWidth(windowHeight / 1.2);     //change size here if desired so it'll still scale with window size
        //change height into width because of everything else being passed height
        missionVisual.setPreserveRatio(true);
        missionVisual.setSmooth(true);
        missionVisual.setCache(true);
        return missionVisual;
    }

    void scaleForNewDay() {
        difficulty += 50;
        missionValue += 50;     //reward them for the missions getting harder later
    }

    //only called after tutorial day
    void scaleDownDifficulty() {
        difficulty -= 50;
    }

    //starts off a chain of calling functions to deal with when a mission is clicked
    //creates and displays first dialogue box for when a mission is clicked, returns if user selects ignore or cancel
    MissionResult handleMission(GameState gameState, GameController gameController) {
        Stage dialogueWindow = new Stage();
        DialogueBoxController dialogueBox = new DialogueBoxController(questionsForUser[0], reactionOptions[0], missionName, dialogueWindow);
        this.gameState = gameState;
        this.gameController = gameController;

        if (gameState.getTeamList().isEmpty()) {
            dialogueBox.updateQuestionAndResponses("You have no available subordinates.\nWait until you have more or close the window\nand press M for the option to end the day.", reactionOptions[1]);
            dialogueBox.displayNonResponseDialogue();
            return MissionResult.KEEP_MARKER;
        }

        dialogueBox.updateQuestionAndResponses(questionsForUser[0], reactionOptions[0]);    //how would you like to handle?
        int dialogueChoice = dialogueBox.displayUserResponseDialogueBox() - 1;        //it starts with 1, so for switch have to -1
        switch (dialogueChoice) {
            case 0: //"Assign people"
                assignedSubordinates.clear();
                maxSubordinates = (difficulty >= 50) ? difficulty / 50 : 1;
                //if difficulty >= 50, it's equal to difficulty/50, otherwise it's equal to 1 (caused issues with tutorial descaling missions)
                return chooseSubordinates(dialogueBox, false);     //it's fine to not have breaks here, because they return
            case 1:     //"Ignore"
                result = MissionResult.REMOVE_MARKER;
                return MissionResult.REMOVE_MARKER;
            case 2:     //"Cancel"
                result = MissionResult.KEEP_MARKER;
                return MissionResult.KEEP_MARKER;
        }
        return null;
    }

    //called upon user choosing "Assign Members"
    //creates and displays the dialogue box for assigning people to the mission
    //checks if teamlist empties for whatever reason, constantly updates the dialogue box to account for people being sent for backup
    private MissionResult chooseSubordinates(DialogueBoxController dialogueBox, Boolean sentBackup) {
        result = null;      //reset the result, in case this mission has been run multiple times

        ArrayList<Subordinate> teamList = gameState.getTeamList();  //just to refer to it easier

        if (gameState.getTeamList().isEmpty()) {     //need this here too, in case sending backup on another mission makes the list empty
            dialogueBox.updateQuestionAndResponses("You have no available subordinates.\nWait until you have more.", reactionOptions[1]);
            dialogueBox.displayNonResponseDialogue();
            return MissionResult.KEEP_MARKER;
        }

        while (!teamList.isEmpty() && assignedSubordinates.size() < maxSubordinates) {
            Timeline updateSubChoiceBoxTimeline = new Timeline(new KeyFrame(Duration.seconds(0.2)));
            //will update the dialogue box asking for subordinate choice every 0.2 seconds
            //this is needed in case someone is sent as backup for another mission while the assign window is open
            updateSubChoiceBoxTimeline.setOnFinished(e -> {
                if (teamList.isEmpty()) {
                    Stage currentWindow = StageHelper.getStages().get(1);
                    currentWindow.close();
                    //close the window that, with an empty teamList, will have an empty list of subordinates to choose from
                } else {
                    dialogueBox.updateSubChoiceBox(teamList);
                    updateSubChoiceBoxTimeline.playFromStart();
                }
            });
            updateSubChoiceBoxTimeline.play();

            dialogueBox.updateQuestionAndResponses(questionsForUser[1], reactionOptions[1]);
            int subChoice = dialogueBox.displaySubChoiceDialogueBox(teamList, maxSubordinates, missionValue);

            if (subChoice == teamList.size() + 2 && !sentBackup) {  //if they press "Cancel"
                for (Subordinate sub : assignedSubordinates) {        //bring back anyone already assigned to the mission
                    gameState.subordinateReturnsFromMission(sub);
                }
                return MissionResult.KEEP_MARKER;
            } else if(subChoice == teamList.size() + 2 && sentBackup){
                updateSubChoiceBoxTimeline.stop();
                return runMissionAndGiveResult(dialogueBox);
            } else if (subChoice == teamList.size() + 1) {  //if they press "Done"
                updateSubChoiceBoxTimeline.stop();
                break;
            } else {
                if (!teamList.isEmpty()) {
                    assignedSubordinates.add(teamList.get(subChoice));
                    gameState.sendSubordinateOnMission(teamList.get(subChoice));   //remove subordinates from teamList (for now)
                } else {
                    break;
                }
            }
            updateSubChoiceBoxTimeline.stop();
        }

        return runMissionAndGiveResult(dialogueBox);
    }

    //runs the mission and determines if it succeeds or fails
    //delays showing the result of the mission for a while, then creates and displays the dialogue boxes for the user to react to
    private MissionResult runMissionAndGiveResult(DialogueBoxController dialogueBox){
        if (!assignedSubordinates.isEmpty()) {
            Timeline waitToGiveResultTimeline = new Timeline(
                    new KeyFrame(Duration.seconds((gameState.getCurrentDay() / 5 + 5)),
                            //wait a certain amount of time before continuing with this code
                            //I kinda like this idea of making it based on the current day they're on
                            //Start with 5 seconds, then every 5 days increase the delay by 1 second
                            e -> Platform.runLater(() -> {  //eventhandler calls Platform.runLater()( {all of this} );
                                Boolean sentBackup = false;     //boolean to keep track of if backup is sent or not (to not resume the cycles if so)
                                Subordinate deadSub;

                                Pair<Boolean, Boolean> missionResults = runMission();

                                gameController.pauseCycles();

                                if (missionResults.getKey()) {      //if mission passed
                                    assignedSubordinates.forEach(Subordinate::increaseStrength);    //"level up" the subordinates upon successful mission
                                    gameState.increaseScore(missionValue);
                                    dialogueBox.updateQuestionAndResponses(
                                            missionName.substring(0, 1).toUpperCase() + missionName.substring(1) + " successful!\n" +   //capitalizes mission name
                                                    "Score increased by " + missionValue + ".\nNew Score: " + gameState.getPlayerScore(), reactionOptions[1]);
                                    dialogueBox.displayNonResponseDialogue();
                                    result = MissionResult.REMOVE_MARKER;
                                } else {  //if mission failed
                                    dialogueBox.updateQuestionAndResponses("Mission failed, someone might die if you don't send backup.", reactionOptions[1]);

                                    int MissionResultResponse = dialogueBox.displayUserResponseDialogueBox();
                                    switch (MissionResultResponse) {
                                        case 1: //Quit Game
                                            result = MissionResult.CLOSE_GAME;
                                            gameController.sendMissionResultQuitGameMessage();
                                            //result doesn't update as I expected because of the Timeline delaying this code
                                            //I couldn't find a way to wait for the Thread to finish while running the rest of the program,then returning result
                                            //so I just added functions to GameController and DisplayController that'll make a chain to close the window
                                            //since the other missionResults here are already accomplished by other parts of the code
                                            //as with the other cases, the marker is already removed, so it doesn't need to be removed again
                                            //but, result still needs to be assigned to something for GameController.reactToMissionClick() to know mission is done
                                            break;
                                        case 2: //Send reinforcements
                                            if (!gameState.getTeamList().isEmpty()) {  //check that there's people to be potential backups, if yes:
                                                sentBackup = true;
                                                result = chooseSubordinates(dialogueBox, true);
                                                break;
                                            } else {   //if there's nobody available to be backup
                                                dialogueBox.updateQuestionAndResponses("You have nobody available to be backup.\n" +
                                                        "Continuing.", reactionOptions[1]);
                                                dialogueBox.displayNonResponseDialogue();
                                                //purposefully don't have a break statement here, so it'll go into the next case
                                                //next case will show the results of the mission, add people back, and kill if necessary
                                            }
                                        case 3: //Continue
                                            //don't scale subordinate levels
                                            if (missionResults.getValue()) {                //if nobody died
                                                gameState.decreaseScore(100);
                                                dialogueBox.updateQuestionAndResponses("Nobody died.\n" +
                                                        "Score decreased 100.\nNew Score: " + gameState.getPlayerScore(), reactionOptions[1]);
                                                dialogueBox.displayNonResponseDialogue();
                                            } else {                                               //if someone died
                                                deadSub = killSubordinate();    //Get a reference to a random person that was sent on the mission
                                                assignedSubordinates.remove(deadSub);        //remove the dead sub from the list of assigned
                                                gameState.decreaseScore(250);               //so when the assigned subs are readded they won't be in it
                                                dialogueBox.updateQuestionAndResponses(deadSub.getName() + " died. Score decreased 250.\n" +
                                                        "New Score: " + gameState.getPlayerScore(), reactionOptions[1]);
                                                dialogueBox.displaySubLeaving(deadSub);     //they're leaving... the hard way...
                                            }
                                            result = MissionResult.REMOVE_MARKER;
                                            break;
                                    }
                                }

                                if (StageHelper.getStages().size() == 1 && !(sentBackup && !gameController.thereIsTimeLeftInDayForAnotherMissionToFinish())) {
                                    //if there's only 1 stage open (only primary can be open) and they didn't send backup without enough time left
                                    //then continue the cycles. If there's more than one open, cycles should already be paused, and will resume after the second closes
                                    //also check that they didn't send backup when there isn't enough time for the mission to finish
                                    if (!sentBackup) { //if they didn't send backup, regardless of time left in day, return the subs from the mission
                                        returnAllAssignedSubsFromMission();
                                    }
                                    gameController.resumeCycles();
                                } else {
                                    final boolean sentBackupFinal = sentBackup;
                                    //needed for lambda to be satisfied, won't changing at the end of a mission instance anyways
                                    Timeline waitForWindowCloseTimeline = new Timeline(new KeyFrame(Duration.seconds(.01)));
                                    //every 0.01 seconds, check and see if there's only one stage open (i.e. the other mission was closed)
                                    waitForWindowCloseTimeline.setOnFinished(e2 -> {
                                        if (!sentBackupFinal) {      //check this mission didn't send backup (if it did, don't wait and don't return the subs)
                                            if (StageHelper.getStages().size() == 1) {    //if so, return the subs from the mission
                                                returnAllAssignedSubsFromMission();
                                            } else {       //if not, check again in 0.01 seconds
                                                waitForWindowCloseTimeline.playFromStart();
                                            }
                                        }
                                    });
                                    waitForWindowCloseTimeline.play();
                                }
                            })
                    )
            );
            waitToGiveResultTimeline.play();

            return result;
        }
        else if (!gameState.getTeamList().isEmpty()) {  //assignedSubordinates empty and team list not empty
            dialogueBox.updateQuestionAndResponses("You need to select at least one\nsubordinate for the " + missionName + "!", reactionOptions[1]);
            dialogueBox.displayNonResponseDialogue();
            return chooseSubordinates(dialogueBox, false);
        } else {    //assignedSubordinates empty and team list empty
            return chooseSubordinates(dialogueBox, false);     //will cycle back up to the "You have no subordinates available" message
        }
    }

    //First Boolean = pass/fail mission, Second Boolean = all subordinates survive/one dies
    //T, * = Pass
    //F, T = Fail, no deaths
    //F, F = Fail, one death
    //package-private so MissionTest can call it without having to wait for runMissionAndGiveResult
    Pair<Boolean, Boolean> runMission() {
        int subStrengthTotal = 0;
        for (Subordinate sub : assignedSubordinates) {
            subStrengthTotal += sub.getStrength() * (1-(5-sub.getStamina())*.05);
            //reduce their strength by 5 percent per every stamina point missing from the 5 max
        }

        if (subStrengthTotal >= difficulty) {
            return new Pair<>(true, true);
        } else if (difficulty - subStrengthTotal < 100) {
            return new Pair<>(false, true);
        } else {
            return new Pair<>(false, false);
        }
    }

    //randomly determines who to kill from the list of subordinates assigned to the mission
    private Subordinate killSubordinate() {  //outputs which sub to kill from the list of ones assigned to the mission
        Random random = new Random();
        int subToKill = random.nextInt(assignedSubordinates.size());    //returns random number between 0 and number of subordinates assigned
        return assignedSubordinates.get(subToKill);
    }

    //seperate function just to not have to put this loop in the run() like five times
    //will be called upon the timeline completing, EXCEPT when user chooses "send backup"
    //because the original assigned people shouldn't return until the backup would come back too
    //also except when they choose "quit game" because no use using the time to add them back if the game's closing
    private void returnAllAssignedSubsFromMission() {
        for (Subordinate sub : assignedSubordinates) {
            gameState.subordinateReturnsFromMission(sub);
        }
        assignedSubordinates.clear();
    }
}
