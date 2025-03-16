package app.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import app.dtos.HotelDto;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class Hotel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    Set<Room> rooms = new HashSet<>();

    public void addRoom(Room room)
    {
        if (room != null)
        {
            this.rooms.add(room);
            room.setHotel(this);
        }
    }

    // This constructor ignores id in hotelDto
    public Hotel(HotelDto hotelDto)
    {
        this.name = hotelDto.name();
        this.address = hotelDto.address();
    }

    public Hotel(int id, HotelDto hotelDto)
    {
        this.id = id;
        this.name = hotelDto.name();
        this.address = hotelDto.address();
    }


    public static boolean isHotelDtoValid(HotelDto hotelDto)
    {
        if (hotelDto.name() == null)
            return false;

        if (hotelDto.address() == null)
            return false;

        if (hotelDto.name().length() > 255)
            return false;

        if (hotelDto.address().length() > 255)
            return false;

        return true;
    }

}
