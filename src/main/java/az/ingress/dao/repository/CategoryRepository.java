package az.ingress.dao.repository;

import az.ingress.dao.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsBySlug(String slug);

    List<CategoryEntity> findByParentIsNullOrderBySortOrderAsc();

    List<CategoryEntity> findByParentIdAndIsActiveTrueOrderBySortOrderAsc(Long parentId);
}