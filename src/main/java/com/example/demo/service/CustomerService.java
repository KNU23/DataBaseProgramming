package com.example.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDTO> getAll() {
        return customerRepository.getAll();
    }
    
    public CustomerDTO getById(Integer id) {
        return customerRepository.getById(id);
    }

    public void insert(CustomerDTO customerDTO) {
        customerRepository.insert(customerDTO);
    }
    
    public void update(CustomerDTO customerDTO) {
        customerRepository.update(customerDTO);
    }

    public void delete(Integer id) {
        customerRepository.delete(id);
    }
}
