package ru.job4j.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Employee;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.EmployeeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private RestTemplate rest;
    private static final String ACC_API = "http://localhost:8080/person/";
    private static final String ACC_API_ID = "http://localhost:8080/person/{id}";
    private final EmployeeRepository employeeRep;

    public EmployeeController(EmployeeRepository employeeRep) {
        this.employeeRep = employeeRep;
    }

    @GetMapping("/")
    public List<Employee> findAll() {
        return StreamSupport.stream(this.employeeRep.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable int id) {
        var employee = employeeRep.findById(id);
        return new ResponseEntity<Employee>(employee.orElse(new Employee()),
                employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return new ResponseEntity<Employee>(employeeRep.save(employee), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Employee employee) {
        employeeRep.save(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Employee employee = new Employee();
        employee.setId(id);
        employeeRep.delete(employee);
        return ResponseEntity.ok().build();
    }




    @GetMapping("/accounts/{idEml}")
    public ResponseEntity<Set<Person>> employeeAccounts(@PathVariable int idEml) {
        var employee = this.employeeRep.findById(idEml);
        Set<Person> rsl = employee.isPresent() ? employee.get().getAccounts() : new HashSet<>();
        return new ResponseEntity<>(rsl, employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/account/{idEml}")
    public ResponseEntity<Person> addAccount(@RequestBody Person person, @PathVariable int idEml) {
        Employee employee = employeeRep.findById(idEml).get();
        employee.addPerson(person);
        this.update(employee);
        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }

    @PutMapping("/account/")
    public ResponseEntity<Void> updateAccount(@RequestBody Person person) {
        rest.put(ACC_API, person);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{id},{idEml}")
    public ResponseEntity<Person> findAccountById(@PathVariable int id, @PathVariable int idEml) {
        Person person = null;
        Set<Person> accounts = this.employeeRep.findById(idEml).get().getAccounts();
        if (accounts != null) {
            for (Person p : accounts) {
                if (p.getId() == id) {
                    person = p;
                }
            }
        }
        return new ResponseEntity<>(person, person != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/account/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
        rest.delete(ACC_API_ID, id);
        return ResponseEntity.ok().build();
    }
}
