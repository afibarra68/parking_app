package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.dtos.DUserRoleList;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.UserRoleService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/user_role")
public class UserRoleController {

    private final UserRoleService service;

    @PostMapping()
    public void createUseRole(@Valid @RequestBody DUserRole dUser) throws WbException {
        service.create(dUser);
    }

    @DeleteMapping("/{userRoleId}")
    public void deleteUserRole(@Valid @PathVariable Long userRoleId) throws WbException {
        service.deleteByRoleId(userRoleId);
    }

    @GetMapping("/pageable")
    public Page<DUserRoleList> getUserRolesPageable(
            @RequestParam(required = false) Long userRoleId,
            @RequestParam(required = false) String numberIdentity,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(userRoleId, numberIdentity, role, pageable);
    }

    @GetMapping("/roles")
    public List<String> getRoles() {
        return service.getRoles();
    }

}

