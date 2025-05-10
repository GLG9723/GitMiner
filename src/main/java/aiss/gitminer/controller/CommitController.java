package aiss.gitminer.controller;

import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Project;
import aiss.gitminer.repository.CommitRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name="Commit", description= "Commit management API")
@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {

    @Autowired
    CommitRepository commitRepository;

    @Operation(
            summary = "Retrieve all commits",
            description = "Get all Commit objects",
            tags = {"repository", "get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")})
    })
    @GetMapping
    public List<Commit> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String name, @RequestParam(required = false) String order) {

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

        Page<Commit> pageCommits;

        if (name != null) {
            pageCommits = commitRepository.findByName(name,paging);
        }else {
            pageCommits = commitRepository.findAll(paging);
        }
        return pageCommits.getContent();
    }

    @Operation(
            summary = "Retrieve a commit by id",
            description = "Get a Commit object specifying its id",
            tags = {"repository", "get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public Commit findOne(@Parameter(description= "id of a commit to be searched")@PathVariable String id) throws CommitNotFoundException {
        Optional<Commit> commit = commitRepository.findById(id);

        if (!commit.isPresent()) {
            throw new CommitNotFoundException();
        }
        return commit.get();
    }

    @Operation(
            summary = "Insert a commit",
            description = "Create a new Commit object",
            tags = {"repository", "post", "create"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commit createCommit(@RequestBody @Valid Commit comm) {
        Commit commit = commitRepository.save(comm);
        return commit;
    }

    @Operation(
            summary = "Update a commit by id",
            description = "Update a Commit object specifying its id or throws an Exception if not exist",
            tags = {"repository", "put", "update"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCommit(@RequestBody @Valid Commit updatedComm,@Parameter(description="id of a commit to be updated") @PathVariable String id)
            throws CommitNotFoundException {
        Optional<Commit> commData = commitRepository.findById(id);

        if (commData.isPresent()) {
            Commit commit = commData.get();
            commit.setTitle(updatedComm.getTitle());
            commit.setMessage(updatedComm.getMessage());
            commit.setWebUrl(updatedComm.getWebUrl());
            commit.setAuthorName(updatedComm.getAuthorName());
            commit.setAuthorEmail(updatedComm.getAuthorEmail());
            commit.setAuthoredDate(updatedComm.getAuthoredDate());
            commitRepository.save(commit);
        } else {
            throw new CommitNotFoundException();
        }

    }

    @Operation(
            summary = "Delete a commit by id",
            description = "Delete a Commit object specifying its id",
            tags = {"repository", "delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommit(@Parameter(description = "id of a comment to be deleted")@PathVariable String id) {
        if (commitRepository.existsById(id)) {
            commitRepository.deleteById(id);
        }
    }
}
