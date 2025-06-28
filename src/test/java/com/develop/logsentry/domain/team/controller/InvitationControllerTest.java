package com.develop.logsentry.domain.team.controller;

import com.develop.logsentry.common.filter.AuthFilter;
import com.develop.logsentry.common.message.SuccessMessage;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.log.service.LogProducer;
import com.develop.logsentry.domain.team.service.InvitationService;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvitationController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvitationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaRepository jpaRepository;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private LogProducer logProducer;

    @MockBean
    private AuthFilter authFilter;

    @MockBean
    private InvitationService invitationService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "user", "user@example.com", "pass", UserRoleEnum.USER, null, true, null);
        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("초대 수락 - 성공")
    void acceptInvitation_success() throws Exception {
        // Given
        String token = "valid-token";
        doNothing().when(invitationService).acceptInvitation(eq(token), eq(mockUser));

        // When & Then
        mockMvc.perform(post("/v1/invitation/{token}/accept", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessMessage.INVITATION_ACEEPT_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessMessage.INVITATION_ACEEPT_SUCCESS.getMessage()));

        verify(invitationService, times(1)).acceptInvitation(eq(token), eq(mockUser));
    }
}