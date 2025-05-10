package aiss.gitminer.repository;

import aiss.gitminer.model.Commit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    Optional<Commit> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);

    Page<Commit> findCommitByAuthorName(@NotEmpty(message = "Author name cannot be empty.") String authorName, Pageable pageable);
}
