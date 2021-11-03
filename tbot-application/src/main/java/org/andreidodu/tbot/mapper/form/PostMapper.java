package org.andreidodu.tbot.mapper.form;

import java.util.List;

import org.andreidodu.tbot.db.PostDB;
import org.andreidodu.tbot.dto.PostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface PostMapper {

	PostDB toDomain(PostDTO dto);
	PostDTO toDTO(PostDB domain);
	List<PostDTO> toListDTO(List<PostDB> dbs);

	@Mapping(target = "id", ignore = true)
	PostDTO updateField(@MappingTarget PostDTO dto, PostDB domain);
}
