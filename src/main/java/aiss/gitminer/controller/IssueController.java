package aiss.gitminer.controller;

import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {

    @Autowired
    IssueRepository issueRepository;

    @GetMapping
    public List<Issue> findAll(@RequestParam(required = false) String authorId,
                               @RequestParam(required = false) String state) {

        List<Issue> issues;

        if (state != null) {
            issues = issueRepository.findByState(state);
        } else {
            issues = issueRepository.findAll();
        }

        if (authorId != null) {
            issues = issues.stream().filter(x -> authorId.equals(x.getAuthor().getId())).toList();
        }
        return issues;
    }

    @GetMapping("/{id}")
    public Issue findOne(@PathVariable String id) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(id);

        if (!issue.isPresent()) {
            throw new IssueNotFoundException();
        }
        return issue.get();
    }

    @GetMapping("/{id}/comments")
    public List<Comment> findOneComments(@PathVariable String id) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(id);

        if (!issue.isPresent()) {
            throw new IssueNotFoundException();
        }
        return issue.get().getComments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createCommit(@RequestBody @Valid Issue iss) {
        Issue issue = issueRepository.save(iss);
        return issue;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCommit(@RequestBody @Valid Issue updatedIss, @PathVariable String id)
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommit(@PathVariable String id) {
        if (issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
        }
    }
}
