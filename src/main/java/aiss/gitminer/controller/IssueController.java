package aiss.gitminer.controller;

import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repository.IssueRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name="Issue", description= "Issue management API")
@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {

    @Autowired
    IssueRepository issueRepository;

    @Operation(
            summary = "Retrieve all issues",
            description = "Get all Issue objects",
            tags = {"Issue", "Get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Issue.class), mediaType = "application/json")})
    })
    @GetMapping
    public List<Issue> findAll(@Parameter(description = "page number to be retrieved")@RequestParam(defaultValue = "0") int page,@Parameter(description = "page size to be retrieved") @RequestParam(defaultValue = "10") int size,@Parameter(description = "id of the author to be filtered") @RequestParam(required = false) String authorId,@Parameter(description = "state of the issue to be filtered") @RequestParam(required = false) String state,@Parameter(description = "order of the request to be retrieved") @RequestParam(required = false) String order) {

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

        Page<Issue> pageIssues;

        if (authorId != null && state != null) {
            pageIssues = issueRepository.findIssueByStateAndAuthor_Id(state, authorId, paging);
        } else if (authorId != null && state == null) {
            pageIssues = issueRepository.findIssueByAuthor_Id(authorId, paging);
        } else if (authorId == null && state != null) {
            pageIssues = issueRepository.findIssueByState(state, paging);
        } else {
            pageIssues = issueRepository.findAll(paging);
        }

        return pageIssues.getContent();
    }

    @Operation(
            summary = "Retrieve an issue by id",
            description = "Get an Issue object specifying its id",
            tags = {"Issue", "Get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Issue.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public Issue findOne(@Parameter(description= "id of an issue to be searched")@PathVariable String id) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(id);

        if (!issue.isPresent()) {
            throw new IssueNotFoundException();
        }
        return issue.get();
    }

    @Operation(
            summary = "Retrieve all comments of an issue by id",
            description = "Get all Comment objects of an Issue object specifying its id, if not found throws an Exception",
            tags = {"Issue", "Get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}/comments")
    public List<Comment> findOneComments(@Parameter(description = "id of an issue to be searched") @PathVariable String id) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(id);

        if (!issue.isPresent()) {
            throw new IssueNotFoundException();
        }
        return issue.get().getComments();
    }

    @Operation(
            summary = "Insert an issue",
            description = "Create a new Issue object",
            tags = {"Issue", "Post"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = Issue.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createCommit(@RequestBody @Valid Issue iss) {
        Issue issue = issueRepository.save(iss);
        return issue;
    }

    @Operation(
            summary = "Update an issue by id",
            description = "Update an Issue object specifying its id or throws an Exception if not exist",
            tags = {"Issue", "Put"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCommit(@RequestBody @Valid Issue updatedIss,@Parameter(description= "id of an issue to be updated") @PathVariable String id)
            throws IssueNotFoundException {
        Optional<Issue> issData = issueRepository.findById(id);

        if (issData.isPresent()) {
            Issue issue = issData.get();
            issue.setTitle(updatedIss.getTitle());
            issue.setDescription(updatedIss.getDescription());
            issue.setState(updatedIss.getState());
            issue.setCreatedAt(updatedIss.getCreatedAt());
            issue.setUpdatedAt(updatedIss.getUpdatedAt());
            issue.setClosedAt(updatedIss.getClosedAt());
            issue.setLabels(updatedIss.getLabels());
            issue.setVotes(updatedIss.getVotes());
            issueRepository.save(issue);
        } else {
            throw new IssueNotFoundException();
        }

    }

    @Operation(
            summary = "Delete an issue by id",
            description = "Delete an Issue object specifying its id",
            tags = {"Issue", "Delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommit(@Parameter(description= "id of an issue to be deleted")@PathVariable String id) {
        if (issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
        }
    }
}
