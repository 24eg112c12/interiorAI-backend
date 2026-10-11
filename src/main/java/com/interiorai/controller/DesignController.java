package com.interiorai.controller;
import com.interiorai.dto.Requests;import com.interiorai.service.*;
import jakarta.validation.Valid;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
@RestController @RequestMapping("/api")
public class DesignController {
 private final DesignService service;public DesignController(DesignService s){service=s;}
 @PostMapping(value="/designs/generate",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<?> generate(@RequestPart("data")String data,@RequestPart("image")MultipartFile image){return ResponseEntity.status(201).body(service.generate(data,image));}
 @GetMapping("/designs/my-projects") public List<Map<String,Object>> projects(){return service.mine();}
 @GetMapping("/designs/saved") public List<Map<String,Object>> saved(){return service.saved();}
 @GetMapping("/designs/{id}") public Map<String,Object> detail(@PathVariable Long id){return service.detail(id);}
 @PutMapping("/designs/{id}/items") public Map<String,Object> items(@PathVariable Long id,@Valid @RequestBody List<Requests.ItemUpdate> updates){return service.updateItems(id,updates);}
 @PostMapping("/designs/{id}/save") public Map<String,String> save(@PathVariable Long id){service.save(id);return Map.of("message","Design saved.");}
 @DeleteMapping("/designs/{id}/save") public Map<String,String> unsave(@PathVariable Long id){service.unsave(id);return Map.of("message","Design removed from saved.");}
 @DeleteMapping("/designs/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.deleteDesign(id);return ResponseEntity.noContent().build();}
 @GetMapping("/projects/{id}") public Map<String,Object> project(@PathVariable Long id){return service.project(service.ownedProject(id));}
 @PostMapping("/projects/{id}/regenerate") public Map<String,Object> regenerate(@PathVariable Long id){return service.regenerate(id);}
 @GetMapping("/projects/{id}/image") public ResponseEntity<byte[]> original(@PathVariable Long id){return ResponseEntity.ok().contentType(MediaType.parseMediaType(service.originalMime(id))).header(HttpHeaders.CACHE_CONTROL,"private, max-age=300").body(service.original(id));}
 @GetMapping("/designs/{id}/image") public ResponseEntity<byte[]> generated(@PathVariable Long id){return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).header(HttpHeaders.CACHE_CONTROL,"private, max-age=300").body(service.generated(id));}
}
