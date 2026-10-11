package com.interiorai.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
public final class Requests {
 private Requests() {}
 public record Register(@NotBlank String fullName,@Email @NotBlank String email,@Size(min=8,max=100) String password) {}
 public record Login(@Email @NotBlank String email,@NotBlank String password) {}
 public record AccountUpdate(@NotBlank @Size(max=120) String fullName,@Email @NotBlank String email) {}
 public record PasswordUpdate(@NotBlank String oldPassword,@Size(min=8,max=100) String newPassword) {}
 public record ProjectInput(
  @NotBlank String roomType,
  @NotNull @DecimalMin("0.1") @DecimalMax("10000") BigDecimal length,
  @NotNull @DecimalMin("0.1") @DecimalMax("10000") BigDecimal width,
  @NotNull @DecimalMin("0.1") @DecimalMax("100") BigDecimal height,
  @Pattern(regexp="ft|m") String unit,
  @Min(0) @Max(100) int doors,@Min(0) @Max(100) int windows,
  String existingFurniture,String requirements,List<String> styles,String colors,String materials,
  String desiredFurniture,String instructions,
  @NotNull @DecimalMin("1000") @DecimalMax("1000000000") BigDecimal budget,
  Boolean includesMaterials,Boolean includesLabour,Boolean includesFurniture,Boolean includesInstallation) {}
 public record ItemUpdate(@NotNull Long id,@NotNull @DecimalMin("0") @DecimalMax("100000") BigDecimal quantity,@NotNull @DecimalMin("0") @DecimalMax("100000000") BigDecimal unitPrice) {}
 public record ProfileInput(@NotBlank String name,@NotBlank String profession,@NotBlank String city,String serviceArea,String phone,@Min(0) @Max(80) int experienceYears,String about,String services,@NotNull @DecimalMin("0") BigDecimal startingPrice,String photoUrl) {}
 public record ProfessionalRegistration(@Valid @NotNull Register account,@Valid @NotNull ProfileInput profile) {}
 public record EnquiryInput(@NotNull Long professionalId,Long designId,@NotBlank @Size(max=2000) String message,String phone) {}
 public record EnquiryStatus(@Pattern(regexp="NEW|CONTACTED|IN_PROGRESS|CLOSED") String status) {}
 public record ReviewInput(@NotNull Long professionalId,@Min(1) @Max(5) int rating,@Size(max=1000) String comment) {}
 public record CategoryInput(@NotBlank String name) {}
}
