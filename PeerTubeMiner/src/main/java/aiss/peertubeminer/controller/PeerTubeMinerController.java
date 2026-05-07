package aiss.peertubeminer.controller;

import aiss.peertubeminer.model.Channel;
import aiss.peertubeminer.service.PeerTubeMinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/peertubeminer")
public class PeerTubeMinerController {

    @Autowired
    PeerTubeMinerService service;

    /**
     * Obtiene los datos de un canal desde PeerTube y los envía a VideoMiner.
     * channelId = identificador del canal de PeerTube (ej. "lumberroom" o "lumberroom@peertube.tv").
     *
     * Los errores se traducen a respuestas HTTP en GlobalExceptionHandler.
     */
    @PostMapping("/{channelId}")
    public Channel sendToVideoMiner(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxComments) {
        Channel channel = service.getChannel(channelId, maxVideos, maxComments);
        service.sendToVideoMiner(channel);
        return channel;
    }

    /**
     * Versión de solo lectura: obtiene los datos del canal desde PeerTube sin enviarlos a VideoMiner.
     * Útil para pruebas y para previsualizar los datos mapeados.
     */
    @GetMapping("/{channelId}")
    public Channel previewChannel(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxComments) {
        return service.getChannel(channelId, maxVideos, maxComments);
    }
}
