package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.DomandaDB;
import org.andreidodu.tbot.dto.DomandaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface DomandaMapper {

	DomandaDB toDomain(DomandaDTO dto);
	DomandaDTO toDTO(DomandaDB domain);
	List<DomandaDTO> toListDTO(List<DomandaDB> dbs);

	@Mapping(target = "id", ignore = true)
	DomandaDTO updateField(@MappingTarget DomandaDTO dto, DomandaDB domain);
}
