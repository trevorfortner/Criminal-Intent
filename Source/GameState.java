import java.util.ArrayList;

//this class keeps track of everything that will be recognizable as part of progress in the game
//i.e. current team list, possible missions, day number, score
class GameState {
    private int currentDay;
    private int playerScore;
    private ArrayList<Mission> missionList;
    private ArrayList<Subordinate> teamList;
    private ArrayList<Subordinate> possibleSubordinates;
    private ArrayList<Subordinate> awayOnMissionSubordinates;
    private ArrayList<Subordinate> restingSubordinates;

    public GameState(int currDay, int score, ArrayList<Subordinate> team, ArrayList<Mission> missions, ArrayList<Subordinate> possSubs){
        currentDay = currDay;
        playerScore = score;
        teamList = team;
        missionList = missions;
        possibleSubordinates = possSubs;
        restingSubordinates = new ArrayList<>();
        awayOnMissionSubordinates = new ArrayList<>();
    }

    int getCurrentDay() {
        return currentDay;
    }
    int getPlayerScore() {
        return playerScore;
    }
    ArrayList<Mission> getMissionList() {
        return missionList;
    }
    ArrayList<Subordinate> getRestingSubordinates() {
        return restingSubordinates;
    }
    ArrayList<Subordinate> getAwayOnMissionSubordinates() {
        return awayOnMissionSubordinates;
    }

    //Both of these ArrayList<Subordinate> functions return the appropriate list in order, determined by Subordinate::compareTo()
    ArrayList<Subordinate> getTeamList() {
        teamList.sort(Subordinate::compareTo);  //use custom compareTo function in Subordinate to sort the list
        return teamList;                        //sorts by decreasing strength, if strength equal then alphabetical order
    }
    ArrayList<Subordinate> getPossibleSubordinates() {
        possibleSubordinates.sort(Subordinate::compareTo);  //use custom compareTo function in Subordinate to sort the list
        return possibleSubordinates;                //sorts by decreasing strength, if strength equal then alphabetical order
    }

    void increaseScore(int amount){
        playerScore += amount;
    }
    void decreaseScore(int amount){
        if(playerScore < amount){
            playerScore = 0;    //don't let it go negative
        }
        else{
            playerScore -= amount;
        }
    }

    void sendSubordinateOnMission(Subordinate sub){
        teamList.remove(sub);
        awayOnMissionSubordinates.add(sub);
    }

    //adds the subordinates back to the team list
    void subordinateReturnsFromMission(Subordinate sub){
        if(!teamList.contains(sub)) {
            teamList.add(sub);
        }
        awayOnMissionSubordinates.remove(sub);
    }

    void restSubordinate(Subordinate sub){
        teamList.remove(sub);
        restingSubordinates.add(sub);
    }

    void bringBackSubFromRestDay(Subordinate sub){
        teamList.add(sub);
        restingSubordinates.remove(sub);
    }

    void scaleUpMissionsAndDay(){
        currentDay += 1;
        missionList.forEach( Mission::scaleForNewDay );    //for each mission, call Mission's scaleForNewDay function to increase difficulty and value by 50
    }

    void scaleDownMissionsAndDay(){     //only to be used after the practice/post-tutorial level
        currentDay -= 1;                //so the user who uses the tutorial isn't at a disadvantage to one who skips it
        missionList.forEach( Mission::scaleDownDifficulty );
    }

    void addNewSubFromPossibleList(Subordinate possSub){
        teamList.add(possSub);
        possibleSubordinates.remove(possSub);
    }
}
