package com.domain.backend.dto.request;

import com.domain.backend.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRolesRequest {
    private Set<Role> roles;
}
