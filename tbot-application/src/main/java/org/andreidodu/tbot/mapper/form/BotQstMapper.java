package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.BotQstDB;
import org.andreidodu.tbot.db.BotQstStatusDB;
import org.andreidodu.tbot.dto.BotQstDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface BotQstMapper {

	BotQstStatusDB toDomain(BotQstDTO dto);
	BotQstDTO toDTO(BotQstDB domain);

	List<BotQstDTO> toListDTO(List<BotQstDB> dbs);

	@Mapping(target = "id", ignore = true)
	BotQstDTO updateField(@MappingTarget BotQstDTO dto, BotQstDB domain);
}
