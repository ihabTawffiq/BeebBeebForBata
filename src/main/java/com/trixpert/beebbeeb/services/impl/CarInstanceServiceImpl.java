package com.trixpert.beebbeeb.services.impl;

import com.trixpert.beebbeeb.data.entites.*;
import com.trixpert.beebbeeb.data.mappers.CarInstanceMapper;
import com.trixpert.beebbeeb.data.repositories.*;
import com.trixpert.beebbeeb.data.request.CarInstanceRequest;
import com.trixpert.beebbeeb.data.response.ResponseWrapper;
import com.trixpert.beebbeeb.data.to.CarInstanceDTO;
import com.trixpert.beebbeeb.exception.NotFoundException;
import com.trixpert.beebbeeb.services.AuditService;
import com.trixpert.beebbeeb.services.CarInstanceService;
import com.trixpert.beebbeeb.services.ReporterService;
import com.trixpert.beebbeeb.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarInstanceServiceImpl implements CarInstanceService {
    private final AuditService auditService;
    private final PriceRepository priceRepository;
    private final CarInstanceRepository carInstanceRepository;
    private final CarInstanceMapper carInstanceMapper;
    private final VendorRepository vendorRepository;
    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final UserService userService;
    private final ReporterService reporterService;


    public CarInstanceServiceImpl(AuditService auditService,
                                  PriceRepository priceRepository,
                                  CarInstanceRepository carInstanceRepository,
                                  CarInstanceMapper carInstanceMapper,
                                  VendorRepository vendorRepository,
                                  BranchRepository branchRepository,
                                  CarRepository carRepository,
                                  UserService userService,
                                  ReporterService reporterService) {

        this.auditService = auditService;
        this.priceRepository = priceRepository;
        this.carInstanceRepository = carInstanceRepository;
        this.carInstanceMapper = carInstanceMapper;
        this.vendorRepository = vendorRepository;
        this.branchRepository = branchRepository;
        this.carRepository = carRepository;
        this.userService = userService;
        this.reporterService = reporterService;
    }


    @Transactional
    @Override
    public ResponseWrapper<Boolean> addCarInstance(CarInstanceRequest carInstanceRequest) {

        try {
            Optional<VendorEntity> optionalVendorEntity = vendorRepository.findById(carInstanceRequest.getVendorId());
            if (!optionalVendorEntity.isPresent()) {
                throw new NotFoundException("This Vendor doesn't exits !");
            }
            Optional<BranchEntity> optionalBranchEntity = branchRepository.findById(carInstanceRequest.getBranchId());
            if (!optionalBranchEntity.isPresent()) {
                throw new NotFoundException("This Branch doesn't exits !");
            }
            Optional<CarEntity> optionalCarEntity = carRepository.findById(carInstanceRequest.getCarId());
            if (!optionalCarEntity.isPresent()) {
                throw new NotFoundException("This Car doesn't exits !");
            }


            CarInstanceEntity carInstanceEntity = carInstanceRepository.save(CarInstanceEntity.builder()
                    .car(optionalCarEntity.get())
                    .vendor(optionalVendorEntity.get())
                    .branch(optionalBranchEntity.get())
                    .originalPrice(carInstanceRequest.getOriginalPrice())
                    .active(true)
                    .build());


           priceRepository.save(PriceEntity.builder()
                    .amount(carInstanceRequest.getVendorPrice())
                    .car(carInstanceEntity)
                    .active(true)
                    .date(LocalDate.now())
                    .build());


            return reporterService.reportSuccess("Car Instance Registered Successfully");

        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<List<CarInstanceDTO>> getALLCarInstances(boolean active) {
        try {
            List<CarInstanceDTO> carInstanceDTOList = new ArrayList<>();

            carInstanceRepository.findAllByActive(active).forEach(carInstance -> {
                carInstanceDTOList.add(carInstanceMapper.convertToDTO(carInstance));
            });
            return reporterService.reportSuccess(carInstanceDTOList);
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<List<CarInstanceDTO>> getAllCarInstancesForVendor(long vendorId, boolean active) {
        try {
            List<CarInstanceDTO> carInstanceDTOForVendorList = new ArrayList<>();

            Optional<VendorEntity> optionalVendorEntityRecord = vendorRepository.findById(vendorId);
            if (!optionalVendorEntityRecord.isPresent()) {
                throw new NotFoundException("Vendor Entity not found");
            }
            carInstanceRepository.findAllByVendorAndActive(optionalVendorEntityRecord.get(), active).forEach(carInstance -> {
                carInstanceDTOForVendorList.add(carInstanceMapper.convertToDTO(carInstance));
            });

            return reporterService.reportSuccess(carInstanceDTOForVendorList);
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<Boolean> deleteCarInstance(long carIstanceId) {
        try {
            Optional<CarInstanceEntity> carInstanceEntityOptional = carInstanceRepository.findById(carIstanceId);
            if (!carInstanceEntityOptional.isPresent()) {
                throw new NotFoundException("Car Instance with id : ".concat(Long.toString(carIstanceId)).concat("not Exist"));
            }
            CarInstanceEntity carEntityRecord = carInstanceEntityOptional.get();
            carEntityRecord.setActive(false);
            carInstanceRepository.save(carEntityRecord);
            return reporterService.reportSuccess("Car Instance with id : ".concat(Long.toString(carIstanceId)).concat("deleted successfully"));
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }


}
