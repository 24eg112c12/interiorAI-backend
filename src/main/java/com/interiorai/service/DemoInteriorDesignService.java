package com.interiorai.service;
import com.interiorai.entity.RoomProject;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
@Service("demoInteriorDesignService")
public class DemoInteriorDesignService implements InteriorDesignService {
 // Different Unsplash reference photos per variant. These are NOT edits of users' uploaded photos.
 private static final String[][] PHOTOS={
  {"photo-1600210492486-724fe5c67fb0","photo-1616486338812-3dadae4b4ace","photo-1600607687939-ce8a6c25118c"},
  {"photo-1616594039964-ae9021a400a0","photo-1615874959474-d609969a20ed","photo-1618221195710-dd6b41faaea6"},
  {"photo-1600585154340-be6161a56a0c","photo-1600607687920-4e2a09cf159d","photo-1600566753086-00f18fb6b3ea"},
  {"photo-1556911220-bff31c812dba","photo-1600607687644-c7171b42498f","photo-1600607687920-4e2a09cf159d"},
  {"photo-1497366754035-f200968a6e72","photo-1497366811353-6870744d04b2","photo-1497366858526-0766cadbe8fa"}
 };
 @Override public RenderedImage render(RoomProject p,Path original,String variant){
  String room=p.roomType.toLowerCase();int type=room.contains("bed")?1:room.contains("bath")?2:room.contains("kitchen")?3:room.contains("office")?4:0;
  int style=switch(variant){case "Minimalist"->1;case "Luxury"->2;default->0;};
  return new RenderedImage("https://images.unsplash.com/"+PHOTOS[type][style]+"?auto=format&fit=crop&w=1100&q=82",null);
 }
 @Override public boolean isDemo(){return true;}
}
