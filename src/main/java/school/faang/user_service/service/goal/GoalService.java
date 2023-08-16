package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filterList;

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        List<GoalDto> dtoList = goalMapper.goalsToDtos(findGoalsByUserId(userId));
        filter(filter, dtoList);
        return dtoList;
    }

    public List<GoalDto> getSubGoalsByUser(long parentId, GoalFilterDto filter) {
        List<GoalDto> dtoList = goalMapper.goalsToDtos(findSubGoalsByParentId(parentId));
        filter(filter, dtoList);
        return dtoList;
    }

    public List<Goal> findGoalsByUserId(long id) {
        if (id < 1) {
            throw new DataValidationException("userId can not be less than 1");
        }
        return goalRepository.findGoalsByUserId(id)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId()))).toList();
    }

    public List<Goal> findSubGoalsByParentId(long id) {
        if (id < 1) {
            throw new DataValidationException("parentId can not be less than 1");
        }
        return goalRepository.findByParent(id)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId()))).toList();
    }

    public void filter(GoalFilterDto filter, List<GoalDto> dtoList) {
        if (filter != null) {
            filterList.stream()
                    .filter((fil) -> fil.isApplicable(filter))
                    .forEach((fil) -> fil.apply(dtoList, filter));
        }
    }

    public void deleteGoal(long id) {
        goalRepository.deleteById(id);
    }

    private Goal getGoalById(long id) {
        return goalRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id: " + id + " not found"));
    }

    public void setGoalStatusOnHold(long id) {
        getGoalById(id).setStatus(GoalStatus.ON_HOLD);
    }
}
