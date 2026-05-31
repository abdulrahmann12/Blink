package com.example.Blink.role.service;

import com.example.Blink.exception.RoleAlreadyExistsException;
import com.example.Blink.exception.RoleNotFoundException;
import com.example.Blink.role.dto.CreateRoleRequest;
import com.example.Blink.role.dto.RoleResponse;
import com.example.Blink.role.entity.Role;
import com.example.Blink.role.mapper.RoleMapper;
import com.example.Blink.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse createRole(@Valid CreateRoleRequest request){
        if(roleRepository.findByRoleName(request.getRoleName()).isPresent()){
            throw new RoleAlreadyExistsException();
        }

        Role role = roleMapper.toEntity(request);
        role.setRoleName(request.getRoleName().trim().toUpperCase());
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    @Transactional
    public RoleResponse updateRole(Long roleId, @Valid CreateRoleRequest request){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(RoleNotFoundException::new);

        if(roleRepository.findByRoleName(request.getRoleName()).isPresent() && !role.getRoleName().equals(request.getRoleName().trim().toUpperCase())){
            throw new RoleAlreadyExistsException();
        }

        role.setRoleName(request.getRoleName().trim().toUpperCase());
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    public Page<RoleResponse> getAllRoles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roleRepository.findAll(pageable)
                .map(roleMapper::toResponse);
    }

    public RoleResponse getRoleById(Long roleId){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(RoleNotFoundException::new);
        return roleMapper.toResponse(role);
    }

    public RoleResponse getRoleByName(String roleName){
        Role role = roleRepository.findByRoleName(roleName.trim().toUpperCase())
                .orElseThrow(RoleNotFoundException::new);
        return roleMapper.toResponse(role);
    }

    public void deleteRole(Long roleId){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(RoleNotFoundException::new);
        roleRepository.delete(role);
    }
}
