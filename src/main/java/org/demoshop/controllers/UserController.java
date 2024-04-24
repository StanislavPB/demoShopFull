package org.demoshop.controllers;

import lombok.RequiredArgsConstructor;
import org.demoshop.controllers.api.UserApi;
import org.demoshop.dto.UserDto;
import org.demoshop.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UsersService service;

    @Override
    public ResponseEntity<UserDto> getUserById(long userId) {
        return ResponseEntity.ok(service.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserDto> updateUser(Long userId, UserDto userDto) {
        return ResponseEntity.ok(service.editUser(userId, userDto));
    }
}
