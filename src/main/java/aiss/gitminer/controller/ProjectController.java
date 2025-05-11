package aiss.gitminer.controller;

import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Project;
import aiss.gitminer.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name="Project", description= "Project management API")
@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;
    @Operation(
            summary = "Retrieve all projects",
            description = "Get all Project objects",
            tags = {"Project", "Get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")})
    })
    @GetMapping
    public List<Project> findAll(@Parameter(description = "Page number to be retrieved") @RequestParam(defaultValue = "0") int page,@Parameter(description = "Page size to be retrieved") @RequestParam(defaultValue = "10") int size,@Parameter(description = "atribute to be filtered") @RequestParam(required = false) String name,@Parameter(description = "order of the request retrieved") @RequestParam(required = false) String order) {

        Pageable paging;

        if(order != null) {
            if(order.startsWith("-")) {
                paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
            }else {
                paging = PageRequest.of(page, size, Sort.by(order).ascending());
            }
        }else {
            paging = PageRequest.of(page, size);
        }

        Page<Project> pageProjects;

        if (name != null) {
            pageProjects = projectRepository.findByName(name,paging);
        }else {
            pageProjects = projectRepository.findAll(paging);
        }
        return pageProjects.getContent();
    }

    @Operation(
            summary = "Retrieve a project by id",
            description = "Get a Project object specifying its id",
            tags = {"Project", "Get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public Project findOne(@Parameter(description= "id of a project to be searched")@PathVariable String id) throws ProjectNotFoundException {
        Optional<Project> proj = projectRepository.findById(id);

        if (!proj.isPresent()) {
            throw new ProjectNotFoundException();
        }
        return proj.get();
    }

    @Operation(
            summary = "Insert a project",
            description = "Create a new Project object",
            tags = {"Project", "Post"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@RequestBody @Valid Project project) {
        Project proj = projectRepository.save(project);
        return proj;
    }

    @Operation(
            summary = "Update a project by id",
            description = "Update a project object specifying its id or throws an Exception if not exist",
            tags = {"Project", "Put"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProject(@RequestBody @Valid Project updatedProject,@Parameter(description= "id of a project to be updated") @PathVariable String id)
            throws ProjectNotFoundException {
        Optional<Project> projData = projectRepository.findById(id);

        if (projData.isPresent()) {
            Project proj = projData.get();
            proj.setName(updatedProject.getName());
            proj.setWebUrl(updatedProject.getWebUrl());
            projectRepository.save(proj);
        } else {
            throw new ProjectNotFoundException();
        }
    }

    @Operation(
            summary = "Delete a project by id",
            description = "Delete a Project object specifying its id",
            tags = {"Project", "Delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@Parameter(description= "id of a project to be deleted")@PathVariable String id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }
    }
}
