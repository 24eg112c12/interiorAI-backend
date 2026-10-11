package com.interiorai.controller;
import com.interiorai.dto.Requests;import com.interiorai.entity.*;import com.interiorai.service.*;import com.interiorai.repository.ServiceCategoryRepository;
import jakarta.validation.Valid;import org.springframework.http.*;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;import org.springframework.web.multipart.MultipartFile;import java.math.*;import java.util.*;
@RestController @RequestMapping("/api")
public class ProfessionalController {
 private final ProfessionalService pros;private final EnquiryService enquiries;private final ServiceCategoryRepository categories;
 public ProfessionalController(ProfessionalService p,EnquiryService e,ServiceCategoryRepository c){pros=p;enquiries=e;categories=c;}
 @GetMapping("/professionals") public List<Map<String,Object>> search(@RequestParam(required=false)String profession,@RequestParam(required=false)String city,@RequestParam(required=false)Integer minExperience,@RequestParam(required=false)BigDecimal maxPrice,@RequestParam(required=false)Double minRating){return pros.search(profession,city,minExperience,maxPrice,minRating);}
 @GetMapping("/professionals/{id}") public Map<String,Object> get(@PathVariable Long id){return pros.asMap(pros.get(id));}
 @PostMapping("/professionals/register") public ResponseEntity<?> register(@Valid @RequestBody Requests.ProfessionalRegistration r){return ResponseEntity.status(201).body(pros.register(r));}
 @PreAuthorize("hasRole('PROFESSIONAL')") @GetMapping("/professionals/my-profile") public Map<String,Object> mine(){return pros.asMap(pros.mine());}
 @PreAuthorize("hasRole('PROFESSIONAL')") @PutMapping("/professionals/profile") public Map<String,Object> update(@Valid @RequestBody Requests.ProfileInput r){return pros.update(r);}
 @PreAuthorize("hasRole('PROFESSIONAL')") @PostMapping(value="/professionals/portfolio",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> upload(@RequestPart("image")MultipartFile photo){return pros.upload(photo);}
 @PreAuthorize("hasRole('PROFESSIONAL')") @GetMapping("/professionals/enquiries") public List<Map<String,Object>> inbox(){return enquiries.forProfessional(pros.mine());}
 @GetMapping("/portfolio/{filename}") public ResponseEntity<byte[]> photo(@PathVariable String filename){return ResponseEntity.ok().contentType(MediaType.parseMediaType(FileStore.mime(filename))).body(pros.portfolio(filename));}
 @GetMapping("/categories") public List<Map<String,Object>> categories(){return categories.findAll().stream().map(c->Map.<String,Object>of("id",c.id,"name",c.name)).toList();}
 @PostMapping("/reviews") public Map<String,String> review(@Valid @RequestBody Requests.ReviewInput input){pros.review(input);return Map.of("message","Review submitted.");}
}
