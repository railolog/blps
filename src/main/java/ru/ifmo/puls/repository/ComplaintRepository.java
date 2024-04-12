package ru.ifmo.puls.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ifmo.puls.domain.ComplaintConv;

@Repository
public interface ComplaintRepository extends JpaRepository<ComplaintConv, Long> {
}
