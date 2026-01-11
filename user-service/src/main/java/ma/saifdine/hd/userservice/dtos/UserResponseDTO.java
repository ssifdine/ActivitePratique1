package ma.saifdine.hd.userservice.dtos;

import lombok.*;
import ma.saifdine.hd.userservice.enums.UserRole;
import ma.saifdine.hd.userservice.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String email;
    private UUID userId;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
