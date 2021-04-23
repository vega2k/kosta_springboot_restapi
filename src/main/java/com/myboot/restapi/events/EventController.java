package com.myboot.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.myboot.restapi.accounts.Account;
import com.myboot.restapi.accounts.CurrentUser;
import com.myboot.restapi.common.ErrorsResource;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final EventValidator eventValidator;

	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}

	// Event 수정
	@PutMapping("/{id}")
	public ResponseEntity<?> updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto,
			Errors errors, @CurrentUser Account currentUser) {
		// 1. id(pk)로 Event를 조회
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		// 2. Optional에 담겨진 Event 객체가 null 이면 404 Error 발생시킨다
		if (optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		// 3. Validation에서 제공하는 어노테이션을 사용해서 입력항목 검증에 실패하면 400 Error 발생시킨다
		if (errors.hasErrors()) {
			return badRequest(errors);
		}
		// 4. 사용자정의 Validator를 사용해서 입력항목의 검증(로직체크)에 실패하면 400 Error 발생시킨다
		this.eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
			return badRequest(errors);
		}
		// 5. Optional에 담겨진 Event 객체를 꺼낸다
		Event existingEvent = optionalEvent.get();
		
		// 5.5 Event를 등록한 사용자만 Event를 수정할 수 있다
		if((existingEvent.getManager() != null) && (!existingEvent.getManager().equals(currentUser))) {
				 return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		// 6. 수정하려는 데이터를 담고 있는 EventDto와 DB에서 읽어온 Event를 매핑한다
		this.modelMapper.map(eventDto, existingEvent);
		// 7. DB에 수정 요청을 한다
		Event savedEvent = this.eventRepository.save(existingEvent);
		// 8. 수정된 Event객체를 EventResource로 Wrapping 해서 반환한다
		EventResource eventResource = new EventResource(savedEvent);
		return ResponseEntity.ok(eventResource);
	}

	// Event id로 1개 조회
	@GetMapping("/{id}")
	public ResponseEntity<?> getEvent(@PathVariable Integer id, @CurrentUser Account currentUser) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		// Optional에 담겨진 Event 객체가 null 이냐?
		if (optionalEvent.isEmpty()) {
			// 404 error 발생시킴
			return ResponseEntity.notFound().build();
		}
		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		
		//로그인 한 상태이면 update 링크를 추가해라
		if ((event.getManager() != null) && (event.getManager().equals(currentUser))) {
			eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
		}
		return ResponseEntity.ok(eventResource);
	}

	// Event 목록
	@GetMapping
	public ResponseEntity<?> queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler,
			@CurrentUser Account account) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		// PagedModel<EntityModel<Event>> pagedModel = assembler.toModel(page);
		/*
		 * PagedResourcesAssembler<T> 의 toModel() 메서드 toModel(Page<T> page,
		 * org.springframework.hateoas.server.RepresentationModelAssembler<T,R>
		 * assembler) RepresentationModelAssembler 가 함수형 인터페이스이고 D toModel(T entity)를
		 * 재정의 할때 람다식을 사용해야 한다.\ Converts the given entity into a D, which extends
		 * RepresentationModel. Event -> EventResource Wrapping 해야 한다.
		 */
		PagedModel<RepresentationModel<EventResource>> pagedModel = assembler.toModel(page,
				event -> new EventResource(event));
		if (account != null) {
			pagedModel.add(linkTo(EventController.class).withRel("create-event"));
		}
		return ResponseEntity.ok(pagedModel);
	}

	// Event 등록
	@PostMapping
	public ResponseEntity<?> createEvent(@RequestBody @Valid EventDto eventDto, Errors errors,
			@CurrentUser Account currentUser) {
		// Validation API에서 제공하는 어노테이션을 사용해서 입력검증 체크
		if (errors.hasErrors()) {
			return badRequest(errors);
		}

		// EventValidator를 사용해서 입력항목의 로직을 체크
		eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
			return badRequest(errors);
		}

		Event event = modelMapper.map(eventDto, Event.class);

		// offline, free 값을 설정
		event.update();
		// Event의 manager변수에 Account 객체 저장
		event.setManager(currentUser);

		Event addEvent = eventRepository.save(event);

		WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(addEvent.getId());
		URI createUri = selfLinkBuilder.toUri();

		EventResource eventResource = new EventResource(addEvent);
		eventResource.add(linkTo(EventController.class).withRel("query-events"));
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		return ResponseEntity.created(createUri).body(eventResource);
	}

	private ResponseEntity<?> badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}

}
