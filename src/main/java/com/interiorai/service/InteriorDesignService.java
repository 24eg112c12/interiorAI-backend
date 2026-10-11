package com.interiorai.service;
import com.interiorai.entity.RoomProject;
import java.nio.file.Path;
/** Provider boundary: output is either a demo reference URL or a private generated filename. */
public interface InteriorDesignService {
 RenderedImage render(RoomProject project,Path original,String variant);
 boolean isDemo();
 record RenderedImage(String referenceUrl,String storedFile) {}
}
