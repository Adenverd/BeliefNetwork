package exceptions;

public class BeliefNetworkException extends RuntimeException{
    public BeliefNetworkException(){
        super();
    }

    public BeliefNetworkException(String message){
        super(message);
    }
}
