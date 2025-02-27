package bg.softuni.serviceScheduler.user.model;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private LocalDateTime registrationDate;
    @Column(nullable = false)
    private String profilePictureURL;
    @OneToMany(mappedBy = "user")
    private List<Car> cars;

}
