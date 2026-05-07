package aiss.dailymotionminer.controller;

import aiss.dailymotionminer.model.Channel;
import aiss.dailymotionminer.service.DailymotionMinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dailymotionminer")
public class DailymotionMinerController {

    @Autowired
    DailymotionMinerService service;


    // Obtiene los datos de un canal desde Dailymotion y los envía a VideoMiner.
    //  channelId = identificador de usuario de Dailymotion (ej. "euronews").

    // Los errores se traducen a respuestas HTTP en GlobalExceptionHandler.

    @PostMapping("/{channelId}")
    public Channel sendToVideoMiner(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxPages) {
        Channel channel = service.getChannel(channelId, maxVideos, maxPages);
        service.sendToVideoMiner(channel);
        return channel;
    }


        // Versión de solo lectura: obtiene los datos del canal desde Dailymotion sin enviarlos a VideoMiner.
    // Útil para pruebas y para previsualizar los datos mapeados.

    @GetMapping("/{channelId}")
    public Channel previewChannel(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxPages) {
        return service.getChannel(channelId, maxVideos, maxPages);
    }
}
