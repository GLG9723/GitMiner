package aiss.gitminer.controller;

import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Project;
import aiss.gitminer.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @GetMapping
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Project findOne(@PathVariable String id) throws ProjectNotFoundException {
        Optional<Project> proj = projectRepository.findById(id);

        if (!proj.isPresent()) {
            throw new ProjectNotFoundException();
        }
        return proj.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@RequestBody @Valid Project project) {
        Project proj = projectRepository.save(project);
        return proj;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProject(@RequestBody @Valid Project updatedProject, @PathVariable String id)
            throws ProjectNotFoundException {
        Optional<Project> projData = projectRepository.findById(id);

        if (projData.isPresent()) { // aqui hay que cambiar lo que modifica
            Project proj = projData.get();
            proj.setName(updatedProject.getName());
            proj.setWebUrl(updatedProject.getWebUrl());
            projectRepository.save(proj);
        } else {
            throw new ProjectNotFoundException();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable String id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }
    }
}
