package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestedEvent;

@Component
@RequiredArgsConstructor
public class RedisMentorshipRequestedEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @Override
    public void publish(MentorshipRequestedEvent event) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), event);
    }
}