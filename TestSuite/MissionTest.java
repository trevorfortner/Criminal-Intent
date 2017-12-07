import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class MissionTest {
    private Mission testMission;

    @BeforeClass
    public static void setUpClass() throws InterruptedException {
        //need to initialize JavaFX first, otherwise it won't let us use Images and stuff in these tests

        Thread jfxInitThread = new Thread("jfxInitThread") {
            //needs to be put in a thread otherwise an exception is thrown saying it can only be done in an event thread, not main
            public void run() {
                try {   //if just running this test by itself
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
        testMission = new Mission("bank robbery", 500, 1000, 7, 3.7, new ImageView(new Image("file:Images/BankRobbery.jpg")));
    }

    @Test
    public void testMissionVisualScalesWidthBasedOnWindowHeight() {
        ImageView imageView500 = testMission.getMissionVisual(500);
        double imageHeight500 = imageView500.getFitWidth();
        ImageView imageView250 = testMission.getMissionVisual(250);
        double imageHeight250 = imageView250.getFitWidth();

        //getMissionVisual directly changes the ImageView saved in Mission, so calling these two back-to-back only leaves the last value

        Assert.assertTrue(imageHeight500 > imageHeight250);
    }

    @Test
    public void testMissionMarkerScalesHeightBasedOnWindowHeight() {
        ImageView imageView500 = testMission.getMissionMarker(500);
        double imageHeight500 = imageView500.getFitHeight();
        ImageView imageView250 = testMission.getMissionMarker(250);
        double imageHeight250 = imageView250.getFitHeight();

        //getMissionMarker directly changes the ImageView saved in Mission, so calling these two back-to-back only leaves the last value

        Assert.assertTrue(imageHeight500 > imageHeight250);
    }

    @Test
    public void testMissionPassesWhenSubStrengthTotalGreaterThanDifficulty() {
        ArrayList<Subordinate> subordinates = makeListOfSubordinatesWithStrengthTotalEqualToMissionDifficulty();
        subordinates.get(0).increaseStrength(); //increases strength 10

        testMission.setAssignedSubordinates(subordinates);

        Pair<Boolean, Boolean> missionPassFail = testMission.runMission();
        Assert.assertTrue(missionPassFail.getKey());
    }

    @Test
    public void testMissionPassesWhenSubStrengthTotalEqualToDifficulty() {
        ArrayList<Subordinate> subordinates = makeListOfSubordinatesWithStrengthTotalEqualToMissionDifficulty();

        testMission.setAssignedSubordinates(subordinates);

        Pair<Boolean, Boolean> missionPassFail = testMission.runMission();
        Assert.assertTrue(missionPassFail.getKey());
    }

    @Test
    public void testMissionFailsWithoutKillingWhenSubStrengthTotalLessThan100UnderDifficulty() {
        ArrayList<Subordinate> subordinates = makeListOfSubordinatesWithStrengthTotalEqualToMissionDifficulty();
        subordinates.get(0).decreaseStrength(); //decreases it by 10

        testMission.setAssignedSubordinates(subordinates);

        Pair<Boolean, Boolean> missionPassFail = testMission.runMission();
        Assert.assertFalse(missionPassFail.getKey());
        Assert.assertTrue(missionPassFail.getValue());
    }

    @Test
    public void testMissionFailsAndKillsWhenSubStrengthTotalMoreThan100UnderDifficulty() {
        ArrayList<Subordinate> subordinates = makeListOfSubordinatesWithStrengthTotalEqualToMissionDifficulty();
        for(int i = 0; i<=10; i++){
            subordinates.get(0).decreaseStrength(); //decreases 10 each time, doing it 11 times
        }

        testMission.setAssignedSubordinates(subordinates);

        Pair<Boolean, Boolean> missionPassFail = testMission.runMission();
        Assert.assertFalse(missionPassFail.getKey());
        Assert.assertFalse(missionPassFail.getValue());
    }

    @Test
    public void testMissionReducesStrengthsBasedOnStamina() {
        ArrayList<Subordinate> subordinates = makeListOfSubordinatesWithStrengthTotalEqualToMissionDifficulty();
        subordinates.get(0).reduceStamina();
        //reduces that sub's stamina by 1, which will deduct 5% from that sub when calculating the total

        testMission.setAssignedSubordinates(subordinates);

        Pair<Boolean, Boolean> missionPassFail = testMission.runMission();
        Assert.assertFalse(missionPassFail.getKey());
        Assert.assertTrue(missionPassFail.getValue());
    }




    private ArrayList<Subordinate> makeListOfSubordinatesWithStrengthTotalEqualToMissionDifficulty() {
        ArrayList<Subordinate> subordinates = new ArrayList<>();
        //first int = strength, second = stamina, rest don't apply to mission
        subordinates.add(new Subordinate("", new ImageView(), 125, 5, new String[0], 0));
        subordinates.add(new Subordinate("", new ImageView(), 50, 5, new String[0], 0));
        subordinates.add(new Subordinate("", new ImageView(), 175, 5, new String[0], 0));
        subordinates.add(new Subordinate("", new ImageView(), 150, 5, new String[0], 0));
        //strength total = 500, matches mission's difficulty, stamina are all 5 so don't affect strengths

        return subordinates;
    }
}
