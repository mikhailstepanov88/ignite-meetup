package com.github.mikhailstepanov88.ignite_meetup.converter;

import com.github.mikhailstepanov88.ignite_meetup.data.dto.PersonDTO;
import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import org.springframework.stereotype.Component;
import reactor.util.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PersonConverter {
    /**
     * Convert person entity to person data transfer object.
     *
     * @param id      identifier of person entity for convert.
     * @param person  person entity for convert.
     * @param friends person friends entities for convert.
     * @return converted person data transfer object.
     */
    @NonNull
    public PersonDTO entityToDTO(long id,
                                 @NonNull PersonEntity person,
                                 @NonNull Map<Long, PersonEntity> friends) {
        Collection<PersonDTO> friendsDTO = friends.entrySet().stream()
                .map(it -> entityToDTO(it.getKey(), it.getValue(), new HashMap<>()))
                .collect(Collectors.toSet());
        return new PersonDTO(id, person.getFirstName(), person.getLastName(),
                person.getAge(), person.getGender(), friendsDTO);
    }

    /**
     * Convert person entity to person data transfer object.
     *
     * @param id      identifier of person entity for convert.
     * @param person  person entity for convert.
     * @return converted person data transfer object.
     */
    @NonNull
    public PersonDTO entityToDTO(long id, @NonNull PersonEntity person) {
        return new PersonDTO(id, person.getFirstName(), person.getLastName(),
                person.getAge(), person.getGender(), new HashSet<>());
    }

    /**
     * Convert person data transfer object to person entity.
     *
     * @param dto person data transfer object for convert.
     * @return converted person entity.
     */
    @NonNull
    public PersonEntity dtoToEntity(@NonNull PersonDTO dto) {
        Collection<Long> friendIds = dto.getFriends().stream()
                .map(PersonDTO::getId)
                .collect(Collectors.toSet());
        return new PersonEntity(dto.getFirstName(), dto.getLastName(),
                dto.getAge(), dto.getGender(), friendIds);
    }
}