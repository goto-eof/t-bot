package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.DominioDB;
import org.andreidodu.tbot.dto.DominioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DominioMapper {

	DominioDB toDomain(DominioDTO dto);
	DominioDTO toDTO(DominioDB domain);
	List<DominioDTO> toListDTO(List<DominioDB> dbs);

	@Mappings({ @Mapping(target = "codiceDominio", ignore = true), @Mapping(target = "valoreDominio", ignore = true), })
	DominioDTO updateField(@MappingTarget DominioDTO dto, DominioDB domain);
}
