package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 日付
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "report_date", nullable = false)
    @NotNull(message = "値を入力してください")
    private LocalDate reportDate;

    // タイトル
    @Column(length = 100, nullable = false)
    @NotEmpty(message = "値を入力してください")
    @Length(max = 100, message = "100文字以下で入力してください")
    private String title;

    // 内容
    @Column(length = 600, nullable = false)
    @NotEmpty(message = "値を入力してください")
    @Length(max = 600, message = "600文字以下で入力してください")
    private String content;

    // 従業員（社員番号で紐付け）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // 削除フラグ(論理削除)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}