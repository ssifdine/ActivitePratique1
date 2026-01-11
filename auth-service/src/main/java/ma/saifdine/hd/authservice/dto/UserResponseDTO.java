package ma.saifdine.hd.authservice.dto;

import lombok.*;
import ma.saifdine.hd.authservice.enums.Role;
import ma.saifdine.hd.authservice.enums.UserStatus;

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
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
