package app.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class Room
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @ManyToOne
    private Hotel hotel;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "price_per_night_dkk")
    private Double pricePerNightDkk;

    public Room(String roomNumber, Double pricePerNightDkk)
    {
        this.roomNumber = roomNumber;
        this.pricePerNightDkk = pricePerNightDkk;
    }
}
