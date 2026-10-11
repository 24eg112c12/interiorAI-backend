package com.interiorai.repository;
import com.interiorai.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoomImageRepository extends JpaRepository<RoomImage,Long> { java.util.Optional<RoomImage> findByProjectId(Long projectId); }
