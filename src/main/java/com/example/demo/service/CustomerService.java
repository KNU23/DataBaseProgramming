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

    /** 전체 고객 조회 **/
    public List<CustomerDTO> getAll() {
        return customerRepository.getAll();
    }
    
    /** 고객 상세 조회 **/
    public CustomerDTO getById(Integer id) {
        return customerRepository.getById(id);
    }

    /** 고객 등록 **/
    public void insert(CustomerDTO customerDTO) {
        customerRepository.insert(customerDTO);
    }
    
    /** 고객 정보 수정 **/
    public void update(CustomerDTO customerDTO) {
        customerRepository.update(customerDTO);
    }

    /** 고객 삭제 **/
    public void delete(Integer id) {
        customerRepository.delete(id);
    }
}
