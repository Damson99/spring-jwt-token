package com.security.jwt.service;

import com.security.jwt.domain.Role;
import com.security.jwt.repository.RoleRepository;
import com.security.jwt.utils.ObjectOperations;
import com.security.web.exceptions.InvalidCredentialsException;
import com.security.web.exceptions.RoleAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl extends ObjectOperations implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role saveRole(Role role) {
        throwExceptionIfObjectEmpty(role, "invalid role: ");
        Optional<Role> roleExists = roleRepository.findByName(role.getName());
        if(roleExists.isPresent()){
            throw new RoleAlreadyExistsException("role "+role.getName()+" already exists");
        }
        return roleRepository.save(role);
    }
}
