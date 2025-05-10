package aiss.gitminer.repository;

import aiss.gitminer.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    Optional<Issue> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);

    Page<Issue> findIssueByStateAndAuthor_Id(String state, String authorId, Pageable pageable);

    Page<Issue> findIssueByAuthor_Id(String authorId, Pageable pageable);

    Page<Issue> findIssueByState(String state, Pageable pageable);
}
