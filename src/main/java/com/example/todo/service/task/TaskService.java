package com.example.todo.service.task;

import com.example.todo.repository.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    // コントローラ層に構えていたビジネスロジックをビジネスロジック層に移動
//    public List<TaskEntity> find() {
//        //model.addAttribute("task", "Spring Bootを学ぶ"); //テンプレート側にtaskというキーで値を渡す
//        var task = new TaskEntity(
//                1L,
//                "testSum",
//                "testDes",
//                TaskStatus.TODO //ENUMで設定
//        );
//        var task2 = new TaskEntity(
//                2L,
//                "testSum",
//                "testDes",
//                TaskStatus.TODO
//        );
//        return List.of(task, task2);
//    }

    private final TaskRepository taskRepository;

    public Optional<TaskEntity> findById(long taskId) {
            return taskRepository.selectById(taskId);
    }

    @Transactional
    public void create(TaskEntity newEntity) {
        taskRepository.insert(newEntity);
//        throw new IllegalArgumentException("TEST"); //テストの為絶対発生する例外
    }

    @Transactional
    public void update(TaskEntity entity) {
        taskRepository.update(entity);
    }

    public void delete(long id) {
        taskRepository.delete(id);
    }

    public List<TaskEntity> find(TaskSearchEntity searchEntity) {
        return taskRepository.select(searchEntity);
    }

//    public Optional<TaskEntity> findById(long taskId) {
//        return taskRepository.selectById(taskId);
//    }
//
//    @Transactional
//    public void create(TaskEntity newEntity) {
//        taskRepository.insert(newEntity);
//    }
//
//    @Transactional
//    public void update(TaskEntity entity) {
//        taskRepository.update(entity);
//    }
//
//    @Transactional
//    public void delete(long id) {
//        taskRepository.delete(id);
//    }
}
