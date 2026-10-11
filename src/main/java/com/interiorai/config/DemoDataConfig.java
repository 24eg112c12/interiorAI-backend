package com.interiorai.config;
import com.interiorai.entity.*;import com.interiorai.repository.*;
import org.springframework.context.annotation.*;import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;import java.util.*;
@Configuration
public class DemoDataConfig {
 @Bean org.springframework.boot.CommandLineRunner seed(ProfessionalRepository pros,ServiceCategoryRepository categories,AccountRepository users,PasswordEncoder encoder,
 @Value("${app.admin-email:}")String email,@Value("${app.admin-password:}")String password){return args->{
  for(String n:List.of("Interior Designers","Carpenters","Electricians","Painters","False Ceiling Workers","Sofa Makers","Lighting Specialists","Flooring Workers","Curtain Installers","Plumbers"))if(categories.findAll().stream().noneMatch(x->x.name.equals(n))){ServiceCategory c=new ServiceCategory();c.name=n;categories.save(c);}
  if(pros.count()==0){
   sample(pros,"Aaya Design Studio (Sample)","Interior Designers","Hyderabad",7,"Warm, approachable interior design concepts for everyday spaces.",new BigDecimal("25000"),"https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=400&q=80");
   sample(pros,"Craft & Grain (Sample)","Carpenters","Bengaluru",11,"Custom cabinetry, modular shelves and wooden furniture inspiration.",new BigDecimal("12000"),"https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=400&q=80");
   sample(pros,"Lumen Atelier (Sample)","Lighting Specialists","Mumbai",5,"Decorative and architectural lighting concepts.",new BigDecimal("8500"),"https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=400&q=80");
   sample(pros,"Terra Finishes (Sample)","Painters","Delhi",8,"Color and decorative finish inspiration.",new BigDecimal("7000"),"https://images.unsplash.com/photo-1600607688969-a5bfcd646154?auto=format&fit=crop&w=400&q=80");
  }
  if(!email.isBlank()&&!password.isBlank()&&password.length()>=12&&users.findByEmailIgnoreCase(email).isEmpty()){Account a=new Account();a.email=email.trim().toLowerCase();a.fullName="InteriorAI Administrator";a.passwordHash=encoder.encode(password);a.role=Account.Role.ADMIN;users.save(a);}
 };}
 private static void sample(ProfessionalRepository repo,String name,String profession,String city,int years,String about,BigDecimal price,String photo){ProfessionalProfile p=new ProfessionalProfile();p.sample=true;p.name=name;p.profession=profession;p.city=city;p.serviceArea=city;p.experienceYears=years;p.about=about;p.services=profession;p.startingPrice=price;p.photoUrl=photo;p.phone="";p.verified=false;repo.save(p);}
}
