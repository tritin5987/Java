package com.example.thuchanh_tuan2.service;

import com.example.thuchanh_tuan2.Role;
import com.example.thuchanh_tuan2.model.User;
import com.example.thuchanh_tuan2.repository.IRoleRepository;
import com.example.thuchanh_tuan2.repository.IUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private MailService mailService;

    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    // Gán vai trò mặc định cho người dùng dựa trên tên người dùng.
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
                    userRepository.save(user);
                },
                () -> { throw new UsernameNotFoundException("User not found"); }
        );
    }

    // Tải thông tin chi tiết người dùng để xác thực.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    // Tìm kiếm người dùng dựa trên tên đăng nhập.
    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    // Thay đổi mật khẩu người dùng.
    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
    }

    // Đặt lại mật khẩu người dùng.
    public void resetPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);

            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            String message = "Click the link below to reset your password:\n" + resetLink;
            mailService.sendEmail(user.getEmail(), "Password Reset Request", message);
        }
    }

    // Xác nhận token đặt lại mật khẩu.
    public boolean validateResetToken(String token) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        return userOptional.isPresent();
    }

    // Cập nhật mật khẩu người dùng dựa trên token đặt lại mật khẩu.
    public void updatePassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            user.setResetToken(null);
            userRepository.save(user);
        }
    }

    // Xử lý đăng nhập OAuth sau khi người dùng đăng nhập bằng Google.
    public void processOAuthPostLogin(OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        User existUser = userRepository.findByEmail(email).orElse(null);

        if (existUser == null) {
            User newUser = User.builder()
                    .googleId(oidcUser.getSubject())
                    .name(oidcUser.getFullName())
                    .email(email)
                    .pictureUrl(oidcUser.getPicture())
                    .provider("GOOGLE")
                    .build();

            userRepository.save(newUser);
        } else {
            existUser.setGoogleId(oidcUser.getSubject());
            existUser.setName(oidcUser.getFullName());
            existUser.setPictureUrl(oidcUser.getPicture());
            existUser.setProvider("GOOGLE");

            userRepository.save(existUser);
        }
    }
}
