package com.carbid.demo.service;

import com.carbid.demo.dto.CarDto;
import com.carbid.demo.model.Car;
import com.carbid.demo.model.CarImage;
import com.carbid.demo.repo.ICar;
import com.carbid.demo.repo.ICarImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);


    @Autowired
    ICar carRepo;

    @Autowired
    ICarImage imageRepo;

    @Autowired
    private S3Service s3Service;

    public Car addCar(CarDto carDto) {

        Car car = new Car();
        car.setCarName(carDto.getCarName());
        car.setFuelType(carDto.getFuelType());
        car.setLocation(carDto.getLocation());
        car.setRcType(carDto.getRcType());
        car.setAuctionEndTime(carDto.getAuctionEndTime());
        car.setVisible(carDto.isVisible());
        car.setModelNumber(carDto.getModelNumber());
        car.setOwnerNumber(carDto.getOwnerNumber());
        car.setTransmissionType(carDto.getTransmissionType());
        car.setVehicleDetail(carDto.getVehicleDetail());
        car.setRegistrationNumber(carDto.getRegistrationNumber());
        return carRepo.save(car);

    }

    public String uploadImage(List<MultipartFile> files, Long id){

        for (MultipartFile file : files){
            s3Service.ImageUploader(file, id);
        }
        return "Images uploaded successfully";
    }




    public List<CarDto> allCar() {

        List<Car> cars = carRepo.findAll();

        List<CarDto> carDtos = cars.stream()
                .map(car -> {
                    CarDto carDto = new CarDto();
                    carDto.setCarName(car.getCarName());
                    carDto.setVisible(car.isVisible());
                    carDto.setLocation(car.getLocation());
                    carDto.setFuelType(car.getFuelType());
                    carDto.setModelNumber(car.getModelNumber());
                    carDto.setOwnerNumber(car.getOwnerNumber());
                    carDto.setRcType(car.getRcType());
                    carDto.setRegistrationNumber(car.getRegistrationNumber());
                    carDto.setAuctionEndTime(car.getAuctionEndTime());
                    carDto.setVehicleDetail(car.getVehicleDetail());
                    carDto.setTransmissionType(car.getTransmissionType());
                    carDto.setId(car.getId());

                    List<String> imageUrls = car.getCarImages().stream()
                            .map(carImage -> s3Service.getUnSignedUrl(carImage.getSavedName()))
                            .collect(Collectors.toList());
                    carDto.setImageUrls(imageUrls);
                    return carDto;
                }).collect(Collectors.toList());

        return carDtos;



    }

    public String updateVisibility(boolean value, Long id) {
        Car car = carRepo.findById(id).orElseThrow();
        car.setVisible(value);
        carRepo.save(car);
        return "update successfully";
    }
}
