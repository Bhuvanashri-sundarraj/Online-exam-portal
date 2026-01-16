package com.exam.onlineexam;

import com.exam.onlineexam.entity.Role;
import com.exam.onlineexam.entity.User;
import com.exam.onlineexam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void teacherLoginSetsSessionAndMeReturnsUser() throws Exception {
        User u = new User();
        u.setLoginId("teach1");
        u.setName("Teach One");
        u.setPassword(passwordEncoder.encode("pass"));
        u.setRole(Role.TEACHER);
        userRepository.save(u);

        String payload = "{\"loginId\":\"teach1\",\"password\":\"pass\"}";

        MvcResult loginRes = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginRes.getRequest().getSession(false);
        assertThat(session).isNotNull();

        MvcResult meRes = mockMvc.perform(get("/api/me").session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = meRes.getResponse().getContentAsString();
        assertThat(responseBody).contains("teach1").contains("TEACHER");
    }
}
