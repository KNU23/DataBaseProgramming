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

    /** 총 회원 수 조회 */
    public int countUsers() { return sql.selectOne("Admin.countUsers"); }

    /** 총 도서 수 조회 */
    public int countBooks() { return sql.selectOne("Admin.countBooks"); }

    /** 총 주문 건수 조회 (주문완료 상태만) */
    public int countOrders() { return sql.selectOne("Admin.countOrders"); }

    /** 총 매출액 조회 */
    public long totalRevenue() { return sql.selectOne("Admin.totalRevenue"); }

    /** 일별 매출 통계 조회(관리자 대쉬보드) **/
    public List<DashboardDTO> getDailySales() {
        return sql.selectList("Admin.getDailySales");
    }

    /** 베스트셀러 조회(관리자 대쉬보드) **/
    public List<DashboardDTO> getTopSellingBooks() {
        return sql.selectList("Admin.getTopSellingBooks");
    }
}