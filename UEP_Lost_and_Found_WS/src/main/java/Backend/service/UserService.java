
package Backend.service;

import Backend.model.User;
import Backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService() {
        this.userRepository = new UserRepository();
    }


    // Constructor for dependency injection (if needed)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public User updateUser(String username, User updatedUser) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFname(updatedUser.getFname());
            user.setMname(updatedUser.getMname());
            user.setLname(updatedUser.getLname());
            user.setEmail(updatedUser.getEmail());
            user.setUsername(updatedUser.getUsername());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public void deleteUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<User> getPendingAdmins() {
        return userRepository.findByRequestAdminTrue();
    }

    public void approveAdmin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setType("Admin");
            user.setRequestAdmin(false);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
