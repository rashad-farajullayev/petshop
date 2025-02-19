package com.thesniffers.application;

import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.random.RandomGenerator;

@RestController
@RequestMapping("/")
public class HomeController {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> index() {
        var customer = new Customer();
        var rnd = Random.from(RandomGenerator.getDefault()).nextInt();
        customer.setName("John Doe " + rnd);
        customer.setOwner("cust" + rnd);
        customer.setTimezone("Europe/Paris");

        customerRepository.save(customer);
        return ResponseEntity.ok("Welcome to Pet Clinic");
    }
}
