package com.demo.repository;

import com.demo.domain.Student;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cassandra repository for the Student entity.
 */
@Repository
public class StudentRepository {

    @Inject
    private Session session;

    private Mapper<Student> mapper;

    private PreparedStatement findAllStmt;

    private PreparedStatement truncateStmt;

    @PostConstruct
    public void init() {
        mapper = new MappingManager(session).mapper(Student.class);
        findAllStmt = session.prepare("SELECT * FROM student");
        truncateStmt = session.prepare("TRUNCATE student");
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        BoundStatement stmt =  findAllStmt.bind();
        session.execute(stmt).all().stream().map(
            row -> {
                Student student = new Student();
                student.setId(row.getUUID("id"));
                student.setFirst_name(row.getString("first_name"));
                student.setRegistration_number(row.getString("registration_number"));
                return student;
            }
        ).forEach(students::add);
        return students;
    }

    public Student findOne(UUID id) {
        return mapper.get(id);
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(UUID.randomUUID());
        }
        mapper.save(student);
        return student;
    }

    public void delete(UUID id) {
        mapper.delete(id);
    }

    public void deleteAll() {
        BoundStatement stmt =  truncateStmt.bind();
        session.execute(stmt);
    }
}
