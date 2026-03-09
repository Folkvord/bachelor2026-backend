package no.bachelor26.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.bachelor26.Entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{

    Optional<Task> findByName(String name);

}
