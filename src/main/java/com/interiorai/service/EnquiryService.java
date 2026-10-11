package com.interiorai.service;
import com.interiorai.dto.Requests;
import com.interiorai.entity.*;import com.interiorai.repository.*;import com.interiorai.exception.ApiException;import com.interiorai.security.CurrentAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class EnquiryService {
 private final CustomerEnquiryRepository enquiries;private final ProfessionalRepository pros;private final DesignRepository designs;
 public EnquiryService(CustomerEnquiryRepository e,ProfessionalRepository p,DesignRepository d){enquiries=e;pros=p;designs=d;}
 @Transactional public Map<String,Object> create(Requests.EnquiryInput input){ProfessionalProfile p=pros.findById(input.professionalId()).orElseThrow(()->ApiException.notFound("Professional not found."));if(p.sample)throw ApiException.bad("This is a sample profile. Choose a real professional to send an enquiry.");if(p.account!=null&&p.account.id.equals(CurrentAccount.get().id))throw ApiException.bad("You cannot contact yourself.");CustomerEnquiry e=new CustomerEnquiry();e.customer=CurrentAccount.get();e.professional=p;e.message=input.message().trim();e.phone=input.phone()==null?"":input.phone().substring(0,Math.min(30,input.phone().length()));if(input.designId()!=null){Design d=designs.findById(input.designId()).orElseThrow(()->ApiException.notFound("Design not found."));if(!d.project.owner.id.equals(e.customer.id))throw ApiException.forbidden();e.design=d;}enquiries.save(e);return view(e);}
 public Map<String,Object> view(CustomerEnquiry e){Map<String,Object> m=new LinkedHashMap<>();m.put("id",e.id);m.put("professionalId",e.professional.id);m.put("professional",e.professional.name);m.put("customer",e.customer.fullName);m.put("customerEmail",e.customer.email);m.put("phone",e.phone);m.put("message",e.message);m.put("status",e.status);m.put("createdAt",e.createdAt);m.put("designId",e.design==null?null:e.design.id);return m;}
 @Transactional(readOnly=true) public List<Map<String,Object>> mine(){return enquiries.findByCustomerIdOrderByCreatedAtDesc(CurrentAccount.get().id).stream().map(this::view).toList();}
 @Transactional(readOnly=true) public List<Map<String,Object>> forProfessional(ProfessionalProfile p){return enquiries.findByProfessionalIdOrderByCreatedAtDesc(p.id).stream().map(this::view).toList();}
 @Transactional public Map<String,Object> status(Long id,String status){CustomerEnquiry e=enquiries.findById(id).orElseThrow(()->ApiException.notFound("Enquiry not found."));if(e.professional.account==null||!e.professional.account.id.equals(CurrentAccount.get().id))throw ApiException.forbidden();if(!List.of("NEW","CONTACTED","IN_PROGRESS","CLOSED").contains(status))throw ApiException.bad("Invalid enquiry status.");e.status=status;return view(enquiries.save(e));}
}
