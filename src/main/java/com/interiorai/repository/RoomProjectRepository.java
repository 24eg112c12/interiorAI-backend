package com.interiorai.repository;
import com.interiorai.entity.RoomProject;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoomProjectRepository extends JpaRepository<RoomProject,Long> { java.util.List<RoomProject> findByOwnerIdOrderByCreatedAtDesc(Long ownerId); }
