package aiss.peertubeminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


 // Se lanza cuando el canal solicitado no existe en PeerTube.

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Channel not found")
public class ChannelNotFoundException extends RuntimeException {
    public ChannelNotFoundException() { super("Channel not found");
    }
}
