package my.fraud.demo.model;

public class WatchlistException extends Exception {
    public String errorMessage;

    public WatchlistException(String errorMessage) {
        super(errorMessage);
    }
}
