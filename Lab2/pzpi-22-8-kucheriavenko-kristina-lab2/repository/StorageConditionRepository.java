package com.BiologicalMaterialsSystem.repository;

import com.BiologicalMaterialsSystem.model.StorageCondition;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageConditionRepository extends JpaRepository<StorageCondition, Long> {

    @Query("SELECT AVG(sc.temperature), AVG(sc.oxygenLevel), AVG(sc.humidity) " +
            "FROM StorageCondition sc " +
            "WHERE sc.materialID.id = :materialId")
    List<Object[]> findAverageValuesByMaterialId(Long materialId);
}
