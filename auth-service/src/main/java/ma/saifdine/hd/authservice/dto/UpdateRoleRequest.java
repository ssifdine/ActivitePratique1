package ma.saifdine.hd.authservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.saifdine.hd.authservice.enums.Role;

@Data
public class UpdateRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}
