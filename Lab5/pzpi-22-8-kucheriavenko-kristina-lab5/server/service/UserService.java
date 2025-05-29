package com.BiologicalMaterialsSystem.service;

import com.BiologicalMaterialsSystem.enums.Access;
import com.BiologicalMaterialsSystem.model.User;
import com.BiologicalMaterialsSystem.repository.UserRepository;
import com.BiologicalMaterialsSystem.config.PasswordEncryptionService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        try{
            user.setPassword(PasswordEncryptionService.encrypt(user.getPassword()));
            user.setAccessRights(Access.READ_ONLY);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userRepository.save(user);
    }
    public User createUserByAdmin(User user) {
        try{
            user.setPassword(PasswordEncryptionService.encrypt(user.getPassword()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setRole(userDetails.getRole());
        user.setAccessRights(userDetails.getAccessRights());
        user.setLogin(userDetails.getLogin());
        user.setPassword(userDetails.getPassword());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public boolean availabilityOfAction(Long id){
        User user = getUserById(id);
        return user.getAccessRights() == Access.FULL;
    }

}
