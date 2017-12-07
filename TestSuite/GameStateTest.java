import javafx.application.Application;
import javafx.scene.image.ImageView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class GameStateTest{
    private GameState gameState;

    @BeforeClass
    public static void setUpClass() throws InterruptedException {
        //need to initialize JavaFX first, otherwise it won't let us use Images and stuff in these tests

        Thread jfxInitThread = new Thread("jfxInitThread") {
            //needs to be put in a thread otherwise an exception is thrown saying it can only be done in an event thread, not main
            public void run() {
                try {
                    Application.launch(simpleApp.class); //just calling this outside the Thread causes it to never happen
                }
                catch(IllegalStateException e){
                    //don't launch it, because then it's already launched by another test
                }
            }
        };
        jfxInitThread.start();
        Thread.sleep(500);   //give the thread half a second to setup everything necessary to run an Application
    }

    @Before
    public void setup() {
        ArrayList<Subordinate> starterSubs = new ArrayList<>();
        String[] subExcuses = {};
        //not testing anything about their excuses, so no need to fully creat the list

        starterSubs.add(new Subordinate("George", new ImageView(), 100, 5, subExcuses, 0));
        starterSubs.add(new Subordinate("Susan", new ImageView(), 200, 5, subExcuses, 0));
        starterSubs.add(new Subordinate("Joe", new ImageView(), 150, 5, subExcuses, 0));
        //not testing anything about their ImageViews, so no need to fully create them

        ArrayList<Mission> missions = new ArrayList<>();
        missions.add(new Mission("bank robbery", 300, 700, 7, 3.7, new ImageView()));
        missions.add(new Mission("home invasion", 120, 100, 1.4, 2.7, new ImageView()));
        missions.add(new Mission("jewellery heist", 150, 250, 2.5, 3.5, new ImageView()));
        missions.add(new Mission("murder", 240, 300, 50, 2.5, new ImageView()));
        missions.add(new Mission("coercion", 105, 125, 3.2, 1.75, new ImageView()));
        missions.add(new Mission("obstruction", 210, 200, 1.3, 30, new ImageView()));
        missions.add(new Mission("pimping", 180, 600, 8, 2, new ImageView()));
        missions.add(new Mission("armored truck", 270, 800, 1.8, 1.75, new ImageView()));
        missions.add(new Mission("drug smuggle", 345, 900, 30, 20, new ImageView()));
        missions.add(new Mission("theft", 75, 50, 1.7, 4.5, new ImageView()));
        //same thing with the ImageViews

        ArrayList<Subordinate> possibleSubs = new ArrayList<>();
        String[] names = {"Dimitri", "John", "Mitch", "Jack", "Bonnie", "Clyde", "Pablo", "Ted", "Rich", "Gary", "Billy", "Ed", "Al",
                "Aileen", "Ayman", "Josef", "Adolf", "Irma"};
        Random random = new Random();
        for(int i=1; i <= names.length; i++){
            int subStrength = random.nextInt(100)*10;
            possibleSubs.add(new Subordinate(names[i-1], new ImageView(), subStrength, 5, subExcuses, subStrength*12-90));
        }
        //same thing with the ImageViews

        gameState = new GameState(0, 0, starterSubs, missions, possibleSubs);
    }

    @Test
    public void testScoreDoesNotGoNegativeAndIncreasesCorrectly() {
        Assert.assertEquals(0, gameState.getPlayerScore());
        //expected, then actual value (confusing if it errors when backwards)

        for(int i=1; i <= 10; i++) {
            gameState.increaseScore(500);
            Assert.assertEquals(500*i, gameState.getPlayerScore());
        }

        for(int i=10; i >= 1; i--) {
            gameState.decreaseScore(500);
            Assert.assertEquals(500*i - 500, gameState.getPlayerScore());
        }
        Assert.assertEquals(0, gameState.getPlayerScore());

        gameState.decreaseScore(500);
        Assert.assertEquals(0, gameState.getPlayerScore());

        gameState.increaseScore(500);
        gameState.decreaseScore(1000);  //test it goes to zero if the decrease is bigger than the current
        Assert.assertEquals(0, gameState.getPlayerScore());
    }

    @Test
    public void testGetTeamListReturnsTeamListInOrder() {
        Collections.shuffle(gameState.getTeamList());
        ArrayList<Subordinate> teamListReturn = gameState.getTeamList();
        for(int i=0; i < teamListReturn.size()-1; i++){
            Subordinate beforeSub = teamListReturn.get(i);
            Subordinate afterSub = teamListReturn.get(i+1);
            Assert.assertTrue(beforeSub.compareTo(afterSub) < 0);   //make sure before is the one that's better
            //Remember, .compareTo returns a negative number if beforeSub is "greater than" afterSub (as is .compareTo convention)
        }
    }

    @Test
    public void testGetPossibleSubsReturnsListInOrder() {
        Collections.shuffle(gameState.getPossibleSubordinates());
        ArrayList<Subordinate> possSubsListReturn = gameState.getPossibleSubordinates();
        for(int i=0; i < possSubsListReturn.size()-1; i++){
            Subordinate beforeSub = possSubsListReturn.get(i);
            Subordinate afterSub = possSubsListReturn.get(i+1);
            Assert.assertTrue(beforeSub.compareTo(afterSub) < 0);   //make sure before is the one that's better
            //Remember, .compareTo returns a negative number if beforeSub is "greater than" afterSub (as is .compareTo convention)
        }
    }

    @Test
    public void testScaleUpMissionsAndDayScalesByRightAmounts() {
        ArrayList<Integer> startMissionDifficulties = gameState.getMissionList().stream().map(Mission::getDifficulty).collect(Collectors.toCollection(ArrayList::new));
        int startDayNum = gameState.getCurrentDay();
        gameState.scaleUpMissionsAndDay();
        for(int i=0; i < gameState.getMissionList().size(); i++){
            Integer startDifficulty = startMissionDifficulties.get(i);
            Integer endDifficulty = gameState.getMissionList().get(i).getDifficulty();
            Assert.assertTrue(endDifficulty - startDifficulty == 50);
        }
        int endDayNum = gameState.getCurrentDay();
        Assert.assertTrue(endDayNum - startDayNum == 1);
    }

    @Test
    public void testScaleDownMissionsAndDayScalesByRightAmounts() {
        ArrayList<Integer> startMissionDifficulties = gameState.getMissionList().stream().map(Mission::getDifficulty).collect(Collectors.toCollection(ArrayList::new));
        int startDayNum = gameState.getCurrentDay();
        gameState.scaleDownMissionsAndDay();
        for(int i=0; i < gameState.getMissionList().size(); i++){
            Integer startDifficulty = startMissionDifficulties.get(i);
            Integer endDifficulty = gameState.getMissionList().get(i).getDifficulty();
            Assert.assertEquals(-50, endDifficulty - startDifficulty);
        }
        int endDayNum = gameState.getCurrentDay();
        Assert.assertEquals(-1, endDayNum - startDayNum);
    }

    @Test
    public void testAddNewSubFromPossibleListUpdatesListsProperly() {
        Random random = new Random();
        for(int i = gameState.getPossibleSubordinates().size(); i > 0; i--){
            int subToMoveNum = random.nextInt(i);   //since the list will return in order, remove them in a random order
            Subordinate subToMove = gameState.getPossibleSubordinates().get(subToMoveNum);

            gameState.addNewSubFromPossibleList(subToMove);
            Assert.assertFalse(gameState.getPossibleSubordinates().contains(subToMove));
            Assert.assertTrue(gameState.getTeamList().contains(subToMove));
        }
        Assert.assertTrue(gameState.getPossibleSubordinates().isEmpty());
    }

    @Test
    public void testRestingAndReturningSubsFromRest() {
        Assert.assertTrue(gameState.getRestingSubordinates().isEmpty());

        Random random = new Random();
        for(int i = gameState.getTeamList().size(); i > 0; i--){
            int subNum = random.nextInt(i);   //go through the whole list in a random order again, to ensure it doesn't just work in order
            Subordinate subToRest = gameState.getTeamList().get(subNum);

            gameState.restSubordinate(subToRest);
            Assert.assertTrue(gameState.getRestingSubordinates().contains(subToRest));
            Assert.assertFalse(gameState.getTeamList().contains(subToRest));
        }

        Assert.assertTrue(gameState.getTeamList().isEmpty());

        for(int i = gameState.getRestingSubordinates().size(); i > 0; i--){
            int subNum = random.nextInt(i);   //go through the whole list in a random order again, to ensure it doesn't just work in order
            Subordinate subToReturn = gameState.getRestingSubordinates().get(subNum);

            gameState.bringBackSubFromRestDay(subToReturn);
            Assert.assertFalse(gameState.getRestingSubordinates().contains(subToReturn));
            Assert.assertTrue(gameState.getTeamList().contains(subToReturn));
        }

        Assert.assertTrue(gameState.getRestingSubordinates().isEmpty());
    }

    @Test
    public void testSendingSubsOnMissionsAndReturning() {
        Assert.assertTrue(gameState.getAwayOnMissionSubordinates().isEmpty());

        Random random = new Random();
        for(int i = gameState.getTeamList().size(); i > 0; i--){
            int subNum = random.nextInt(i);   //go through the whole list in a random order again, to ensure it doesn't just work in order
            Subordinate subToRest = gameState.getTeamList().get(subNum);

            gameState.sendSubordinateOnMission(subToRest);
            Assert.assertTrue(gameState.getAwayOnMissionSubordinates().contains(subToRest));
            Assert.assertFalse(gameState.getTeamList().contains(subToRest));
        }

        Assert.assertTrue(gameState.getTeamList().isEmpty());

        for(int i = gameState.getAwayOnMissionSubordinates().size(); i > 0; i--){
            int subNum = random.nextInt(i);   //go through the whole list in a random order again, to ensure it doesn't just work in order
            Subordinate subToReturn = gameState.getAwayOnMissionSubordinates().get(subNum);

            gameState.subordinateReturnsFromMission(subToReturn);
            Assert.assertFalse(gameState.getAwayOnMissionSubordinates().contains(subToReturn));
            Assert.assertTrue(gameState.getTeamList().contains(subToReturn));
        }

        Assert.assertTrue(gameState.getAwayOnMissionSubordinates().isEmpty());
    }
}
