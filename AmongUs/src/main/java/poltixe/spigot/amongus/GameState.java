package poltixe.spigot.amongus;

public class GameState {
    public Boolean gameStarted;
    public PlayerState[] imposters = new PlayerState[2];
    public Boolean inMeeting;
    public Boolean isVotingTime;
    public Boolean gameEnded;

    GameState(PlayerState imposter) {
        this(imposter, null);
    }

    GameState(PlayerState imposter1, PlayerState imposter2) {
        this.gameStarted = false;
        this.imposters[0] = imposter1;
        this.imposters[1] = imposter2;

        this.inMeeting = false;
        this.isVotingTime = false;
        this.gameEnded = false;
    }
}