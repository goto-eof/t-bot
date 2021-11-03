package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.QuestionarioDB;
import org.andreidodu.tbot.dto.QuestionarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring", uses = { DomandaMapper.class })
public interface QuestionarioMapper {

	QuestionarioDB toDomain(QuestionarioDTO dto);
	QuestionarioDTO toDTO(QuestionarioDB domain);
	List<QuestionarioDTO> toListDTO(List<QuestionarioDB> dbs);

	@Mapping(target = "id", ignore = true)
	QuestionarioDTO updateField(@MappingTarget QuestionarioDTO dto, QuestionarioDB domain);
}
