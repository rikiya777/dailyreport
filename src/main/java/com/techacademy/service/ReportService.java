package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧取得
    public List<Report> findAll(Employee employee, boolean isAdmin) {
        if (isAdmin) {
            return reportRepository.findByDeleteFlgFalseOrderByReportDateDesc();
        } else {
            return reportRepository.findByEmployeeAndDeleteFlgFalseOrderByReportDateDesc(employee);
        }
    }

    // 詳細取得
    public Report findById(Integer id) {
        Optional<Report> opt = reportRepository.findByIdAndDeleteFlgFalse(id);
        return opt.orElse(null);
    }

    // 新規登録
    @Transactional
    public boolean save(Report report, Employee employee) {
        // 業務チェック：同日重複
        if (reportRepository.existsByEmployeeAndReportDateAndDeleteFlgFalse(employee, report.getReportDate())) {
            return false;
        }
        report.setEmployee(employee);
        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        reportRepository.save(report);
        return true;
    }

    // 更新
    @Transactional
    public boolean update(Integer id, Report formReport, Employee employee) {
        Report report = findById(id);
        if (report == null) return false;

        // 業務チェック：自分以外で同日重複
        if (reportRepository.existsByEmployeeAndReportDateAndIdNotAndDeleteFlgFalse(employee, formReport.getReportDate(), id)) {
            return false;
        }

        report.setReportDate(formReport.getReportDate());
        report.setTitle(formReport.getTitle());
        report.setContent(formReport.getContent());
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
        return true;
    }

    // 論理削除
    @Transactional
    public void delete(Integer id) {
        Report report = findById(id);
        if (report != null) {
            report.setDeleteFlg(true);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
        }
    }public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployeeAndDeleteFlgFalseOrderByReportDateDesc(employee);
    }
}