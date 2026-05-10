package aiss.peertubeminer.controller;

import aiss.peertubeminer.model.videominer.VMChannel;
import aiss.peertubeminer.service.PeerTubeMinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/peertubeminer")
public class PeerTubeMinerController {

    @Autowired
    PeerTubeMinerService service;

    /*
      Obtiene los datos de un canal desde PeerTube y los envía a VideoMiner
      donde channelId es el identificador del canal de PeerTube

      Devuelve el VMChannel ya transformado al formato de VideoMiner.
      Los errores se traducen a respuestas HTTP en GlobalExceptionHandler.
     */
    @PostMapping("/{channelId}")
    public VMChannel sendToVideoMiner(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxComments) {
        VMChannel channel = service.getChannel(channelId, maxVideos, maxComments);
        service.sendToVideoMiner(channel);
        return channel;
    }


    // Versión de solo lectura: obtiene los datos del canal desde PeerTube sin enviarlos a VideoMiner (para pruebas)

    @GetMapping("/{channelId}")
    public VMChannel previewChannel(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxComments) {
        return service.getChannel(channelId, maxVideos, maxComments);
    }
}
