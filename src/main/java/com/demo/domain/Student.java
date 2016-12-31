package com.demo.domain;

import com.datastax.driver.mapping.annotations.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A Student.
 */

@Table(name = "student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @PartitionKey
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

    public Student first_name(String first_name) {
        this.first_name = first_name;
        return this;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public Student registration_number(String registration_number) {
        this.registration_number = registration_number;
        return this;
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
        Student student = (Student) o;
        if(student.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Student{" +
            "id=" + id +
            ", first_name='" + first_name + "'" +
            ", registration_number='" + registration_number + "'" +
            '}';
    }
}
