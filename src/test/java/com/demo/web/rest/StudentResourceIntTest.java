package com.demo.web.rest;

import com.demo.AbstractCassandraTest;
import com.demo.JhipsterDemoApp;

import com.demo.domain.Student;
import com.demo.repository.StudentRepository;
import com.demo.service.StudentService;
import com.demo.service.dto.StudentDTO;
import com.demo.service.mapper.StudentMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StudentResource REST controller.
 *
 * @see StudentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterDemoApp.class)
public class StudentResourceIntTest extends AbstractCassandraTest {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBBBBBBB";

    @Inject
    private StudentRepository studentRepository;

    @Inject
    private StudentMapper studentMapper;

    @Inject
    private StudentService studentService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStudentMockMvc;

    private Student student;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StudentResource studentResource = new StudentResource();
        ReflectionTestUtils.setField(studentResource, "studentService", studentService);
        this.restStudentMockMvc = MockMvcBuilders.standaloneSetup(studentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Student createEntity() {
        Student student = new Student()
                .first_name(DEFAULT_FIRST_NAME)
                .registration_number(DEFAULT_REGISTRATION_NUMBER);
        return student;
    }

    @Before
    public void initTest() {
        studentRepository.deleteAll();
        student = createEntity();
    }

    @Test
    public void createStudent() throws Exception {
        int databaseSizeBeforeCreate = studentRepository.findAll().size();

        // Create the Student
        StudentDTO studentDTO = studentMapper.studentToStudentDTO(student);

        restStudentMockMvc.perform(post("/api/students")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
                .andExpect(status().isCreated());

        // Validate the Student in the database
        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(databaseSizeBeforeCreate + 1);
        Student testStudent = students.get(students.size() - 1);
        assertThat(testStudent.getFirst_name()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testStudent.getRegistration_number()).isEqualTo(DEFAULT_REGISTRATION_NUMBER);
    }

    @Test
    public void getAllStudents() throws Exception {
        // Initialize the database
        studentRepository.save(student);

        // Get all the students
        restStudentMockMvc.perform(get("/api/students?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(student.getId().toString())))
                .andExpect(jsonPath("$.[*].first_name").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].registration_number").value(hasItem(DEFAULT_REGISTRATION_NUMBER.toString())));
    }

    @Test
    public void getStudent() throws Exception {
        // Initialize the database
        studentRepository.save(student);

        // Get the student
        restStudentMockMvc.perform(get("/api/students/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(student.getId().toString()))
            .andExpect(jsonPath("$.first_name").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.registration_number").value(DEFAULT_REGISTRATION_NUMBER.toString()));
    }

    @Test
    public void getNonExistingStudent() throws Exception {
        // Get the student
        restStudentMockMvc.perform(get("/api/students/{id}", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateStudent() throws Exception {
        // Initialize the database
        studentRepository.save(student);
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();

        // Update the student
        Student updatedStudent = studentRepository.findOne(student.getId());
        updatedStudent
                .first_name(UPDATED_FIRST_NAME)
                .registration_number(UPDATED_REGISTRATION_NUMBER);
        StudentDTO studentDTO = studentMapper.studentToStudentDTO(updatedStudent);

        restStudentMockMvc.perform(put("/api/students")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
                .andExpect(status().isOk());

        // Validate the Student in the database
        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(databaseSizeBeforeUpdate);
        Student testStudent = students.get(students.size() - 1);
        assertThat(testStudent.getFirst_name()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStudent.getRegistration_number()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
    }

    @Test
    public void deleteStudent() throws Exception {
        // Initialize the database
        studentRepository.save(student);
        int databaseSizeBeforeDelete = studentRepository.findAll().size();

        // Get the student
        restStudentMockMvc.perform(delete("/api/students/{id}", student.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(databaseSizeBeforeDelete - 1);
    }
}
