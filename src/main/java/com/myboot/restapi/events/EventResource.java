package com.myboot.restapi.events;

import org.springframework.hateoas.RepresentationModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class EventResource extends RepresentationModel<EventResource> {
	@JsonUnwrapped
	private Event event;

	public EventResource(Event event) {
		this.event = event;
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}

	public Event getEvent() {
		return event;
	}
}