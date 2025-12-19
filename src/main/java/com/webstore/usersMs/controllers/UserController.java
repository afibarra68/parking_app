package com.webstore.usersMs.controllers;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserList;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.error.WbException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/users")
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
public class UserController {

    private final UserService service;

    @Operation(summary = "Crear usuario público", description = "Crea un nuevo usuario público (sin autenticación requerida)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = DUserCreated.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/create_public_user")
    public DUserCreated createPublicUser(@Valid @RequestBody DUser dUser) throws WbException {
        return service.create(dUser);
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = DUserCreated.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("")
    public DUserCreated createUser(@Valid @RequestBody DUser dUser) throws WbException {
        return service.create(dUser);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza la información de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = DUserCreated.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping("")
    public DUserCreated updateUser(@Valid @RequestBody DUser dUser) throws WbException {
        return service.update(dUser);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por número de documento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente",
                    content = @Content(schema = @Schema(implementation = DUserCreated.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("/down_public_user")
    public DUserCreated deleteUser(
            @Parameter(description = "Número de documento del usuario", required = true) @RequestParam(required = true) Long userDocument) throws WbException {
        return service.deleteByDocument(userDocument);
    }

    @Operation(summary = "Listar usuarios paginados", description = "Obtiene una lista paginada de usuarios con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/pageable")
    public Page<DUserList> getUsersPageable(
            @Parameter(description = "ID del usuario") @RequestParam(required = false) Long appUserId,
            @Parameter(description = "Número de identidad") @RequestParam(required = false) String numberIdentity,
            @Parameter(description = "ID de la empresa") @RequestParam(required = false) Long companyCompanyId,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(appUserId, numberIdentity, companyCompanyId, pageable);
    }

}
