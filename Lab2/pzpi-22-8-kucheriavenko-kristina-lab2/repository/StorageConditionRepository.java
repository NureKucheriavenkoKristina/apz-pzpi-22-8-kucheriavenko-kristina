package com.BiologicalMaterialsSystem.repository;

import com.BiologicalMaterialsSystem.model.StorageCondition;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageConditionRepository extends JpaRepository<StorageCondition, Long> {
}
