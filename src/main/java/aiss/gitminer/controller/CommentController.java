package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Project;
import aiss.gitminer.repository.CommentRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Tag(name="Comment", description= "Comment management API")
@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Operation(
            summary = "Retrieve all comments",
            description = "Get all Comment objects",
            tags = {"Comment", "Get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")})
    })
    @GetMapping
    public List<Comment> findAll(@Parameter(description = "Page number to be retrieved")@RequestParam(defaultValue = "0") int page,@Parameter(description = "Page size to be retrieved") @RequestParam(defaultValue = "10") int size,@Parameter(description = "id of the author to be filtered") @RequestParam(required = false) String authorId,@Parameter(description = "order of the request retrieved") @RequestParam(required = false) String order) {

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

        Page<Comment> pageComments;

        if (authorId != null) {
            pageComments = commentRepository.findCommentByAuthor_Id(authorId, paging);
        }else {
            pageComments = commentRepository.findAll(paging);
        }
        return pageComments.getContent();
    }

    @Operation(
            summary = "Retrieve a comment by id",
            description = "Get a Comment object specifying its id",
            tags = {"Comment", "Get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public Comment findById(@Parameter(description= "id of a comment to be searched") @PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);

        if (!comment.isPresent()) {
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    @Operation(
            summary = "Insert a comment",
            description = "Create a new Comment object",
            tags = {"Comment", "Post"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody @Valid Comment comm) {
        Comment comment = commentRepository.save(comm);
        return comment;
    }

    @Operation(
            summary = "Update a comment by id",
            description = "Update a Comment object specifying its id or throws an Exception if not exist",
            tags = {"Comment", "Put"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateComment(@RequestBody @Valid Comment updatedComment, @Parameter(description="id of a comment to be updated")@PathVariable String id)
            throws CommentNotFoundException {
        Optional<Comment> commData = commentRepository.findById(id);
        if (!commData.isPresent()) {
            throw new CommentNotFoundException();
        } else {
            Comment comment = commData.get();
            comment.setBody(updatedComment.getBody());
            comment.setUpdatedAt(LocalDateTime.now().toString());
            commentRepository.save(comment);
        }
    }

    @Operation(
            summary = "Delete a comment by id",
            description = "Delete a Comment object specifying its id",
            tags = {"Comment", "Delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Parameter(description = "id of a comment to be deleted")@PathVariable String id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
    }


}
