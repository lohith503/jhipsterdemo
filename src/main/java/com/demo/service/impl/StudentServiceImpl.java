package com.demo.service.impl;

import com.demo.service.StudentService;
import com.demo.domain.Student;
import com.demo.repository.StudentRepository;
import com.demo.service.dto.StudentDTO;
import com.demo.service.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Student.
 */
@Service
public class StudentServiceImpl implements StudentService{

    private final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
    
    @Inject
    private StudentRepository studentRepository;

    @Inject
    private StudentMapper studentMapper;

    /**
     * Save a student.
     *
     * @param studentDTO the entity to save
     * @return the persisted entity
     */
    public StudentDTO save(StudentDTO studentDTO) {
        log.debug("Request to save Student : {}", studentDTO);
        Student student = studentMapper.studentDTOToStudent(studentDTO);
        student = studentRepository.save(student);
        StudentDTO result = studentMapper.studentToStudentDTO(student);
        return result;
    }

    /**
     *  Get all the students.
     *  
     *  @return the list of entities
     */
    public List<StudentDTO> findAll() {
        log.debug("Request to get all Students");
        List<StudentDTO> result = studentRepository.findAll().stream()
            .map(studentMapper::studentToStudentDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one student by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public StudentDTO findOne(String id) {
        log.debug("Request to get Student : {}", id);
        Student student = studentRepository.findOne(UUID.fromString(id));
        StudentDTO studentDTO = studentMapper.studentToStudentDTO(student);
        return studentDTO;
    }

    /**
     *  Delete the  student by id.
     *
     *  @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Student : {}", id);
        studentRepository.delete(UUID.fromString(id));
    }
}
