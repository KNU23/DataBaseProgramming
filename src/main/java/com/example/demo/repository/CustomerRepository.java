package com.example.demo.repository;

import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerRepository {

    private final SqlSessionTemplate sql;

    public List<CustomerDTO> getAll() {
        return sql.selectList("Customer.getAll");
    }

    public CustomerDTO getById(Integer id) {
        return sql.selectOne("Customer.getById", id);
    }
    
    public void insert(CustomerDTO customerDTO) {
        sql.insert("Customer.insert", customerDTO);
    }
    
    public void update(CustomerDTO customerDTO) {
        sql.update("Customer.update", customerDTO);
    }

    public void delete(Integer id) {
        sql.delete("Customer.delete", id);
    }
}

