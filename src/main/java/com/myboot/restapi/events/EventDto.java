package com.myboot.restapi.events;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

	@NotBlank
	private String name;
	@NotEmpty
	private String description;
	
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime beginEnrollmentDateTime;
	
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime closeEnrollmentDateTime;
	
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime beginEventDateTime;
	
	
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endEventDateTime;
	
	private String location;
	
	@Min(value=0, message = "basePrice는 0보다 큰 값이어야 합니다.")
	private int basePrice;
	@Min(0)
	private int maxPrice;
	@Min(0)
	private int limitOfEnrollment;
}