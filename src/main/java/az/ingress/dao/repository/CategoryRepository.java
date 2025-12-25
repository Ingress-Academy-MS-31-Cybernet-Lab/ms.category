package az.ingress.dao.repository;

import az.ingress.dao.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsBySlug(String slug);

    List<CategoryEntity> findByParentIsNullOrderBySortOrderAsc();

    List<CategoryEntity> findByParentIdAndIsActiveTrueOrderBySortOrderAsc(Long parentId);
}