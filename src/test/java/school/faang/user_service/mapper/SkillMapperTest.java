package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SkillMapperTest {
    @Autowired
    private SkillMapper mapper;

    @Test
    void toEntity() {
        SkillDto skillDto = new SkillDto(1L,"Programming");

        Skill skill = mapper.toEntity(skillDto);

        assertNotNull(skill);
        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    void toDto() {
        Skill skill = new Skill();
        skill.setId(1);
        skill.setTitle("Programming");

        SkillDto skillDto = mapper.toDto(skill);

        assertNotNull(skillDto);
        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }
}