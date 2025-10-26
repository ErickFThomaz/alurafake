package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.NewTaskMultipleChoiceDTO;
import br.com.alura.AluraFake.task.dto.NewTaskOpenTextDTO;
import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import br.com.alura.AluraFake.task.dto.NewTaskSingleChoiceDTO;
import br.com.alura.AluraFake.util.exception.BadRequestException;
import br.com.alura.AluraFake.util.exception.ConflictException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskCommand taskCommand;

    @Test
    void newOpenText__should_return_bad_request_when_statement_is_invalid() throws Exception {
        NewTaskOpenTextDTO body = new NewTaskOpenTextDTO(1L, "abc", 1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenText__should_return_created_when_request_is_valid() throws Exception {
        NewTaskOpenTextDTO body = new NewTaskOpenTextDTO(1L, "Uma pergunta válida?", 1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void newOpenText__should_return_not_found_when_course_does_not_exist() throws Exception {
        NewTaskOpenTextDTO body = new NewTaskOpenTextDTO(999L, "Uma pergunta válida?", 1);

        doThrow(new ResourceNotFoundException("O curso não existe"))
                .when(taskCommand).createOpenText(any(NewTaskOpenTextDTO.class));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("O curso não existe"));
    }

    @Test
    void newOpenText__should_return_conflict_when_statement_is_duplicated() throws Exception {
        NewTaskOpenTextDTO body = new NewTaskOpenTextDTO(1L, "Pergunta repetida", 1);

        doThrow(new ConflictException("Já existe uma tarefa com a mesma pergunta"))
                .when(taskCommand).createOpenText(any(NewTaskOpenTextDTO.class));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma tarefa com a mesma pergunta"));
    }

    @Test
    void newOpenText__should_return_bad_request_when_course_is_published() throws Exception {
        NewTaskOpenTextDTO body = new NewTaskOpenTextDTO(1L, "Pergunta válida", 1);

        doThrow(new BadRequestException("Adicionar novas tarefas com o curso publicado não é possível"))
                .when(taskCommand).createOpenText(any(NewTaskOpenTextDTO.class));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Adicionar novas tarefas com o curso publicado não é possível"));
    }

    // ---------- Single Choice ----------
    @Test
    void newSingleChoice__should_return_bad_request_when_options_size_is_invalid() throws Exception {
        List<NewTaskOptionDTO> options = List.of(new NewTaskOptionDTO("Opt 1", true)); // min 2
        NewTaskSingleChoiceDTO body = new NewTaskSingleChoiceDTO(1L, "Qual a alternativa correta?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_ok_when_request_is_valid() throws Exception {
        List<NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", false)
        );
        NewTaskSingleChoiceDTO body = new NewTaskSingleChoiceDTO(1L, "Qual a alternativa correta?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void newSingleChoice__should_map_exceptions_from_command() throws Exception {
        List<NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", false)
        );
        NewTaskSingleChoiceDTO body = new NewTaskSingleChoiceDTO(1L, "Qual a alternativa correta?", 1, options);

        // Not Found
        doThrow(new ResourceNotFoundException("O curso não existe"))
                .when(taskCommand).createSingleChoice(any(NewTaskSingleChoiceDTO.class));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("O curso não existe"));

        // Conflict
        doThrow(new ConflictException("Já existe uma tarefa com a mesma pergunta"))
                .when(taskCommand).createSingleChoice(any(NewTaskSingleChoiceDTO.class));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma tarefa com a mesma pergunta"));

        // Bad Request
        doThrow(new BadRequestException("Adicionar novas tarefas com o curso publicado não é possível"))
                .when(taskCommand).createSingleChoice(any(NewTaskSingleChoiceDTO.class));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Adicionar novas tarefas com o curso publicado não é possível"));
    }

    // ---------- Multiple Choice ----------
    @Test
    void newMultipleChoice__should_return_bad_request_when_options_size_is_invalid() throws Exception {
        List<NewTaskOptionDTO> options = List.of(new NewTaskOptionDTO("Opção 1", true)); // min 2
        NewTaskMultipleChoiceDTO body = new NewTaskMultipleChoiceDTO(1L, "Selecione as corretas", 1, options);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_ok_when_request_is_valid() throws Exception {
        List<NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", true) // at least 2 correct options
        );
        NewTaskMultipleChoiceDTO body = new NewTaskMultipleChoiceDTO(1L, "Selecione as corretas", 1, options);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void newMultipleChoice__should_map_exceptions_from_command() throws Exception {
        List<NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", true) // at least 2 correct options
        );
        NewTaskMultipleChoiceDTO body = new NewTaskMultipleChoiceDTO(1L, "Selecione as corretas", 1, options);

        // Not Found
        doThrow(new ResourceNotFoundException("O curso não existe"))
                .when(taskCommand).createMultipleChoice(any(NewTaskMultipleChoiceDTO.class));
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("O curso não existe"));

        // Conflict
        doThrow(new ConflictException("Já existe uma tarefa com a mesma pergunta"))
                .when(taskCommand).createMultipleChoice(any(NewTaskMultipleChoiceDTO.class));
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma tarefa com a mesma pergunta"));

        // Bad Request
        doThrow(new BadRequestException("Adicionar novas tarefas com o curso publicado não é possível"))
                .when(taskCommand).createMultipleChoice(any(NewTaskMultipleChoiceDTO.class));
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Adicionar novas tarefas com o curso publicado não é possível"));
    }
}
