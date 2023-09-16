package ru.practicum.service.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Length;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    Category category;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(nullable = false)
    @NotBlank
    @Length(min = 20, max = 7000)
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    Location location;
    @Column(nullable = false)
    Boolean paid;
    @Column(name = "participant_limit")
    Long participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Column(nullable = false)
    @Length(min = 3, max = 120)
    String title;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EventState state;
}
