import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Random;

//this class manages things having to do with the background processes of the game
//such as updating the GameState and calling DisplayController to update visuals
public class GameController extends Application {
    private static DisplayController display = new DisplayController();
    private GameState currentState;
    //these are basically all variables that are going to be used in the lambda statements later, so they're "effectively final"
    private final int MISSIONMARKERVISIBLETIME = 2;    //(how long the user will have to react to a mission popup)-1
    //I kinda like the 1 (technically 2) second idea, very fast paced and fun (imo)
    //Maybe try out 2 (technically 3) seconds
    private final int MISSIONLOOPNUMBER = 1;   //(how many times the list of missions will loop)-1
    //2 is way too long with 10+ missions at this point
    private Label countdownLabel = new Label();
    private ListIterator<Mission> missionIterator;
    private Timeline missionCycleTimeline;  //iterates through the list of missions and displays them
    private Timeline countdownTimeline;     //shows time left to react to each mission
    private Mission missionToUse;
    private int timelineIterationTracker;
    //need this because CycleCount will just return the size of the array we're iterating through
    //so to get a day to last 5 mins, for example, we have to solve
    //[5 mins = missionCycleTimeline.getTotalDuration() * timelineIterationTracker]
    //then put that number for timelineIterationTracker into the "onFinish" function for missionCycleTimeline

    //JavaFX's way of starting a program
    public static void main(String[] args){
        launch(args);
    }

    @Override   //called from launch(args) in main(), calls main menu
    public void start(Stage primaryStage) {
        primaryStage.setHeight(480);
        primaryStage.setWidth(800);
        primaryStage.setResizable(false);

        display.setWindow(primaryStage);
        display.setGameController(this);
        display.displayMainMenu();
    }

    //only used in GameControllerTest
    GameState getCurrentState() {
        return currentState;
    }

    //manages what to do when a user clicks on a mission marker
    private void reactToMissionClick(AnchorPane layout, Mission mission, Label zeroMinutesLabel, ImageView imageView){
        pauseCycles();

        display.setupWithMissionVisual(mission, layout);
        MissionResult missionResult;

        double currentMissionTimeToRespond = currentState.getCurrentDay()/5+5;     //how long the timeline waits for before returning a result (scales based on day)
        if(!thereIsTimeLeftInDayForAnotherMissionToFinish()){
            //basically, if there isn't enough time left in the day for the mission to display its result
            //delay the day ending by showing just the default day image (city + subordinates)
            //see comments for function below for explanation of how this is calculated
            missionResult = mission.handleMission(currentState, this);

            if(missionResult != null) {     //if they did cancel or ignore (would come back immediately, but assign wouldn't)
                resumeCycles();
            }
            else{
                Timeline waitForMissionFinishTimeline = new Timeline(
                        new KeyFrame(Duration.ZERO, e -> display.resetDayVisuals(layout)),
                        //first one just resets what's seen on the screen (gets rid of missionVisual and puts subordinates and background image)
                        new KeyFrame(Duration.seconds(currentMissionTimeToRespond+.201))
                        //+.201 because OnFinished will call playFrom(time+.01) on itself, so it'll restart after the layout setting
                        //then I want it to "finish" again every .2 seconds
                        //So it'll check if postThreadResult (see OnFinished below) was updated every .2 seconds
                );
                waitForMissionFinishTimeline.setOnFinished(e -> {
                    if(mission.getResult() == null){  //if the mission didn't finish by then, aka user didn't close last window or did "send backup"
                        display.resetDayVisuals(layout);
                        waitForMissionFinishTimeline.playFrom(Duration.seconds(currentMissionTimeToRespond+.01));
                    }
                    else{
                        resumeCycles();
                    }
                });
                waitForMissionFinishTimeline.play();
            }
        }
        else{   //if there is time left in the day to finish
            missionResult = mission.handleMission(currentState, this);
            if(missionResult != null){
                display.resetDayVisuals(layout);
                resumeCycles();
            }
            else {
                Timeline waitForMissionFinishTimeline = new Timeline(new KeyFrame(Duration.seconds(0.2)));
                waitForMissionFinishTimeline.setOnFinished(e -> {
                    if (mission.getResult() == null) {
                        display.updateSubordinateImages(layout);    //updates bottom-left visual list as people are added and when backup is sent
                        waitForMissionFinishTimeline.playFromStart();
                    } else {        //mission's fully complete
                        display.updateSubordinateImages(layout);
                        //only update the images, because a new mission could have popped up in the time it took to complete the mission
                        //resetting the day would get rid of a new mission's marker
                    }
                });
                waitForMissionFinishTimeline.play();
                display.resetDayVisuals(layout);    //reset here because the mission will be dealt with
                resumeCycles();
            }
        }

        display.reactToMissionResults(layout, missionResult, mission, imageView, countdownLabel, zeroMinutesLabel);
    }

