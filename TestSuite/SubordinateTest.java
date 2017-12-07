import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SubordinateTest{
    private Subordinate testSub;

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
        String[] subExcuses = {"My dog is sick.", "My neighbors had a fire.", "I have bone spurs.", "I accidentally sold my car.", "I'm too hungover."};

        testSub = new Subordinate("George", new ImageView(new Image("file:Images/possSub1.png")), 100, 5, subExcuses, 0);
    }

    @Test
    public void testSubImageViewScalesBasedOnWindowHeight() {
        ImageView imageView500 = testSub.getSubImageView(500);
        double imageHeight500 = imageView500.getFitHeight();
        ImageView imageView250 = testSub.getSubImageView(250);
        double imageHeight250 = imageView250.getFitHeight();

        //getSubImageView directly changes the ImageView saved in Subordinate, so calling these two back-to-back only leaves the last value

        Assert.assertTrue(imageHeight500 > imageHeight250);
    }

    @Test
    public void testCompareTo() {
        //this.compareTo(that) returns 0 if equal
        //positive number if "this" should be AFTER the "that"
        //negative number if "this" should be BEFORE the "that"

        //also note - subordinate names should never be the same
        //testSub = new Subordinate("George", new ImageView(new Image("file:Images/possSub1.png")), 100, 5, subExcuses, 0);

        Subordinate sameStrengthAlphabeticallyFirstSub = new Subordinate("A-A-Ron", new ImageView(), 100, 5, new String[0], 0);
        Subordinate sameStrengthAlphabeticallyAfterSub = new Subordinate("Zach", new ImageView(), 100, 5, new String[0], 0);
        Subordinate lowerStrengthSub = new Subordinate("", new ImageView(), 70, 5, new String[0], 0);
        Subordinate higherStrengthSub = new Subordinate("", new ImageView(), 130, 5, new String[0], 0);

        Assert.assertTrue(testSub.compareTo(higherStrengthSub) > 0);   //should be positive because higher strength goes before
        Assert.assertTrue(testSub.compareTo(sameStrengthAlphabeticallyFirstSub) > 0);   //should be positive because alphabeticallyFirst goes before

        Assert.assertTrue(testSub.compareTo(lowerStrengthSub) < 0); //should be negative because test sub has higher strength, so should be first
        Assert.assertTrue(testSub.compareTo(sameStrengthAlphabeticallyAfterSub) < 0);   //should be negative because test sub is first alphabetically
    }

    @Test
    public void testStaminaStaysBetweenZeroAndFive() {
        for(int i = 0; i <= 50; i++){
            testSub.returnStamina();
        }
        Assert.assertEquals(5, testSub.getStamina());

        for(int i = 0; i <= 50; i++){
            testSub.reduceStamina();
        }
        Assert.assertEquals(0, testSub.getStamina(), 0);
    }
}
