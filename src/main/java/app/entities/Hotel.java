package app.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import app.dtos.HotelDto;

@Getter
@NoArgsConstructor
@Entity
public class Hotel
{
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_ADDRESS_LENGTH = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(nullable = false, length = MAX_ADDRESS_LENGTH)
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

        if (hotelDto.name().length() > MAX_NAME_LENGTH)
            return false;

        if (hotelDto.address().length() > MAX_ADDRESS_LENGTH)
            return false;

        return true;
    }

}