    //sets up the timelines for each day, including managing the countdown displayed on mission markers
    void nextDay(){
        ArrayList<Mission> missionList = currentState.getMissionList();    //for easier reference
        Collections.shuffle(missionList);       //so it's not always the same order for the first cycle in a day

        AnchorPane layout = new AnchorPane();
        timelineIterationTracker = 0;
        missionIterator = missionList.listIterator();
        missionToUse = missionIterator.next();  //grab the first mission and save it
        missionIterator.previous();             //then go back, so next is the next mission

        IntegerProperty timeSeconds = new SimpleIntegerProperty();  //can't bind a label to a normal int

        ImageView imageView = new ImageView();

        Label zeroMinutesLabel = new Label("0:0");      //can't get countdownLabel to add "0:0" to the front of the number
        zeroMinutesLabel.setTextFill(Color.BLACK);      //so just putting this label in front of it in the AnchorPane
        zeroMinutesLabel.setOnMouseClicked(e -> reactToMissionClick(layout, missionToUse, zeroMinutesLabel, imageView));

        countdownLabel.textProperty().bind(timeSeconds.asString());     //binds the text of the label to timeSeconds, which will update in a timeline
        countdownLabel.setTextFill(Color.BLACK);
        countdownLabel.setOnMouseClicked(e -> reactToMissionClick(layout, missionToUse, zeroMinutesLabel, imageView));

        double windowHeight = DisplayController.getWindow().getHeight();    //needed to scale image sizes

        missionCycleTimeline = new Timeline(        //can't figure out how to get gaps between missions if I wanted
                new KeyFrame(
                        Duration.ZERO,
                        e -> {
                            display.resetDayVisuals(layout); //refresh layout in case the mission marker disappeared after user input

                            layout.getChildren().addAll(imageView, zeroMinutesLabel, countdownLabel);

                            if (countdownTimeline != null) countdownTimeline.stop();
                            timeSeconds.set(MISSIONMARKERVISIBLETIME);        //starts off the countdown at MISSIONMARKERVISIBLETIME
                            countdownTimeline = new Timeline();
                            countdownTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(MISSIONMARKERVISIBLETIME + 1), new KeyValue(timeSeconds, 0)));
                            //KeyFrame lasts for MISSIONMARKERVISIBLETIME+1 (to make the countdown display 0), KeyValue counts down timeSeconds to 0
                            countdownTimeline.playFromStart();

                            if(missionIterator.hasNext()) {
                                missionToUse = missionIterator.next();
                            }

                            imageView.setImage(missionToUse.getMissionMarker(windowHeight).getImage());
                            imageView.setOnMouseClicked(e2 -> reactToMissionClick(layout, missionToUse, zeroMinutesLabel, imageView));

                            display.placeMissionMarker(missionToUse, imageView, zeroMinutesLabel, countdownLabel);
                        }
                ), new KeyFrame(Duration.seconds(MISSIONMARKERVISIBLETIME + 1)) //how long the image lasts, +1 so it'll show zero
        );

        missionCycleTimeline.setCycleCount(missionList.size());
        missionCycleTimeline.setOnFinished(e -> {
            timelineIterationTracker++;
            if(timelineIterationTracker > MISSIONLOOPNUMBER){       //at the end of the day
                //total day real time (in seconds) = MISSIONMARKERVISIBLETIME * (timelineIterationTracker+1) * (total missions in game)
                layout.getChildren().removeAll(imageView, zeroMinutesLabel, countdownLabel);
                missionCycleTimeline.stop();
                countdownTimeline.stop();
                currentState.scaleUpMissionsAndDay();    //scale missions
                display.displayDayTransition();    //then do between day transition
            }
            else {      //if it's not the end of the day
                Mission lastMissionBeforeShuffle = missionList.get(missionList.size()-1);
                Collections.shuffle(missionList);
                while(lastMissionBeforeShuffle == missionList.get(0)){
                    //check that the first mission in the newly shuffled list isn't the same as the last mission before shuffling
                    //otherwise, the cycle would just renew the marker in the same place, with a new countdown. Just looks odd
                    Collections.shuffle(missionList);
                }
                missionIterator = missionList.listIterator();
                missionCycleTimeline.playFromStart();
            }
        });
        missionCycleTimeline.play();

        DisplayController.getWindow().setScene(new Scene(layout));
        DisplayController.getWindow().show();
    }

    //called from main menu - where all missions and subordinates are created - calls askForTutorial
    void newGame(){
        ArrayList<Subordinate> starterSubs = new ArrayList<>();
        String[] subExcuses = {"My dog is sick.", "My neighbors had a fire.", "I have bone spurs.", "I accidentally sold my car.", "I'm too hungover."};

        starterSubs.add(new Subordinate("Susan", new ImageView(new Image("file:Images/starterSub1.png")), 200, 5, subExcuses, 0));
        starterSubs.add(new Subordinate("Joe", new ImageView(new Image("file:Images/starterSub2.png")), 150, 5, subExcuses, 0));
        starterSubs.add(new Subordinate("George", new ImageView(new Image("file:Images/starterSub3.png")), 100, 5, subExcuses, 0));
        //Image sources:
        //SmileDude - http://www.freepngimg.com/download/man/2-2-man-transparent.png
        //JimmyMom - http://daytoncreativephotography.com/wp-content/uploads/2013/06/Studio-business-portrait-of-woman-1.png
        //Person - https://alansands.com/images/ase_promo/download/hr/brown-outfit-1-hr.png

        ArrayList<Mission> missions = new ArrayList<>();
        //constructor = name, difficulty, value, xLocationFactor, yLocationFactor, image
        //reminder about Mission -> xLocationFactor is how much the window size will be DIVIDED BY to get the x location
        //increase in xFactor = move to right, increase in yFactor = move up (both must be >1 to appear on screen)
        //all x and y factors are chosen to point to specific buildings in the background image
        //because they're factors, increases have smaller effects than decreases
        missions.add(new Mission("bank robbery", 300, 700, 7, 3.7, new ImageView(new Image("file:Images/BankRobbery.jpg"))));
        missions.add(new Mission("home invasion", 120, 100, 1.4, 2.7, new ImageView(new Image("file:Images/House.jpg"))));
        missions.add(new Mission("jewellery heist", 150, 250, 2.5, 3.5, new ImageView(new Image("file:Images/JewelleryStore.jpg"))));
        missions.add(new Mission("murder", 240, 300, 50, 2.5, new ImageView(new Image("file:Images/Homicide.jpg"))));
        missions.add(new Mission("coercion", 105, 125, 3.2, 1.75, new ImageView(new Image("file:Images/Coercion.jpg"))));
        missions.add(new Mission("obstruction", 210, 200, 1.3, 30, new ImageView(new Image("file:Images/Obstruction.jpg"))));
        missions.add(new Mission("pimping", 180, 600, 8, 2, new ImageView(new Image("file:Images/Prostitution.jpg"))));
        missions.add(new Mission("armored truck heist", 270, 800, 1.8, 1.75, new ImageView(new Image("file:Images/ArmoredTruck.jpg"))));
        missions.add(new Mission("drug smuggling", 345, 900, 30, 20, new ImageView(new Image("file:Images/DrugSmuggling.jpg"))));
        missions.add(new Mission("pickpocketing", 75, 50, 1.7, 4.5, new ImageView(new Image("file:Images/Theft.jpg"))));
        //image sources in order:
        //bank robbery - https://localtvktvi.files.wordpress.com/2015/02/bank-robbery-mon.jpg?quality=85&strip=all&w=1200
        //home invasion - https://i.ytimg.com/vi/Xx6t0gmQ_Tw/maxresdefault.jpg
        //jewllery heist - http://retaildesignblog.net/wp-content/uploads/2011/12/Trewarne-Fine-jewelry-store-by-MIM-Design-Chadstone.jpg
        //murder - http://madison365.com/wp-content/uploads/2017/11/homicide-attorney-northern-va.jpg
        //coercion - http://images.wisegeek.com/young-man-pointing-gun-at-man-in-suit-who-is-using-a-phone.jpg
        //obstruction - http://cdn.cnn.com/cnnnext/dam/assets/170511181039-one-thing-justice-exlarge-169.jpg
        //prostitution - https://cdn.nyccriminallawyer.com/wp-content/uploads/2011/07/prostitute_in_newyork.jpg
        //robbery - http://img-aws.ehowcdn.com/340x221p/photos.demandstudios.com/getty/article/76/107/200544570-001.jpg
        //drug smuggle - http://www.haigbrown.com/wp-content/uploads/2014/11/Alex-F1.jpg
        //theft - https://www.lvcriminaldefense.com/wp-content/uploads/2015/03/Theft-Crimes.jpg

        ArrayList<Subordinate> possibleSubs = new ArrayList<>();
        String[] names = {"Dimitri", "John", "Mitch", "Jack", "Bonnie", "Clyde", "Pablo", "Ted", "Rich", "Gary", "Billy", "Ed", "Al",
                            "Aileen", "Ayman", "Josef", "Adolf", "Irma"};
        Random random = new Random();
        for(int i=1; i <= names.length; i++){
            int subStrength = random.nextInt(100)*10;
            possibleSubs.add(new Subordinate(names[i-1], new ImageView(new Image("file:Images/possSub"+i+".png")), subStrength, 5, subExcuses, subStrength*12-90));
            //adds a new subordinate with a name from the array above, a random strength between 0-500, a cost based on that strength, and an image from possSub1.png to possSub18.png
        }
        //Image sources in order (again):
        //possSub1 - https://res.cloudinary.com/jpress/image/fetch/w_520,h_520,c_scale/http://editorial.jpress.co.uk/web/AuthorBio/Images/160/profile.png
        //possSub2 - https://i.guim.co.uk/img/uploads/2017/10/09/Zach-Stafford,-R.png?w=300&q=55&auto=format&usm=12&fit=max&s=cbb87058919c629035b08728a80d29d0
        //possSub3 - https://i.guim.co.uk/img/static/sys-images/Guardian/Pix/pictures/2015/5/8/1431057546910/James_Woodford_L.png?w=300&q=55&auto=format&usm=12&fit=max&s=4d09a81a3c05382363bba93f3e72b3c9
        //possSub4 - https://res.cloudinary.com/demo/image/upload/w_200,h_250,c_fill,g_face/business_man_clipped.png
        //possSub5 - https://i.guim.co.uk/img/uploads/2017/10/06/Alissa_Quart,_L.png?w=300&q=55&auto=format&usm=12&fit=max&s=e7b0717626e0c7161ac023e7d03073bd
        //possSub6 - https://s3-us-west-2.amazonaws.com/s.cdpn.io/157670/person-transparent-background.png
        //possSub7 - https://www.denvergov.org/content/dam/denvergov/Portals/344/images/Image_Hancock2016_croppedNoBkgd.psd.png
        //possSub8 - http://www.pngmart.com/image/1846
        //possSub9 - https://i.guim.co.uk/img/uploads/2017/10/06/Ian-Sample,-R.png?w=300&q=55&auto=format&usm=12&fit=max&s=4d73107831cad1c07f47991c73bf38a7
        //possSub10 - http://www.completeselling.com/wp-content/uploads/John%20Chapin/Photos/John%20NEW%2004-13%20-%20BLPIC%20Transparent.png
        //possSub11 - https://coachdq.com/wp-content/uploads/2012/03/transparent-for-any-background.png
        //possSub12 - https://www.emdgroup.com/en/company/responsibility/how-we-do-business.html
        //possSub13 - https://lowenthal.house.gov/images/lowenthal-crop.png
        //possSub14 - https://www.ft.com/__origami/service/image/v2/images/raw/fthead:sarah-o-connor?source=next&fit=scale-down&width=150&compression=best&quality=highest&dpr=2
        //possSub15 - https://c.s-microsoft.com/en-gb/CMSImages/MS-Execs-2015-07-Nadella-Satya-24-2.png?version=1df74ca3-80d7-9ffe-593c-05292a3f8f87
        //possSub16 - https://mgtvwfla.files.wordpress.com/2016/05/600x338_ian_oliver.png?w=993
        //possSub17 - https://i.guim.co.uk/img/uploads/2017/10/09/Sam-Thielman,-L.png?w=300&q=55&auto=format&usm=12&fit=max&s=81499a3c22f63f0188e64ff42dede4f3
        //possSub18 - http://uploads.billionphotos.com/store/sample1-people.png

        currentState = new GameState(0, 0, starterSubs, missions, possibleSubs);

        display.askAboutTutorial(currentState);
    }

    void pauseCycles(){
        missionCycleTimeline.pause();
        countdownTimeline.pause();
    }

    //different from pausing, because it resets the play head to the initial position
    void stopCycles(){
        missionCycleTimeline.stop();
        countdownTimeline.stop();
    }

    void resumeCycles(){
        missionCycleTimeline.play();
        countdownTimeline.play();
    }

    //a method for other classes to be able to close the window
    void sendMissionResultQuitGameMessage(){
        DisplayController.getWindow().close();
    }

    //uses timelines and iterators to check if there's enough time in the day for another mission to finish
    Boolean thereIsTimeLeftInDayForAnotherMissionToFinish(){
        int missionListSize = currentState.getMissionList().size();
        double currentMissionTimeToRespond = currentState.getCurrentDay()/5+5;
        //how long the mission waits before returning a result (scales based on day)

        return (missionCycleTimeline.getCurrentTime().toSeconds() + (missionIterator.nextIndex()-1+timelineIterationTracker*missionListSize)*(MISSIONMARKERVISIBLETIME+1))
                < missionCycleTimeline.getTotalDuration().toSeconds()*(MISSIONLOOPNUMBER+1) - currentMissionTimeToRespond;
        //current time of day < [(the total time for a day) - (the time for a mission to return a MissionResult)]

        //left side of inequality = current time the day has been going on for =
            //currently displayed mission's time on screen so far +
            //{[next mission's index in the arrayList - 1] + [amount of times looped through all missions so far - 1]*(number of missions)} *
            //(amount of time each mission appears on screen)

            //missionCycleTimeline.getCurrentTime().toSeconds() = currently displayed mission's time on screen so far
            //missionIterator.nextIndex()-1 = [next mission's index in the arrayList - 1] (subtract 1 because it starts at 1)
            //timelineIterationTracker = [amount of times looped through all missions so far - 1]
            //missionListSize = current amount of missions available in the GameState
            //MISSIONMARKERVISIBLETIME+1 = [amount of time each mission appears on screen] (add 1 because we display 0 in the countdown)

        //right side of inequality = total day real time (in seconds) =
        //cycleTimelineTotalDuration *(MISSIONLOOPNUMBER+1) - (time to get response from mission)
        //cycleTimelineTotalDuration = total time to cycle through mission list
        //(MISSIONLOOPNUMBER+1) = number of times to cycle through the mission list (number of times to "loop")
        //currentMissionTimeToRespond = explained above when created in this function
    }
}