package com.demo.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


/**
 * A DTO for the Student entity.
 */
public class StudentDTO implements Serializable {

    private UUID id;

    private String first_name;

    private String registration_number;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StudentDTO studentDTO = (StudentDTO) o;

        if ( ! Objects.equals(id, studentDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
            "id=" + id +
            ", first_name='" + first_name + "'" +
            ", registration_number='" + registration_number + "'" +
            '}';
    }
}
