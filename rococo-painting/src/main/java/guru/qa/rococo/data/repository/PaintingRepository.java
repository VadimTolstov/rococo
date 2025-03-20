package guru.qa.rococo.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import guru.qa.rococo.data.PaintingEntity;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
    Page<PaintingEntity> findAllByArtist(Pageable pageable, UUID artistId);

    Page<PaintingEntity> findAllByTitleContainingIgnoreCase(Pageable pageable, String title);
}