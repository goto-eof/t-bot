package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.QstTemplatesDB;
import org.andreidodu.tbot.dto.QstTemplatesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface QuestionarioTemplateMapper {

	QstTemplatesDB toDomain(QstTemplatesDTO dto);

	@Mapping(source = "domanda.id", target = "codiceDomanda")
	QstTemplatesDTO toDTO(QstTemplatesDB domain);

	List<QstTemplatesDTO> toListDTO(List<QstTemplatesDB> dbs);

	@Mapping(target = "id", ignore = true)
	QstTemplatesDTO updateField(@MappingTarget QstTemplatesDTO dto, QstTemplatesDB domain);
}
