package com.eddmash.app.authentication.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eddmash.app.authentication.dto.SignUpDto;
import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authentication.service.UserService;
import com.eddmash.app.authlibrary.dto.AuthUser;
import com.eddmash.app.shared.dto.GenericResponse;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    @Getter
    private final UserService userService;

    @PutMapping
    public ResponseEntity<GenericResponse<User>> updateUser(@Valid @RequestBody SignUpDto signUpDto,
            @AuthenticationPrincipal AuthUser authUser) {
        GenericResponse<User> resp = getUserService().updateUser(authUser.getUser().getId(), signUpDto, authUser);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{userID}")
    public ResponseEntity<GenericResponse<User>> getUser(@PathVariable Long userID,
            @AuthenticationPrincipal AuthUser authUser) {
        GenericResponse<User> resp = getUserService().getUser(userID, authUser);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<User>>> fetchAllUsers(@RequestParam(required = false) String name,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "100", required = false) int pageSize) {
        GenericResponse<List<User>> resp = getUserService().fetchAll(name, authUser, pageNo, pageSize);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<GenericResponse<User>> deleteUser(@PathVariable Long userID,
            @RequestParam(required = false, defaultValue = "0") int active,
            @AuthenticationPrincipal AuthUser authUser) {
        GenericResponse<User> resp = getUserService().changeUserStatus(userID, active, authUser);
        return ResponseEntity.ok(resp);
    }

}
