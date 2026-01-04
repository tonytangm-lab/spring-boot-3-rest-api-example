// java
package com.bezkoder.spring.restapi.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import com.bezkoder.spring.restapi.model.Tutorial;
import com.bezkoder.spring.restapi.service.TutorialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TutorialControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TutorialService tutorialService;

    @InjectMocks
    private TutorialController tutorialController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(tutorialController).build();
    }

    @Test
    void getAllTutorials_returnsList() throws Exception {
        Tutorial t = new Tutorial();
        t.setId(1L);
        t.setTitle("Title");
        t.setDescription("Desc");
        t.setPublished(true);

        when(tutorialService.findAll()).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void getAllTutorials_noContent() throws Exception {
        when(tutorialService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTutorialById_found() throws Exception {
        Tutorial t = new Tutorial();
        t.setId(1L);
        t.setTitle("Found");
        when(tutorialService.findById(1L)).thenReturn(t);

        mockMvc.perform(get("/api/tutorials/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Found"));
    }

    @Test
    void getTutorialById_notFound() throws Exception {
        when(tutorialService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/tutorials/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTutorial_success() throws Exception {
        Tutorial input = new Tutorial();
        input.setTitle("New");
        input.setDescription("D");
        input.setPublished(false);

        Tutorial saved = new Tutorial();
        saved.setId(10L);
        saved.setTitle("New");
        saved.setDescription("D");
        saved.setPublished(false);

        when(tutorialService.save(org.mockito.ArgumentMatchers.any(Tutorial.class))).thenReturn(saved);

        mockMvc.perform(post("/api/tutorials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("New"));
    }

    @Test
    void updateTutorial_found() throws Exception {
        Tutorial existing = new Tutorial();
        existing.setId(1L);
        existing.setTitle("Old");
        existing.setDescription("OldD");
        existing.setPublished(false);

        Tutorial update = new Tutorial();
        update.setTitle("Updated");
        update.setDescription("NewD");
        update.setPublished(true);

        Tutorial saved = new Tutorial();
        saved.setId(1L);
        saved.setTitle("Updated");
        saved.setDescription("NewD");
        saved.setPublished(true);

        when(tutorialService.findById(1L)).thenReturn(existing);
        when(tutorialService.save(org.mockito.ArgumentMatchers.any(Tutorial.class))).thenReturn(saved);

        mockMvc.perform(put("/api/tutorials/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void updateTutorial_notFound() throws Exception {
        when(tutorialService.findById(1L)).thenReturn(null);

        Tutorial update = new Tutorial();
        update.setTitle("X");

        mockMvc.perform(put("/api/tutorials/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTutorial_success() throws Exception {
        doNothing().when(tutorialService).deleteById(1L);

        mockMvc.perform(delete("/api/tutorials/1"))
                .andExpect(status().isNoContent());

        verify(tutorialService).deleteById(1L);
    }

    @Test
    void deleteAllTutorials_success() throws Exception {
        doNothing().when(tutorialService).deleteAll();

        mockMvc.perform(delete("/api/tutorials"))
                .andExpect(status().isNoContent());

        verify(tutorialService).deleteAll();
    }

    @Test
    void findByPublished_returnsList() throws Exception {
        Tutorial t = new Tutorial();
        t.setId(2L);
        t.setTitle("Pub");
        t.setPublished(true);

        when(tutorialService.findByPublished(true)).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].published").value(true));
    }

    @Test
    void findByPublished_noContent() throws Exception {
        when(tutorialService.findByPublished(true)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isNoContent());
    }
}
