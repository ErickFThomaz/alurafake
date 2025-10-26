package br.com.alura.AluraFake.task.web;

import br.com.alura.AluraFake.task.TaskCommand;
import br.com.alura.AluraFake.task.web.dto.NewTaskMultipleChoiceDTO;
import br.com.alura.AluraFake.task.web.dto.NewTaskOpenTextDTO;
import br.com.alura.AluraFake.task.web.dto.NewTaskSingleChoiceDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskCommand taskCommand;

    @Autowired
    public TaskController(TaskCommand taskCommand) {
        this.taskCommand = taskCommand;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody NewTaskOpenTextDTO dto) {
        taskCommand.createOpenText(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@Valid @RequestBody NewTaskSingleChoiceDTO dto) {
        taskCommand.createSingleChoice(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@Valid @RequestBody NewTaskMultipleChoiceDTO dto) {
        taskCommand.createMultipleChoice(dto);
        return ResponseEntity.ok().build();
    }

}
