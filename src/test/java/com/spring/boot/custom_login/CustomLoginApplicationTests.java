package com.spring.boot.custom_login;

import com.spring.boot.custom_login.routes.Routes;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class CustomLoginApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private static String authorizationKey = null;

    @Before
    void setUp() {

    }

    @BeforeEach
    void initUseCase() {

    }

    private String getAuthorizationKey(){
        return authorizationKey;
    }

    private void setAuthorizationKey(String authorizationKey){
        this.authorizationKey = authorizationKey;
    }

    @Test
    void userLogout() throws Exception {
        MvcResult result = mockMvc
                .perform(post(Routes.BASE_URL + Routes.LOGOUT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Authorization", getAuthorizationKey())
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void userLogin() throws Exception {
        String userName = "admin";
        String password = "admin123";
        MvcResult result = mockMvc
                .perform(post(Routes.BASE_URL + Routes.LOGIN)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .characterEncoding("utf-8")
                        .param("username", userName)
                        .param("password", password)
                )
                .andExpect(status().isOk())
                .andReturn();
        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        setAuthorizationKey(obj.getString("api_key"));
    }
}
