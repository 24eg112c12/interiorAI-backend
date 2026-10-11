package com.interiorai.service;
import com.interiorai.dto.Requests;
import com.interiorai.entity.*;
import com.interiorai.exception.ApiException;
import com.interiorai.repository.*;
import com.interiorai.security.CurrentAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;import java.util.*;
@Service
public class DesignService {
 private final RoomProjectRepository projects;private final RoomImageRepository images;private final DesignRepository designs;private final DesignItemRepository items;private final SavedDesignRepository saved;
 private final FileStore store;private final InteriorDesignService provider;private final CostEstimator costs;private final ObjectMapper json;private final Validator validator;
 public DesignService(RoomProjectRepository p,RoomImageRepository i,DesignRepository d,DesignItemRepository di,SavedDesignRepository s,FileStore store,
  DemoInteriorDesignService demo,OpenAiInteriorDesignService openAi,@Value("${app.ai-provider:demo}")String ai,CostEstimator costs,ObjectMapper json,Validator validator){
  this.projects=p;this.images=i;this.designs=d;this.items=di;this.saved=s;this.store=store;this.provider=ai.equalsIgnoreCase("openai")?openAi:demo;this.costs=costs;this.json=json;this.validator=validator;
 }
 public RoomProject ownedProject(Long id){RoomProject p=projects.findById(id).orElseThrow(()->ApiException.notFound("Project not found."));if(!p.owner.id.equals(CurrentAccount.get().id)&&!CurrentAccount.admin())throw ApiException.forbidden();return p;}
 public Design ownedDesign(Long id){Design d=designs.findById(id).orElseThrow(()->ApiException.notFound("Design not found."));if(!d.project.owner.id.equals(CurrentAccount.get().id)&&!CurrentAccount.admin())throw ApiException.forbidden();return d;}
 @Transactional public Map<String,Object> generate(String data,MultipartFile image){Requests.ProjectInput r;
  try{r=json.readValue(data,Requests.ProjectInput.class);}catch(Exception e){throw ApiException.bad("Invalid room details.");}
  var violations=validator.validate(r);if(!violations.isEmpty())throw ApiException.bad(violations.iterator().next().getPropertyPath()+": "+violations.iterator().next().getMessage());
  if(!List.of("Living Room","Bedroom","Kitchen","Dining Room","Office","Bathroom","Other").contains(r.roomType()))throw ApiException.bad("Invalid room type.");
  if(r.styles()==null||r.styles().isEmpty())throw ApiException.bad("Choose at least one interior style.");
  if(r.styles().size()>9||r.styles().stream().anyMatch(x->x.length()>50))throw ApiException.bad("Invalid style selection.");
  RoomProject p=new RoomProject();p.owner=CurrentAccount.get();p.roomType=r.roomType();p.length=r.length();p.width=r.width();p.height=r.height();p.unit=r.unit()==null?"ft":r.unit();p.doors=r.doors();p.windows=r.windows();p.existingFurniture=clip(r.existingFurniture(),1500);p.requirements=clip(r.requirements(),1500);
  p.styles=String.join(", ",r.styles());p.colors=clip(r.colors(),400);p.materials=clip(r.materials(),400);p.desiredFurniture=clip(r.desiredFurniture(),1000);p.instructions=clip(r.instructions(),2000);p.budget=r.budget();
  p.includesMaterials=!Boolean.FALSE.equals(r.includesMaterials());p.includesLabour=!Boolean.FALSE.equals(r.includesLabour());p.includesFurniture=!Boolean.FALSE.equals(r.includesFurniture());p.includesInstallation=!Boolean.FALSE.equals(r.includesInstallation());
  String filename=store.put(image);p=projects.save(p);RoomImage roomImage=new RoomImage();roomImage.project=p;roomImage.storageName=filename;roomImage.originalName=clip(image.getOriginalFilename(),200);roomImage.contentType=image.getContentType();images.save(roomImage);
  List<Design> variants=build(p,filename);return Map.of("projectId",p.id,"designs",variants.stream().map(this::summary).toList(),"demo",provider.isDemo());
 }
 private String clip(String value,int max){if(value==null)return "";return value.substring(0,Math.min(value.length(),max));}
 private List<Design> build(RoomProject p,String file){List<Design> result=new ArrayList<>();for(String v:List.of("Modern","Minimalist","Luxury")){
  InteriorDesignService.RenderedImage img=provider.render(p,store.resolve(file),v);Design d=new Design();d.project=p;d.variant=v;d.description=switch(v){case "Minimalist"->"Thoughtful essentials, uncluttered surfaces and calming materials.";case "Luxury"->"Layered finishes, feature lighting and expressive detailing.";default->"Clean lines, balanced textures and comfortable everyday living.";};d.imageUrl=img.referenceUrl();d.generatedFile=img.storedFile();d.demo=provider.isDemo();designs.save(d);
  for(var line:costs.estimate(p,v)){DesignItem it=new DesignItem();it.design=d;it.category=line.category();it.quantity=line.quantity();it.unitPrice=line.unitPrice();items.save(it);}result.add(d);
 }return result;}
 public Map<String,Object> summary(Design d){BigDecimal total=items.findByDesignIdOrderByIdAsc(d.id).stream().map(DesignItem::total).reduce(BigDecimal.ZERO,BigDecimal::add);return Map.of("id",d.id,"variant",d.variant,"imageUrl",d.generatedFile!=null?"/api/designs/"+d.id+"/image":d.imageUrl==null?"":d.imageUrl,"demo",d.demo,"estimatedCost",total);}
 @Transactional(readOnly=true) public Map<String,Object> detail(Long id){Design d=ownedDesign(id);RoomProject p=d.project;var rows=items.findByDesignIdOrderByIdAsc(id);BigDecimal total=rows.stream().map(DesignItem::total).reduce(BigDecimal.ZERO,BigDecimal::add);
  Map<String,Object> out=new LinkedHashMap<>();out.put("id",d.id);out.put("projectId",p.id);out.put("variant",d.variant);out.put("description",d.description);out.put("imageUrl",d.generatedFile==null?d.imageUrl:"/api/designs/"+d.id+"/image");out.put("demo",d.demo);out.put("project",project(p));out.put("variants",designs.findByProjectIdOrderByIdAsc(p.id).stream().map(this::summary).toList());out.put("items",rows.stream().map(i->Map.of("id",i.id,"category",i.category,"quantity",i.quantity,"unitPrice",i.unitPrice,"total",i.total())).toList());out.put("estimatedCost",total);out.put("remaining",p.budget.subtract(total));out.put("saved",saved.existsByAccountIdAndDesignId(CurrentAccount.get().id,d.id));return out;
 }
 public Map<String,Object> project(RoomProject p){Map<String,Object> m=new LinkedHashMap<>();m.put("id",p.id);m.put("roomType",p.roomType);m.put("length",p.length);m.put("width",p.width);m.put("height",p.height);m.put("unit",p.unit);m.put("doors",p.doors);m.put("windows",p.windows);m.put("existingFurniture",p.existingFurniture);m.put("requirements",p.requirements);m.put("styles",p.styles);m.put("colors",p.colors);m.put("materials",p.materials);m.put("desiredFurniture",p.desiredFurniture);m.put("instructions",p.instructions);m.put("budget",p.budget);m.put("includesMaterials",p.includesMaterials);m.put("includesLabour",p.includesLabour);m.put("includesFurniture",p.includesFurniture);m.put("includesInstallation",p.includesInstallation);m.put("createdAt",p.createdAt);m.put("imageUrl","/api/projects/"+p.id+"/image");return m;}
 @Transactional(readOnly=true) public List<Map<String,Object>> mine(){return projects.findByOwnerIdOrderByCreatedAtDesc(CurrentAccount.get().id).stream().map(p->{Map<String,Object> m=project(p);m.put("designs",designs.findByProjectIdOrderByIdAsc(p.id).stream().map(this::summary).toList());return m;}).toList();}
 @Transactional(readOnly=true) public List<Map<String,Object>> saved(){return saved.findByAccountIdOrderByCreatedAtDesc(CurrentAccount.get().id).stream().map(s->detail(s.design.id)).toList();}
 @Transactional public Map<String,Object> updateItems(Long id,List<Requests.ItemUpdate> updates){Design d=ownedDesign(id);var existing=items.findByDesignIdOrderByIdAsc(d.id);Map<Long,DesignItem> map=new HashMap<>();for(var item:existing)map.put(item.id,item);if(updates.size()>50)throw ApiException.bad("Too many items.");for(var row:updates){DesignItem it=map.get(row.id());if(it==null)throw ApiException.forbidden();if(row.quantity()==null||row.unitPrice()==null||row.quantity().signum()<0||row.unitPrice().signum()<0||row.quantity().compareTo(new BigDecimal("100000"))>0||row.unitPrice().compareTo(new BigDecimal("100000000"))>0)throw ApiException.bad("Invalid item amount.");it.quantity=row.quantity();it.unitPrice=row.unitPrice();items.save(it);}return detail(id);}
 @Transactional public void save(Long id){ownedDesign(id);if(!saved.existsByAccountIdAndDesignId(CurrentAccount.get().id,id)){SavedDesign s=new SavedDesign();s.account=CurrentAccount.get();s.design=designs.getReferenceById(id);saved.save(s);}}
 @Transactional public void unsave(Long id){ownedDesign(id);saved.findByAccountIdAndDesignId(CurrentAccount.get().id,id).ifPresent(saved::delete);}
 @Transactional public Map<String,Object> regenerate(Long projectId){RoomProject p=ownedProject(projectId);var before=designs.findByProjectIdOrderByIdAsc(p.id);String name=images.findByProjectId(p.id).orElseThrow(()->ApiException.notFound("Original image missing.")).storageName;List<Design> newer=build(p,name);for(Design d:before){saved.deleteByDesignId(d.id);items.deleteByDesignId(d.id);designs.delete(d);store.remove(d.generatedFile);}return Map.of("projectId",p.id,"designs",newer.stream().map(this::summary).toList(),"demo",provider.isDemo());}
 @Transactional public void deleteDesign(Long id){Design d=ownedDesign(id);saved.deleteByDesignId(id);items.deleteByDesignId(id);designs.delete(d);store.remove(d.generatedFile);}
 public byte[] original(Long projectId){ownedProject(projectId);return store.read(images.findByProjectId(projectId).orElseThrow(()->ApiException.notFound("Image missing.")).storageName);}
 public String originalMime(Long projectId){ownedProject(projectId);return images.findByProjectId(projectId).orElseThrow(()->ApiException.notFound("Image missing.")).contentType;}
 public byte[] generated(Long id){Design d=ownedDesign(id);if(d.generatedFile==null)throw ApiException.notFound("No generated image was stored for this demo design.");return store.read(d.generatedFile);}
}
