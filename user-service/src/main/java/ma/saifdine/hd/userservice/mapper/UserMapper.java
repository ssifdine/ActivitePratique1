package ma.saifdine.hd.userservice.mapper;

import ma.saifdine.hd.userservice.dtos.UserRequestDTO;
import ma.saifdine.hd.userservice.dtos.UserResponseDTO;
import ma.saifdine.hd.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "role", source = "role")
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "role", ignore = true)
    User toEntity(UserRequestDTO dto);

//    @Mapping(target = "role", ignore = true)
    void updateEntityFromDTO(UserRequestDTO dto, @MappingTarget User user);
}
