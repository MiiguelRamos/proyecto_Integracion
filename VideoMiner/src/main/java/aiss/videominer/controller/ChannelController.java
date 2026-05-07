package aiss.videominer.controller;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    @GetMapping("/channels")
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @GetMapping("/channels/{id}")
    public Channel findOne(@PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        if (channel.isEmpty()) {
            throw new ChannelNotFoundException();
        }
        return channel.get();
    }

    @PostMapping("/channels")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel create(@Valid @RequestBody Channel channel) {
        return channelRepository.save(channel);
    }

    @PutMapping("/channels/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String id, @Valid @RequestBody Channel updatedChannel)
            throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        if (channel.isEmpty()) {
            throw new ChannelNotFoundException();
        }
        Channel existing = channel.get();
        existing.setName(updatedChannel.getName());
        existing.setDescription(updatedChannel.getDescription());
        existing.setCreatedTime(updatedChannel.getCreatedTime());
        existing.setVideos(updatedChannel.getVideos());
        channelRepository.save(existing);
    }

    @DeleteMapping("/channels/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) throws ChannelNotFoundException {
        if (!channelRepository.existsById(id)) {
            throw new ChannelNotFoundException();
        }
        channelRepository.deleteById(id);
    }
}
