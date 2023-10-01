package ru.practicum.service.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.service.CommonConstants;
import ru.practicum.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    String text;
    UserShortDto author;
    Long eventId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATE_FORMAT)
    LocalDateTime created;
}
