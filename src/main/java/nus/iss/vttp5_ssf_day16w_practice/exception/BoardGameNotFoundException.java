package nus.iss.vttp5_ssf_day16w_practice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// automatically returns a 404 status
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BoardGameNotFoundException extends RuntimeException {

    // constructor accepts GID to be displayed in the message 
    public BoardGameNotFoundException(String GID) {
        super("Board game with redis key: " + GID + " not found :((");
    }
    
}
