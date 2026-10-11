package com.interiorai.controller;
import com.interiorai.dto.Requests;import com.interiorai.entity.*;import com.interiorai.exception.ApiException;
import com.interiorai.repository.*;import com.interiorai.security.CurrentAccount;import com.interiorai.service.ProfessionalService;
import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
 private final AccountRepository users;private final ProfessionalRepository pros;private final RoomProjectRepository projects;private final DesignRepository designs;private final ReviewRepository reviews;private final ServiceCategoryRepository categories;private final ProfessionalService mapper;
 public AdminController(AccountRepository u,ProfessionalRepository p,RoomProjectRepository pr,DesignRepository d,ReviewRepository r,ServiceCategoryRepository c,ProfessionalService m){users=u;pros=p;projects=pr;designs=d;reviews=r;categories=c;mapper=m;}
 @GetMapping("/users") public List<Map<String,Object>> users(){return users.findAll().stream().map(u->Map.<String,Object>of("id",u.id,"fullName",u.fullName,"email",u.email,"role",u.role.name(),"createdAt",u.createdAt,"enabled",u.enabled)).toList();}
 @PutMapping("/users/{id}/status") @Transactional public Map<String,Object> userStatus(@PathVariable Long id,@RequestBody Map<String,Boolean> request){Account user=users.findById(id).orElseThrow(()->ApiException.notFound("Account not found."));if(user.id.equals(CurrentAccount.get().id))throw ApiException.bad("Administrators cannot disable their own account.");if(!request.containsKey("enabled"))throw ApiException.bad("enabled is required.");user.enabled=Boolean.TRUE.equals(request.get("enabled"));users.save(user);return Map.of("id",user.id,"enabled",user.enabled);}
 @GetMapping("/professionals") public List<Map<String,Object>> professionals(){return pros.findAll().stream().map(mapper::asMap).toList();}
 @PutMapping("/professionals/{id}/verify") @Transactional public Map<String,Object> verify(@PathVariable Long id,@RequestBody Map<String,Boolean> request){ProfessionalProfile p=pros.findById(id).orElseThrow(()->ApiException.notFound("Professional not found."));if(p.sample)throw ApiException.bad("Sample professionals cannot be verified.");p.verified=Boolean.TRUE.equals(request.get("verified"));return mapper.asMap(pros.save(p));}
 @GetMapping("/designs") public List<Map<String,Object>> designs(){return designs.findAll().stream().map(d->Map.<String,Object>of("id",d.id,"projectId",d.project.id,"variant",d.variant,"demo",d.demo)).toList();}
 @GetMapping("/reviews") public List<Map<String,Object>> reviews(){return reviews.findAll().stream().map(r->Map.<String,Object>of("id",r.id,"professional",r.professional.name,"author",r.author.fullName,"rating",r.rating,"comment",r.comment==null?"":r.comment)).toList();}
 @DeleteMapping("/reviews/{id}") public Map<String,String> remove(@PathVariable Long id){Review r=reviews.findById(id).orElseThrow(()->ApiException.notFound("Review not found."));reviews.delete(r);return Map.of("message","Review removed.");}
 @GetMapping("/reports") public Map<String,Long> reports(){return Map.of("users",users.count(),"professionals",pros.count(),"verified",pros.findAll().stream().filter(p->p.verified).count(),"projects",projects.count(),"designs",designs.count(),"reviews",reviews.count());}
 @GetMapping("/categories") public List<ServiceCategory> categories(){return categories.findAll();}
 @PostMapping("/categories") public ServiceCategory addCategory(@Valid @RequestBody Requests.CategoryInput r){if(r.name().length()>100)throw ApiException.bad("Category name is too long.");ServiceCategory c=new ServiceCategory();c.name=r.name().trim();return categories.save(c);}
}
