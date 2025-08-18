package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReportService reportService; // ← 追加

    public EmployeeService(EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            ReportService reportService) {
this.employeeRepository = employeeRepository;
this.passwordEncoder = passwordEncoder;
this.reportService = reportService;
}

// 従業員保存
@Transactional
public ErrorKinds save(Employee employee) {

// パスワードチェック
ErrorKinds result = employeePasswordCheck(employee);
if (ErrorKinds.CHECK_OK != result) {
return result;
}

// 従業員番号重複チェック
if (findByCode(employee.getCode()) != null) {
return ErrorKinds.DUPLICATE_ERROR;
}

employee.setDeleteFlg(false);
LocalDateTime now = LocalDateTime.now();
employee.setCreatedAt(now);
employee.setUpdatedAt(now);

employeeRepository.save(employee);
return ErrorKinds.SUCCESS;
}

// 従業員削除
@Transactional
public ErrorKinds delete(String code, UserDetail userDetail) {

// 自分自身を削除しようとした場合はエラー
if (code.equals(userDetail.getEmployee().getCode())) {
return ErrorKinds.LOGINCHECK_ERROR;
}

// 対象従業員を取得
Employee employee = findByCode(code);
if (employee == null) {
return ErrorKinds.NOTFOUND_ERROR;
}

// 紐づく日報情報の削除
List<Report> reportList = reportService.findByEmployee(employee);
for (Report report : reportList) {
reportService.delete(report.getId());
}

// 従業員の論理削除
LocalDateTime now = LocalDateTime.now();
employee.setUpdatedAt(now);
employee.setDeleteFlg(true);

return ErrorKinds.SUCCESS;
}

// 従業員一覧表示処理
public List<Employee> findAll() {
return employeeRepository.findAll();
}

// 1件を検索
public Employee findByCode(String code) {
Optional<Employee> option = employeeRepository.findById(code);
return option.orElse(null);
}

// 従業員パスワードチェック
private ErrorKinds employeePasswordCheck(Employee employee) {

// 半角英数字チェック
if (isHalfSizeCheckError(employee)) {
return ErrorKinds.HALFSIZE_ERROR;
}

// 文字数チェック
if (isOutOfRangePassword(employee)) {
return ErrorKinds.RANGECHECK_ERROR;
}

employee.setPassword(passwordEncoder.encode(employee.getPassword()));
return ErrorKinds.CHECK_OK;
}

// 半角英数字チェック
private boolean isHalfSizeCheckError(Employee employee) {
Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
Matcher matcher = pattern.matcher(employee.getPassword());
return !matcher.matches();
}

// パスワード文字数チェック
public boolean isOutOfRangePassword(Employee employee) {
int passwordLength = employee.getPassword().length();
return passwordLength < 8 || 16 < passwordLength;
}

// 従業員更新
@Transactional
public ErrorKinds update(String code, Employee formEmployee) {
Employee employee = findByCode(code);
if (employee == null) {
return ErrorKinds.NOTFOUND_ERROR;
}

employee.setName(formEmployee.getName());
employee.setRole(formEmployee.getRole());

if (formEmployee.getPassword() != null && !formEmployee.getPassword().isEmpty()) {
ErrorKinds result = employeePasswordCheck(formEmployee);
if (ErrorKinds.CHECK_OK != result) {
 return result;
}
employee.setPassword(formEmployee.getPassword());
}

employee.setUpdatedAt(LocalDateTime.now());
employeeRepository.save(employee);
return ErrorKinds.SUCCESS;
}

public Employee getEmployee(String username) {
return null; // TODO: 実装予定
}
}