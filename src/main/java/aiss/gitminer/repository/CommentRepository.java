package aiss.gitminer.repository;

import aiss.gitminer.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);
}
