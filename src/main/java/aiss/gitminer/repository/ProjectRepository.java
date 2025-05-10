package aiss.gitminer.repository;

import aiss.gitminer.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);

    Page<Project> findByName(String name, Pageable pageable);
}
