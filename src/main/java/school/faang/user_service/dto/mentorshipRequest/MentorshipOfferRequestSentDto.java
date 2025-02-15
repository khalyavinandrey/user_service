package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class MentorshipOfferRequestSentDto {
    @NotNull
    private Long id;

    @NotNull
    private Long requesterId;

    @NotNull
    private Long receiverId;
}

