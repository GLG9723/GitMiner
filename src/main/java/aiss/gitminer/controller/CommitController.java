package aiss.gitminer.controller;

import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.repository.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {

    @Autowired
    CommitRepository commitRepository;

    @GetMapping
    public List<Commit> findAll() {
        return commitRepository.findAll();
    }

    @GetMapping("/{id]")
    public Commit findOne(@PathVariable Long id) throws CommitNotFoundException {
        Optional<Commit> commit = commitRepository.findById(id);

        if (!commit.isPresent()) {
            throw new CommitNotFoundException();
        }
        return commit.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commit createCommit(@RequestBody @Valid Commit comm) {
        Commit commit = commitRepository.save(comm);
        return commit;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCommit(@RequestBody @Valid Commit updatedComm, @PathVariable long id)
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommit(@PathVariable long id) {
        if (commitRepository.existsById(id)) {
            commitRepository.deleteById(id);
        }
    }
}
