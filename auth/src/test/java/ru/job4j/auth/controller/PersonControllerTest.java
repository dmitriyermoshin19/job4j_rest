package ru.job4j.auth.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.job4j.auth.domain.Person;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
class PersonControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    PersonController personController;

    @Test
    void whenGetRequestFindAllThenReturnJSONs() throws Exception {
        Person user1 = Person.of(1, "user1", "123");
        Person user2 = Person.of(2, "user2", "456");
        when(personController.findAll()).thenReturn(Arrays.asList(user1, user2));
        mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].login", is("user1")))
                .andExpect(jsonPath("$[0].password", is("123")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].login", is("user2")))
                .andExpect(jsonPath("$[1].password", is("456")));
        verify(personController, times(1)).findAll();
        verifyNoMoreInteractions(personController);
    }

    @Test
    void whenGetRequestWithNotExistedIdThenReturnJsonWithNullState() throws Exception {
        Person user1 = new Person();
        when(personController.findById(anyInt()))
                .thenReturn(new ResponseEntity<>(user1, HttpStatus.NOT_FOUND));
        mockMvc.perform(get("/person/{id}", anyInt()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.login", nullValue()))
                .andExpect(jsonPath("$.password", nullValue()));
        verify(personController, times(1)).findById(anyInt());
        verifyNoMoreInteractions(personController);
    }

    @Test
    void whenGetRequestWithPresentedInDBIdThenReturnJson() throws Exception {
        Person user1 = Person.of(1, "user1", "123");
        when(personController.findById(1))
                .thenReturn(new ResponseEntity<Person>(user1, HttpStatus.OK));
        mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.login", is("user1")))
                .andExpect(jsonPath("$.password", is("123")));
        verify(personController, times(1)).findById(anyInt());
        verifyNoMoreInteractions(personController);
    }

    @Test
    void whenPostRequestThenReturnJsonWithPersonObjectAndRealId() throws Exception {
        Person userReturn = Person.of(1, "user1", "123");
        when(personController.create(any(Person.class)))
                .thenReturn(new ResponseEntity<>(userReturn, HttpStatus.OK));
        mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"user1\",\"password\":\"123\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.login", is("user1")))
                .andExpect(jsonPath("$.password", is("123")));
        verify(personController, times(1)).create(any(Person.class));
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personController).create(captor.capture());
        assertEquals(0, captor.getValue().getId());
        assertEquals("user1", captor.getValue().getLogin());
        assertEquals("123", captor.getValue().getPassword());
        verifyNoMoreInteractions(personController);
    }

    @Test
    void whenPutRequestThenReturnStatusOk() throws Exception {
        when(personController.update(any(Person.class)))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        mockMvc.perform(put("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"user1\",\"password\":\"123\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(personController, times(1)).update(any(Person.class));
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personController).update(captor.capture());
        assertEquals(0, captor.getValue().getId());
        assertEquals("user1", captor.getValue().getLogin());
        assertEquals("123", captor.getValue().getPassword());
        verifyNoMoreInteractions(personController);
    }

    @Test
    void whenDeleteRequestThenReturnStatusOk() throws Exception {
        when(personController.delete(anyInt()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        mockMvc.perform(MockMvcRequestBuilders.delete("/person/{id}", anyInt()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(personController, times(1)).delete(anyInt());
        verifyNoMoreInteractions(personController);
    }
}