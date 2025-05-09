package aiss.gitminer.controller;

import aiss.gitminer.model.Project;
import aiss.gitminer.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @GetMapping
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Project findOne(@PathVariable long id) {
        Optional<Project> proj = projectRepository.findById(id);
        return proj.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@RequestBody @Valid Project project) {
        Project proj = projectRepository.save(new Project(project.getName(), project.getWebUrl()));
        return proj;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProject(@RequestBody @Valid Project updatedProject, @PathVariable long id) {
        Optional<Project> projData = projectRepository.findById(id);

        if (projData.isPresent()) {
            Project proj = projData.get();
            proj.setName(updatedProject.getName());
            proj.setWebUrl(updatedProject.getWebUrl());
            projectRepository.save(proj);
        } else {
            // crear el projectNotFound
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }
    }
}
