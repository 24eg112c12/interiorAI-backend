package com.interiorai;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
@SpringBootTest @AutoConfigureMockMvc
class ApiIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;
 private String register(String email)throws Exception{
  String result=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
   .content(mapper.writeValueAsString(Map.of("fullName","Integration Test","email",email,"password","very-secure-example-password"))))
   .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
  return mapper.readTree(result).path("token").asText();
 }
 @Test void anonymousAccessAndAuthorization()throws Exception{
  mvc.perform(get("/api/designs/my-projects")).andExpect(status().isUnauthorized());
  String token=register("test-auth-"+UUID.randomUUID()+"@example.com");
  mvc.perform(get("/api/designs/my-projects").header("Authorization","Bearer "+token)).andExpect(status().isOk());
  mvc.perform(get("/api/admin/users").header("Authorization","Bearer "+token)).andExpect(status().isForbidden());
  mvc.perform(get("/api/professionals")).andExpect(status().isOk());
  mvc.perform(post("/api/auth/logout").header("Authorization","Bearer "+token)).andExpect(status().isOk());
  mvc.perform(get("/api/designs/my-projects").header("Authorization","Bearer "+token)).andExpect(status().isUnauthorized());
 }
 @Test void rejectsBadDimensionsAndFakeImage()throws Exception{
  String token=register("test-upload-"+UUID.randomUUID()+"@example.com");
  String payload="{\"roomType\":\"Living Room\",\"length\":-2,\"width\":10,\"height\":9,\"unit\":\"ft\",\"doors\":1,\"windows\":2,\"styles\":[\"Modern\"],\"budget\":200000}";
  MockMultipartFile data=new MockMultipartFile("data","","text/plain",payload.getBytes());
  MockMultipartFile fake=new MockMultipartFile("image","room.png","image/png","not a real png".getBytes());
  mvc.perform(multipart("/api/designs/generate").file(data).file(fake).header("Authorization","Bearer "+token)).andExpect(status().isBadRequest());
 }
 @Test void sampleEnquiryCannotBeMistakenForRealBusiness()throws Exception{
  String token=register("test-enquiry-"+UUID.randomUUID()+"@example.com");
  String result=mvc.perform(get("/api/professionals")).andReturn().getResponse().getContentAsString();
  long sampleId=mapper.readTree(result).findValues("id").get(0).asLong();
  mvc.perform(post("/api/enquiries").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
   .content(mapper.writeValueAsString(Map.of("professionalId",sampleId,"message","Please provide a quotation")))).andExpect(status().isBadRequest());
 }
}
