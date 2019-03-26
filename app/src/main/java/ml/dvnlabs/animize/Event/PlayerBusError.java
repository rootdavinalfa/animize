package ml.dvnlabs.animize.Event;

public class PlayerBusError {
    private final String status;
    public  PlayerBusError(String status1){
        this.status = status1;
    }
    public String geterror(){
        return status;
    }
}
