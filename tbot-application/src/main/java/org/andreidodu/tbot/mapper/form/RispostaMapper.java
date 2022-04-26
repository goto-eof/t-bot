package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.RispostaDB;
import org.andreidodu.tbot.dto.RispostaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RispostaMapper {

	RispostaDB toDomain(RispostaDTO dto);
	@Mapping(source = "domanda.id", target = "codiceDomanda")
	RispostaDTO toDTO(RispostaDB domain);
	List<RispostaDTO> toListDTO(List<RispostaDB> dbs);

	@Mapping(target = "id", ignore = true)
	RispostaDTO updateField(@MappingTarget RispostaDTO dto, RispostaDB domain);
}
