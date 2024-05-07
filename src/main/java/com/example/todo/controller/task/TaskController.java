package com.example.todo.controller.task;

import com.example.todo.service.task.TaskEntity;
import com.example.todo.service.task.TaskSearchEntity;
import com.example.todo.service.task.TaskService;
import com.example.todo.service.task.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor //コンストラクタ不要でBeanが使える
@RequestMapping("/tasks") // /tasksを設定することでGetMappingやPostMappingのreturnのテンプレ指定を省略
public class TaskController {

    private final TaskService taskService;

//    @GetMapping
////    public String list(TaskSearchForm searchForm, Model model) {
//    public String list(Model model) {
////        var taskList = taskService.find(searchForm.toEntity())
//        var taskList = taskService.find()
//                .stream()
////                    .map(entity -> new TaskDTO(
////                        entity.id(), entity.summary(), entity.description(), entity.status().name()
//                .map(TaskDTO::toDTO)
//                .toList();
//        model.addAttribute("taskList", taskList);
////        model.addAttribute("searchDTO", searchForm.toDTO());
//        return "tasks/list";
//    }

    // 検索画面から検索条件が引数として渡った場合を想定したlistメソッド
    @GetMapping
    public String list(TaskSearchForm searchForm, Model model) {
//        //statusをEnumのリストにする statusが未選択:nullの時は空のリスト
//        var statusEntitiyList = Optional.ofNullable(searchForm.status())
//                .map(statusList -> statusList.stream().map(TaskStatus::valueOf).toList())
//                .orElse(List.of());
//        var searchEntity = new TaskSearchEntity(searchForm.summary(), statusEntitiyList);
        var taskList = taskService.find(searchForm.toEntity())
                .stream()
                .map(TaskDTO::toDTO)
                .toList();
        model.addAttribute("taskList", taskList);
        model.addAttribute("searchDTO", searchForm.toDTO());
        return "tasks/list";
    }

//    @GetMapping("/detail")
//    public String showDetail() {
//        return "tasks/detail";
//    }

    @GetMapping("/{id}")
    public String showDetail(@PathVariable("id") long taskId, Model model) {
        // taskIdからtaskEntityを取得したい
//        taskService.findById(taskId); Optional<taskEntity>で渡されるのでOptionalから抜き出す
//        var taskEntity = taskService.findById(taskId)
//                .orElseThrow(TaskNotFoundException::new);
//            .orElseThrow(() -> new IllegalArgumentException("Task not found: if = " + taskId));
//        model.addAttribute("taskId", taskId); taskDTOを渡そう
//        model.addAttribute("task", TaskDTO.toDTO(taskEntity));
        var taskDTO = taskService.findById(taskId)
                        .map(TaskDTO::toDTO)
                                .orElseThrow(TaskNotFoundException::new);
        model.addAttribute("task", taskDTO);
        return "tasks/detail";
    }

//    @GetMapping("/{id}")
//    public String showDetail(@PathVariable("id") long taskId, Model model) {
//        var taskDTO = taskService.findById(taskId)
//                .map(TaskDTO::toDTO)
//                .orElseThrow(TaskNotFoundException::new);
//        model.addAttribute("task", taskDTO);
//        return "tasks/detail";
//    }
//
    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute TaskForm form, Model model) {
        model.addAttribute("mode", "CREATE");
        return "tasks/form";
    }

    @PostMapping
    public String create(@Validated TaskForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showCreationForm(form, model); //エラーの時に直前の入力値を復元して戻す
        }
        taskService.create(form.toEntity());
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/editForm")
    public String showEditForm(@PathVariable("id") long id, Model model) {
//        var form = new TaskForm("hoge", "hoge", "TODO"); dummy
//        var taskEntity= taskService.findById(id) // Optional型をはがす
//                        .orElseThrow(TaskNotFoundException::new);
        // statusはString型でないのでnameを取得
//        var form = new TaskForm(taskEntity.summary(), taskEntity.description(), taskEntity.status().name());
        var form = taskService.findById(id)
                .map(TaskForm::fromEntity) //DBに値があればmodel(表示内容)に渡す
                .orElseThrow(TaskNotFoundException::new); //DBに値がなければ404エラー
        model.addAttribute("taskForm", form);
        model.addAttribute("mode", "EDIT");
        return "tasks/form";
    }

    @PutMapping("{id}") //PUT tasks/{id}を処理できるようにする
    public String update(
            @PathVariable("id") long id,
            @Validated @ModelAttribute TaskForm form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
//            model.addAttribute("taskForm", form); // エラー時の戻り遷移で入力値を復元→@ModelAttributeで可能
            model.addAttribute("mode", "EDIT"); //エラー時の戻り遷移でモードを渡す
            return "tasks/form";
        }
        var entity = form.toEntity(id); //更新のためID指定のtoEntity
        taskService.update(entity);
        return "redirect:/tasks/{id}";
    }

    //POST /tasks/1 (hidden: _method: delete)というURLが来た時、-> DELETE /tasks/1と読み替える処理
    @DeleteMapping("{id}")
    public String delete(@PathVariable("id") long id) {
        taskService.delete(id);
        return "redirect:/tasks";
    }

//
//    @PostMapping
//    public String create(@Validated TaskForm form, BindingResult bindingResult, Model model) {
//        if (bindingResult.hasErrors()) {
//            return showCreationForm(form, model);
//        }
//        taskService.create(form.toEntity());
//        return "redirect:/tasks";
//    }
//
//    @GetMapping("/{id}/editForm")
//    public String showEditForm(@PathVariable("id") long id, Model model) {
//        var form = taskService.findById(id)
//                .map(TaskForm::fromEntity)
//                .orElseThrow(TaskNotFoundException::new);
//        model.addAttribute("taskForm", form);
//        model.addAttribute("mode", "EDIT");
//        return "tasks/form";
//    }
//
//    @PutMapping("{id}") // PUT /tasks/{id}
//    public String update(
//            @PathVariable("id") long id,
//            @Validated @ModelAttribute TaskForm form,
//            BindingResult bindingResult,
//            Model model
//    ) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("mode", "EDIT");
//            return "tasks/form";
//        }
//        var entity = form.toEntity(id);
//        taskService.update(entity);
//        return "redirect:/tasks/{id}";
//    }
//
//    // POST /tasks/1 (hidden: _method: delete)
//    // -> DELETE /tasks/1
//    @DeleteMapping("{id}")
//    public String delete(@PathVariable("id") long id) {
//        taskService.delete(id);
//        return "redirect:/tasks";
//    }
}
