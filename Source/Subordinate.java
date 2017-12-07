import javafx.scene.image.ImageView;

//this class manages everything having to do with Subordinates, your lackies to order around
public class Subordinate {
    private String name;
    private ImageView subImageView;
    private int strength;
    private int stamina;
    private String[] excuses;
    private int cost;
    private int denialTracker;

    public Subordinate(String name, ImageView image, int strength, int stamina, String[] excuses, int cost){
        this.name = name;
        this.subImageView = image;
        this.strength = strength;
        this.stamina = stamina;
        this.excuses = excuses;
        this.cost = cost;
        denialTracker = 0;
    }

    String getName() {
        return name;
    }
    int getStrength() {
        return strength;
    }
    int getCost() {
        return cost;
    }
    int getStamina() {
        return stamina;
    }
    int getDenialTracker() {
        return denialTracker;
    }
    String[] getExcuses() {
        return excuses;
    }

    void returnStamina(){
        if(stamina < 5){
            stamina++;
        }
    }

    void reduceStamina(){
        if(stamina > 0) {
            stamina--;
        }
    }

    //used in GameState when giving the list of subordinates in order
    //outputs which sub to put first, based upon strength and, if strengths equal, alphabetical order of name
    int compareTo(Subordinate sub2){
        //this.compareTo(that) returns 0 if equal
        //positive number if "this" should be AFTER the "that"
        //negative number if "this" should be BEFORE the "that"

        if(this.getStrength() > sub2.getStrength()){
            return -1;      //put "this" first
        }
        else if(this.getStrength() == sub2.getStrength()){  //if their strengths are equal
            if(this.getName().compareTo(sub2.getName()) < 0){   //if this sub's name is first alphabetically
                return -1;                                      //put "this" first
            }
            else{
                return 1;       //put "this" second
            }
        }
        else{   //if this one is weaker than comparing to
            return 1;       //put "this" second
        }
    }

    //returns an ImageView that scales on window size for other classes to place in their visuals
    ImageView getSubImageView(double windowHeight) {
        subImageView.setFitHeight(windowHeight/10);     //change size here if desired so it'll still scale with window size
        subImageView.setPreserveRatio(true);
        subImageView.setSmooth(true);
        subImageView.setCache(true);
        return subImageView;
    }

    //called upon successfully completed mission
    void increaseStrength(){
        strength += 10;
    }

    //called in MissionTest
    void decreaseStrength(){
        strength -= 10;
    }

    void incremementDenialTracker() {
        denialTracker++;
    }

    void resetDenialTracker() {
        denialTracker = 0;
    }
}
