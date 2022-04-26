package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.BotQstStatusDB;
import org.andreidodu.tbot.dto.BotQstStatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BotQstStatusMapper {

	BotQstStatusDB toDomain(BotQstStatusDTO dto);

	@Mappings({ @Mapping(source = "botQst.codiceBot", target = "codiceBot"),
			@Mapping(source = "botQst.codiceTemplate", target = "codiceTemplate"),
			@Mapping(source = "botQst.codiceQuestionario", target = "codiceQuestionario") })
	BotQstStatusDTO toDTO(BotQstStatusDB domain);

	List<BotQstStatusDTO> toListDTO(List<BotQstStatusDB> dbs);

	@Mapping(target = "id", ignore = true)
	BotQstStatusDTO updateField(@MappingTarget BotQstStatusDTO dto, BotQstStatusDB domain);
}
