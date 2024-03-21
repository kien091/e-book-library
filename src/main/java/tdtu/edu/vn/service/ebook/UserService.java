package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import tdtu.edu.vn.model.User;
import tdtu.edu.vn.repository.UserRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    // CRUD
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id){
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(User updatedUser) {
        if(userRepository.existsById(updatedUser.getId())){
            return userRepository.save(updatedUser);
        }
        return null;
    }

    public boolean deleteUser(String userId) {
        if(userRepository.existsById(userId)){
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }





    // Other methods
    public void register(String username, String email, String password, String confirmPassword){
        User user = new User(username, email, password, confirmPassword);

        user.setPassword(passwordEncoder.encode(password));
        user.setConfirmPassword(passwordEncoder.encode(password));
        user.setPosition("");
        user.setPhone("");
        user.setFullname("");
        user.setAddress("");
        user.setSex("");
        user.setBirthday("");
        user.setCreateday(new Date());
        user.setSubscribe("");
        user.setAvatar(new byte[0]);
        user.setRole("ROLE_USER");
        user.setCreateday(new Date());

        System.out.println("Register: " + user.getUsername() + " " + user.getEmail() + " " + user.getPassword() + " " + user.getConfirmPassword());

        userRepository.save(user);
    }

    public boolean checkLogin(User user) {
        User userDb = findByEmail(user.getEmail());

        if (userDb == null) {
            return false;
        }

        System.out.println("Check login: " + user.getEmail() + " " + user.getPassword());

        return passwordEncoder.matches(user.getPassword(), userDb.getPassword());
    }

    public User findByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (UsernameNotFoundException e) {
            return null;
        }
    }

    public boolean isUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }

    public User updateUser_Profile(User updatedUser)
    {
        User existingUser = userRepository.findById(updatedUser.getId()).orElse(null);
        if (existingUser!=null){
            // Cập nhật các trường dữ liệu nếu chúng khác null
            if (updatedUser.getAvatar() != null) {
                existingUser.setAvatar(updatedUser.getAvatar());
            }
            if (updatedUser.getPhone() != null) {
                existingUser.setPhone(updatedUser.getPhone());
            }
            if (updatedUser.getAddress() != null) {
                existingUser.setAddress(updatedUser.getAddress());
            }
            if (updatedUser.getSex() != null) {
                existingUser.setSex(updatedUser.getSex());
            }
            if (updatedUser.getFullname() != null) {
                existingUser.setFullname(updatedUser.getFullname());
            }
            if (updatedUser.getBirthday() != null) {
                existingUser.setBirthday(updatedUser.getBirthday());
            }
            existingUser.setSubscribe(updatedUser.getSubscribe());

            System.out.println("Updating user profile: " + existingUser);
            return userRepository.save(existingUser);
        }
        return null;
    }
}
