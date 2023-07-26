package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtos = recommendationDto.getSkillOffers();
        validate(skillOfferDtos);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        User author = userRepository.findById(recommendationDto.getAuthorId()).orElseThrow();
        User receiver = userRepository.findById(recommendationDto.getReceiverId()).orElseThrow();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent(recommendationDto.getContent());
        recommendation.setCreatedAt(recommendationDto.getCreatedAt());
        List<Skill> offeredSkills = skillRepository.findAllById(skillOfferDtos.stream().map(SkillOfferDto::getSkillId).collect(Collectors.toList()));
        addGuarantee(recommendation, offeredSkills);
        recommendation.setSkillOffers(offeredSkills.stream().map(skill -> SkillOffer.builder().skill(skill).build()).collect(Collectors.toList()));
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    public Page<RecommendationDto> getAllUserRecommendations(Long receiverId, Pageable pageable) {
        Page<Recommendation> page = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return page.map(recommendationMapper::toDto);
    }

    private void addGuarantee(Recommendation recommendation, List<Skill> offeredSkills) {
        List<Skill> receiverSkills = recommendation.getReceiver().getSkills();
        List<UserSkillGuarantee> userSkillGuarantees = new ArrayList<>();
        offeredSkills.forEach(offeredSkill -> {
                    receiverSkills.forEach(receiverSkill -> {
                        boolean userSkillContainGuarantor = !receiverSkill.getGuarantees()
                                .stream()
                                .map(UserSkillGuarantee::getGuarantor)
                                .toList()
                                .contains(recommendation.getAuthor());
                        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                .skill(offeredSkill)
                                .guarantor(recommendation.getAuthor())
                                .user(recommendation.getReceiver())
                                .build();
                        if (receiverSkills.contains(offeredSkill)) {
                            if (!userSkillContainGuarantor) {
                                userSkillGuarantees.add(userSkillGuarantee);
                            }
                        }
                    });
                }
        );
        userSkillGuaranteeRepository.saveAll(userSkillGuarantees);
    }

    private void validate(List<SkillOfferDto> skills) {
        if (skills != null && !skills.isEmpty()) {
            List<Long> skillIds = skills.stream()
                    .map(SkillOfferDto::getSkillId)
                    .collect(Collectors.toList());
            if (!skillRepository.existsAllById(skillIds)) {
                throw new DataValidationException("list of skills contains not valid skills, please, check this");
            }
        }
    }
}
