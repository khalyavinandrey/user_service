package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
  @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "mapListToId")
  EventDto toDto(Event event);

  @Mapping(target = "relatedSkills", ignore = true)
  Event toEntity(EventDto eventDto);

  @Named("mapListToId")
  default List<Long> mapListToId(List<Skill> objects) {
    return objects.stream()
        .map(Skill::getId)
        .collect(Collectors.toList());
  }
}