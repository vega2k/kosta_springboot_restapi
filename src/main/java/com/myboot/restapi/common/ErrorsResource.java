package com.myboot.restapi.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.myboot.restapi.index.IndexController;

public class ErrorsResource extends EntityModel<Errors> {
	@JsonUnwrapped
	private Errors errors;

	public ErrorsResource(Errors content) {
		this.errors = content;
		add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
	}
}