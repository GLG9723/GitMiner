package aiss.gitminer.repository;

import aiss.gitminer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // este no lo piden --> EXTRA
}
