package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @GetMapping
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Comment findById(@PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);

        if (!comment.isPresent()) {
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody @Valid Comment comm) {
        Comment comment = commentRepository.save(comm);
        return comment;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateComment(@RequestBody @Valid Comment updatedComment, @PathVariable String id)
        throws CommentNotFoundException {
        Optional<Comment> commData = commentRepository.findById(id);
        if (!commData.isPresent()) {
            throw new CommentNotFoundException();
        } else {
            Comment comment = commData.get();
            comment.setBody(updatedComment.getBody());
            comment.setCreatedAt(updatedComment.getCreatedAt());
            comment.setUpdatedAt(updatedComment.getUpdatedAt());
            commentRepository.save(comment);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable String id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
    }


}
