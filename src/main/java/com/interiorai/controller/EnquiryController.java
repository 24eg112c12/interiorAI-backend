package com.interiorai.controller;
import com.interiorai.dto.Requests;import com.interiorai.service.EnquiryService;
import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;import org.springframework.http.*;import java.util.*;
@RestController @RequestMapping("/api/enquiries")
public class EnquiryController {
 private final EnquiryService service;public EnquiryController(EnquiryService service){this.service=service;}
 @PostMapping public ResponseEntity<Map<String,Object>> create(@Valid @RequestBody Requests.EnquiryInput input){return ResponseEntity.status(201).body(service.create(input));}
 @GetMapping("/my-enquiries") public List<Map<String,Object>> mine(){return service.mine();}
 @PutMapping("/{id}/status") public Map<String,Object> update(@PathVariable Long id,@Valid @RequestBody Requests.EnquiryStatus input){return service.status(id,input.status());}
}
