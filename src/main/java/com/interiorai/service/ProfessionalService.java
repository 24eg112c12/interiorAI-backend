package com.interiorai.service;
import com.interiorai.dto.Requests;
import com.interiorai.entity.*;import com.interiorai.exception.ApiException;
import com.interiorai.repository.*;import com.interiorai.security.CurrentAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import java.math.*;import java.util.*;
@Service
public class ProfessionalService {
 private final ProfessionalRepository profiles;private final PortfolioRepository photos;private final ReviewRepository reviews;private final FileStore store;private final AuthService auth;
 public ProfessionalService(ProfessionalRepository p,PortfolioRepository imgs,ReviewRepository reviews,FileStore store,AuthService auth){this.profiles=p;this.photos=imgs;this.reviews=reviews;this.store=store;this.auth=auth;}
 public ProfessionalProfile get(Long id){return profiles.findById(id).orElseThrow(()->ApiException.notFound("Professional not found."));}
 public ProfessionalProfile mine(){if(CurrentAccount.get().role!=Account.Role.PROFESSIONAL)throw ApiException.forbidden();return profiles.findByAccountId(CurrentAccount.get().id).orElseThrow(()->ApiException.notFound("Complete your professional profile first."));}
 @Transactional(readOnly=true) public List<Map<String,Object>> search(String profession,String city,Integer minExperience,BigDecimal maxPrice,Double minRating){return profiles.findAllByOrderByIdDesc().stream()
  .filter(p->profession==null||profession.isBlank()||p.profession.toLowerCase().contains(profession.toLowerCase()))
  .filter(p->city==null||city.isBlank()||(p.city!=null&&p.city.toLowerCase().contains(city.toLowerCase()))||(p.serviceArea!=null&&p.serviceArea.toLowerCase().contains(city.toLowerCase())))
  .filter(p->minExperience==null||p.experienceYears>=minExperience)
  .filter(p->maxPrice==null||p.startingPrice.compareTo(maxPrice)<=0)
  .filter(p->minRating==null||stats(p.id).rating>=minRating)
  .limit(100).map(this::asMap).toList();}
 record Rating(double rating,int count) {}
 private Rating stats(Long id){var found=reviews.findByProfessionalId(id);return found.isEmpty()?new Rating(0,0):new Rating(Math.round(found.stream().mapToInt(r->r.rating).average().orElse(0)*10)/10.0,found.size());}
 public Map<String,Object> asMap(ProfessionalProfile p){Rating rating=stats(p.id);Map<String,Object> m=new LinkedHashMap<>();m.put("id",p.id);m.put("name",p.name);m.put("profession",p.profession);m.put("experienceYears",p.experienceYears);m.put("city",p.city);m.put("serviceArea",p.serviceArea);m.put("phone",p.phone);m.put("about",p.about);m.put("services",p.services);m.put("startingPrice",p.startingPrice);m.put("photoUrl",p.photoUrl);m.put("verified",p.verified);m.put("sample",p.sample);m.put("rating",rating.rating);m.put("reviewCount",rating.count);m.put("portfolio",photos.findByProfessionalId(p.id).stream().map(x->"/api/portfolio/"+x.storageName).toList());m.put("reviews",reviews.findByProfessionalId(p.id).stream().map(r->Map.of("id",r.id,"author",r.author.fullName,"rating",r.rating,"comment",r.comment==null?"":r.comment)).toList());return m;}
 private void apply(ProfessionalProfile p,Requests.ProfileInput r){p.name=r.name().trim();p.profession=r.profession().trim();p.city=r.city().trim();p.serviceArea=limit(r.serviceArea(),170);p.phone=limit(r.phone(),30);p.experienceYears=r.experienceYears();p.about=limit(r.about(),2500);p.services=limit(r.services(),900);p.startingPrice=r.startingPrice();p.photoUrl=limit(r.photoUrl(),500);}
 private String limit(String s,int n){return s==null?"":s.substring(0,Math.min(s.length(),n));}
 @Transactional public Map<String,Object> register(Requests.ProfessionalRegistration r){Account user=auth.register(r.account(),Account.Role.PROFESSIONAL);ProfessionalProfile p=new ProfessionalProfile();p.account=user;apply(p,r.profile());profiles.save(p);return auth.issue(user);}
 @Transactional public Map<String,Object> update(Requests.ProfileInput r){ProfessionalProfile p=mine();apply(p,r);profiles.save(p);return asMap(p);}
 @Transactional public Map<String,Object> upload(MultipartFile image){ProfessionalProfile p=mine();if(photos.findByProfessionalId(p.id).size()>=12)throw ApiException.bad("Maximum 12 portfolio photos.");String file=store.put(image);PortfolioImage pi=new PortfolioImage();pi.professional=p;pi.storageName=file;photos.save(pi);return asMap(p);}
 public byte[] portfolio(String file){photos.findByStorageName(file).orElseThrow(()->ApiException.notFound("Portfolio image not found."));return store.read(file);}
 @Transactional public void review(Requests.ReviewInput input){ProfessionalProfile pro=get(input.professionalId());if(pro.sample)throw ApiException.bad("Demo professionals cannot receive real reviews.");if(pro.account!=null&&pro.account.id.equals(CurrentAccount.get().id))throw ApiException.bad("You cannot review yourself.");if(reviews.findByAuthorIdAndProfessionalId(CurrentAccount.get().id,pro.id).isPresent())throw new ApiException(HttpStatus.CONFLICT,"You already reviewed this professional.");Review r=new Review();r.author=CurrentAccount.get();r.professional=pro;r.rating=input.rating();r.comment=input.comment();reviews.save(r);}
}
