package com.example.demo.repository;

import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.dto.DashboardDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final SqlSessionTemplate sql;

    public int countUsers() { return sql.selectOne("Admin.countUsers"); }
    public int countBooks() { return sql.selectOne("Admin.countBooks"); }
    public int countOrders() { return sql.selectOne("Admin.countOrders"); }
    public long totalRevenue() { return sql.selectOne("Admin.totalRevenue"); }

    public List<DashboardDTO> getDailySales() {
        return sql.selectList("Admin.getDailySales");
    }

    public List<DashboardDTO> getTopSellingBooks() {
        return sql.selectList("Admin.getTopSellingBooks");
    }
}
