package com.courseplatform.userservice;

import com.courseplatform.common.dto.RegisterRequest;
import com.courseplatform.userservice.entity.Role;
import com.courseplatform.userservice.entity.User;
import com.courseplatform.userservice.repository.UserRepository;
import com.courseplatform.userservice.security.JwtService;
import com.courseplatform.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceApplicationTests {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    void contextLoads() {
        assertNotNull(userService);
    }
    
    @Test
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        var response = userService.register(request);
        
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());
    }
    
    @Test
    void testGetUserById() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();
        user = userRepository.save(user);
        
        var userDTO = userService.getUserById(user.getId());
        
        assertNotNull(userDTO);
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("test@example.com", userDTO.getEmail());
    }
    
    @Test
    void testDuplicateUsernameThrowsException() {
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("testuser");
        request1.setEmail("test1@example.com");
        request1.setPassword("password123");
        
        userService.register(request1);
        
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("testuser");
        request2.setEmail("test2@example.com");
        request2.setPassword("password123");
        
        assertThrows(RuntimeException.class, () -> userService.register(request2));
    }
}
