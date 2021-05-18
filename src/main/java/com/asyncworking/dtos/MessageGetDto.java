package com.asyncworking.dtos;

import com.asyncworking.models.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageGetDto {
    private Long id;

    private String messageTitle;

    private Long posterUserId;

    private String posterUser;

    private String content;

    private Category category;

    private String docURL;

    private OffsetDateTime postTime;

}
