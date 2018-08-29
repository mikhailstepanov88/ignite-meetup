package com.github.mikhailstepanov88.ignite_meetup.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.mikhailstepanov88.ignite_meetup.data.common.Gender;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class PersonDTO {
    @Nullable
    private final Long id;
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
    @NonNull
    private final Integer age;
    @NonNull
    private final Gender gender;
    @NonNull
    private final Collection<PersonDTO> friends;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param id        identifier of person.
     * @param firstName first name of person.
     * @param lastName  last name of person.
     * @param age       age of person.
     * @param gender    gender of person.
     * @param friends   friends of person.
     */
    @JsonCreator
    public PersonDTO(@Nullable Long id,
                     @NonNull String firstName,
                     @NonNull String lastName,
                     @NonNull Integer age,
                     @Nullable Gender gender,
                     @Nullable Collection<PersonDTO> friends) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = Optional.ofNullable(gender)
                .orElse(Gender.UNKNOWN);
        this.friends = Optional.ofNullable(friends)
                .orElse(new HashSet<>());
    }
    //</editor-fold>

    //<editor-fold desc="getters">
    @Nullable public Long getId() {return id;}
    @NonNull public Integer getAge() {return age;}
    @NonNull public Gender getGender() {return gender;}
    @NonNull public String getLastName() {return lastName;}
    @NonNull public String getFirstName() {return firstName;}
    @NonNull public Collection<PersonDTO> getFriends() {return friends;}
    //</editor-fold>

    //<editor-fold desc="equals and hashCode">
    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) that;
        return Objects.equals(id, personDTO.id) &&
                Objects.equals(firstName, personDTO.firstName) &&
                Objects.equals(lastName, personDTO.lastName) &&
                Objects.equals(age, personDTO.age) &&
                gender == personDTO.gender &&
                Objects.equals(friends, personDTO.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, gender, friends);
    }
    //</editor-fold>
}
