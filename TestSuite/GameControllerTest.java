import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameControllerTest {
    private DisplayController display = new DisplayController();
    private GameController gameController;

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
        gameController = new GameController();
        Platform.runLater(() -> display.setWindow(new Stage()));    //has to be on javaFX Application Thread
        display.setGameController(gameController);
    }

    @Test
    public void testSendQuitGameMessage(){
        Platform.runLater(() -> {   //since the stage was set in runLater, we have to run this test in runLater
            DisplayController.getWindow().show();
            Assert.assertTrue(DisplayController.getWindow().isShowing());   //assert it's showing
            gameController.sendMissionResultQuitGameMessage();              //send message
            Assert.assertFalse(DisplayController.getWindow().isShowing());  //assert it's not showing any more
        });
    }

    @Test
    public void testNewGameSetsUpGameCorrectly(){
        Platform.runLater(() -> {
            gameController.newGame();
            Assert.assertEquals(3, gameController.getCurrentState().getTeamList().size());
            Assert.assertEquals(10, gameController.getCurrentState().getMissionList().size());
            Assert.assertEquals(19, gameController.getCurrentState().getPossibleSubordinates().size());
            Assert.assertEquals(0, gameController.getCurrentState().getCurrentDay());
            Assert.assertEquals(0, gameController.getCurrentState().getPlayerScore());
            Assert.assertEquals(0, gameController.getCurrentState().getRestingSubordinates().size());
            Assert.assertEquals(0, gameController.getCurrentState().getAwayOnMissionSubordinates().size());
        });
    }
}
