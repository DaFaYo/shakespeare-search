package nl.demo.shakespeare.mapper;

import nl.demo.shakespeare.model.Play;
import org.mapstruct.*;
import nl.demo.shakespeare.dto.*;

@Mapper(componentModel = "spring")
public interface PlayMapper {

    PlayDto toDto(Play play);

    Play toEntity(PlayCreateDto dto);

    void updateEntityFromDto(PlayUpdateDto dto, @MappingTarget Play play);
}