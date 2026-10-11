package com.interiorai.service;
import com.interiorai.entity.RoomProject;
import com.interiorai.exception.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import java.nio.file.*;import java.util.Base64;
@Service("openAiInteriorDesignService")
@Lazy
public class OpenAiInteriorDesignService implements InteriorDesignService {
 private final String key;private final String model;private final FileStore store;private final RestClient client;
 public OpenAiInteriorDesignService(@Value("${app.openai-api-key:}")String key,@Value("${app.ai-model:gpt-image-1.5}")String model,FileStore store){this.key=key;this.model=model;this.store=store;this.client=RestClient.builder().baseUrl("https://api.openai.com").build();}
 @Override public RenderedImage render(RoomProject p,Path original,String variant){
  if(key.isBlank())throw ApiException.bad("OPENAI_API_KEY is required when AI_PROVIDER=openai.");
  try{
   byte[] source=Files.readAllBytes(original);
   String prompt="Photorealistically redesign this exact "+p.roomType+" room in "+variant+" interior design style. Keep the original room's walls, openings, proportions and camera viewpoint. "+
    "Dimensions: "+p.length+" by "+p.width+" by "+p.height+" "+p.unit+". Style notes: "+p.styles+". Colors: "+p.colors+". Materials: "+p.materials+". Furniture: "+p.desiredFurniture+". Additional requests: "+p.instructions+". "+
    "Create a plausible functional residential interior, realistic materials and soft daylight. Do not overlay labels or text.";
   LinkedMultiValueMap<String,Object> form=new LinkedMultiValueMap<>();form.add("model",model);form.add("prompt",prompt);form.add("size","1024x1024");
   form.add("image",new ByteArrayResource(source){@Override public String getFilename(){return original.getFileName().toString();}});
   JsonNode reply=client.post().uri("/v1/images/edits").header(HttpHeaders.AUTHORIZATION,"Bearer "+key).contentType(MediaType.MULTIPART_FORM_DATA).body(form).retrieve().body(JsonNode.class);
   String b64=reply==null?null:reply.path("data").path(0).path("b64_json").asText(null);
   if(b64==null||b64.isBlank())throw new IllegalStateException("Provider did not return image data");
   return new RenderedImage(null,store.putGenerated(Base64.getDecoder().decode(b64)));
  }catch(Exception e){throw new ApiException(HttpStatus.BAD_GATEWAY,"Image provider failed. Your previous projects remain saved. Check your AI configuration and retry.");}
 }
 @Override public boolean isDemo(){return false;}
}
