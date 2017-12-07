import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class DisplayControllerTest {
    private DisplayController display;
    private AnchorPane layout;
    private GameState gameState;

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
    public void setup(){
        layout = new AnchorPane();
        display = new DisplayController();

        ArrayList<Subordinate> starterSubs = new ArrayList<>();
        starterSubs.add(new Subordinate("", new ImageView(), 0, 0, new String[0], 0));

        ArrayList<Mission> missions = new ArrayList<>();
        ArrayList<Subordinate> possibleSubs = new ArrayList<>();

        gameState = new GameState(0, 0, starterSubs, missions, possibleSubs);
        display.setGameState(gameState);
        Platform.runLater(() -> display.setWindow(new Stage())); //have to do this because it operates a Stage
        //this function gets us in the JavaFX Application Thread, which is the only one allowed to modify visuals
    }

    @Test
    public void testUpdateSubordinateImages(){
        display.updateSubordinateImages(layout);
        //should add 5 things for the one sub (stamina bar border, current stamina, sub pic, label background rectangle, info label)
        Assert.assertEquals(5, layout.getChildren().size());

        gameState.getTeamList().add(new Subordinate("", new ImageView(), 0, 0, new String[0], 0));
        display.updateSubordinateImages(layout);    //should now do 10 things (5 for each sub)
        Assert.assertEquals(10, layout.getChildren().size());

        gameState.sendSubordinateOnMission(gameState.getTeamList().get(0));
        //can't just remove because it uses the list of subordinates away on mission to tell who needs to be removed
        display.updateSubordinateImages(layout);    //back down to 1 sub, should now have 5 again
        Assert.assertEquals(5, layout.getChildren().size());
    }

    @Test
    public void testResetDayVisuals(){
        display.resetDayVisuals(layout);    //should add background image + 5 things for the one sub
        Assert.assertEquals(6, layout.getChildren().size());
    }

    @Test
    public void testAddSubAndDoNextDay(){
        Assert.assertEquals(1, gameState.getTeamList().size());
        Subordinate newSub = new Subordinate("", new ImageView(), 0, 0, new String[0], 1500);   //1500 cost

        Platform.runLater(() -> {   //have to do this because handleAddingNewSubAndDoNextDay() will create and display a Stage
            display.handleAddingNewSubAndDoNextDay(newSub);     //this will make a window pop up, but it'll close when all the tests finish
            Assert.assertEquals(1, gameState.getTeamList().size()); //shouldn't change because the gameState doesn't have enough to buy the new sub

            gameState.increaseScore(5000000);
            display.handleAddingNewSubAndDoNextDay(newSub);
            Assert.assertEquals(5000000-1500, gameState.getPlayerScore());
            Assert.assertTrue(gameState.getTeamList().contains(newSub));
        });
    }

    @Test
    public void testDisplayDayTransition(){
        Platform.runLater(() -> {
            display.displayDayTransition();    //should add day transition image + 2 labels + 2 buttons
            Assert.assertEquals(5, display.getLayout().getChildren().size());
        });
    }
}
