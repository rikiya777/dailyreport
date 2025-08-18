package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    // 一般:自分の日報のみ
    List<Report> findByEmployeeAndDeleteFlgFalseOrderByReportDateDesc(Employee employee);

    // 管理者:全日報
    List<Report> findByDeleteFlgFalseOrderByReportDateDesc();

    // 業務チェック（新規）
    boolean existsByEmployeeAndReportDateAndDeleteFlgFalse(Employee employee, LocalDate reportDate);

    // 業務チェック（更新：自分以外で同日が存在するか）
    boolean existsByEmployeeAndReportDateAndIdNotAndDeleteFlgFalse(Employee employee, LocalDate reportDate, Integer id);

    Optional<Report> findByIdAndDeleteFlgFalse(Integer id);
}