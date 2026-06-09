package com.chaithanya.redirect_service.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "redirect_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedirectStats
{
    @Id
    private String shortCode;

    private Long clickCount;
}