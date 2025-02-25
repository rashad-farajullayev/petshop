package com.thesniffers.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@NoRepositoryBean
public interface GenericRepository<T> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
}
