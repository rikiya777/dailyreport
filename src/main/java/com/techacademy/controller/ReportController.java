package com.techacademy.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import com.techacademy.service.ReportService;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeService;

    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報一覧
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        Employee employee = userDetail.getEmployee();
        boolean isAdmin = employee.getRole() == Employee.Role.ADMIN;
        model.addAttribute("reports", reportService.findAll(employee, isAdmin));
        model.addAttribute("listSize", reportService.findAll(employee, isAdmin).size());
        return "reports/list";
    }

    // 詳細画面
    @GetMapping("/{id}/detail")
    public String detail(@PathVariable Integer id, Model model) {
        Report report = reportService.findById(id);
        if (report == null) return "redirect:/reports";
        model.addAttribute("report", report);
        return "reports/detail";
    }

    // 新規登録画面
    @GetMapping("/add")
    public String create(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        model.addAttribute("report", new Report());
        model.addAttribute("employee", userDetail.getEmployee());
        return "reports/add";
    }

    // 新規登録処理
    @PostMapping("/add")
    public String add(@AuthenticationPrincipal UserDetail userDetail, @Validated Report report, BindingResult res, Model model) {
        Employee employee = userDetail.getEmployee();

        if (res.hasErrors()) {
            model.addAttribute("employee", employee);
            model.addAttribute("report", report);  // 追加
            return "reports/add";
        }

        if (!reportService.save(report, employee)) {
            model.addAttribute("dateError", "既に登録されている日付です");
            model.addAttribute("employee", employee);
            model.addAttribute("report", report);  // 追加
            return "reports/add";
        }

        return "redirect:/reports";
    }

    // 更新画面
    @GetMapping("/{id}/update")
    public String edit(@PathVariable Integer id, Model model) {
        Report report = reportService.findById(id);
        if (report == null) return "redirect:/reports";
        model.addAttribute("report", report);
        return "reports/update";
    }

    // 更新処理
    @PostMapping("/{id}/update")
    public String update(@AuthenticationPrincipal UserDetail userDetail,
                         @PathVariable Integer id,
                         @Validated Report report,
                         BindingResult res,
                         Model model) {

        Report existingReport = reportService.findById(id);
        if (existingReport == null) return "redirect:/reports";

        // エラーがある場合は既存の社員情報を使って表示
        if (res.hasErrors()) {
            report.setEmployee(existingReport.getEmployee()); // ← ここが重要
            model.addAttribute("report", report);
            return "reports/update";
        }

        // 更新チェック（同日重複など）
        if (!reportService.update(id, report, existingReport.getEmployee())) {
            model.addAttribute("dateError", "既に登録されている日付です");
            report.setEmployee(existingReport.getEmployee());
            model.addAttribute("report", report);
            return "reports/update";
        }

        return "redirect:/reports";
    }

    // 削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        reportService.delete(id);
        return "redirect:/reports";
    }
}