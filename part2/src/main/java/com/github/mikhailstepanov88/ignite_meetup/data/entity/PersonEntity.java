package com.github.mikhailstepanov88.ignite_meetup.data.entity;

import com.github.mikhailstepanov88.ignite_meetup.data.common.Gender;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class PersonEntity {
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
    @NonNull
    private final Integer age;
    @NonNull
    private final Gender gender;
    @NonNull
    private final Collection<Long> friendIds;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param firstName first name of person.
     * @param lastName  last name of person.
     * @param age       age of person.
     * @param gender    gender of person.
     * @param friendIds identifiers of person friends.
     */
    public PersonEntity(@NonNull String firstName,
                        @NonNull String lastName,
                        @NonNull Integer age,
                        @Nullable Gender gender,
                        @Nullable Collection<Long> friendIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = Optional.ofNullable(gender)
                .orElse(Gender.UNKNOWN);
        this.friendIds = Optional.ofNullable(friendIds)
                .orElse(new HashSet<>());
    }
    //</editor-fold>

    //<editor-fold desc="getters">
    @NonNull public String getFirstName() {return firstName;}
    @NonNull public String getLastName() {return lastName;}
    @NonNull public Integer getAge() {return age;}
    @NonNull public Gender getGender() {return gender;}
    @NonNull public Collection<Long> getFriendIds() {return friendIds;}
    //</editor-fold>

    //<editor-fold desc="equals and hashCode">
    @Override
    public boolean equals(@NonNull Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        PersonEntity personEntity = (PersonEntity) that;
        return Objects.equals(firstName, personEntity.firstName) &&
                Objects.equals(lastName, personEntity.lastName) &&
                Objects.equals(age, personEntity.age) &&
                gender == personEntity.gender &&
                Objects.equals(friendIds, personEntity.friendIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, age, gender, friendIds);
    }
    //</editor-fold>
}
