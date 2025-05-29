package com.BiologicalMaterialsSystem.controllers;

import java.util.List;

import com.BiologicalMaterialsSystem.enums.Access;
import com.BiologicalMaterialsSystem.model.User;
import jakarta.validation.Valid;
import com.BiologicalMaterialsSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/admin/{userId}/add")
    public ResponseEntity<User> createUserByAdmin(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody User user,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        User adminUser = userService.getUserById(userId);

        if (adminUser.getAccessRights() != Access.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(userService.createUserByAdmin(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/admin/{adminUserId}/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable("adminUserId") Long userId,
            @PathVariable("userId") Long id,
            @Valid @RequestBody User user,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        User adminUser = userService.getUserById(userId);
        if ((adminUser.getAccessRights() == Access.READ_ONLY)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/admin/{adminUserId}/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("adminUserId") Long userId,
            @PathVariable Long id) {

        User adminUser = userService.getUserById(userId);
        if ((adminUser.getAccessRights() != Access.FULL)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
