package br.com.luiscutilak.todolist2.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.luiscutilak.todolist2.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")    
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        //Vincular Id usuario a tarefa
        taskModel.setIdUser((UUID)idUser);
        var currentDate = LocalDateTime.now();
        //Se data atual maior que Data startada pela tarefa >> ERROR
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início / data término, deve ser maior do que a data atual.");
        }
        // Se a data de inicio da tarefa for maior que a data de témino ERROR tbm.
        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor do que a data de término.");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);

    }
    // Buscando Usuario, e as tarefas vinculadas a esse iD User.
    @GetMapping("/")    
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;

    }
    //Alterar/Update nas tarefas(tasks) pelo id do user
    @PutMapping("/{id}")
    public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {


       var task = this.taskRepository.findById(id).orElse(null);

       Utils.copyNonNullProperties(taskModel, task);


        return this.taskRepository.save(task);
    }


}
