package com.trixpert.beebbeeb.services.impl;

import com.trixpert.beebbeeb.data.entites.*;
import com.trixpert.beebbeeb.data.mappers.*;
import com.trixpert.beebbeeb.data.repositories.*;
import com.trixpert.beebbeeb.data.request.CarRegistrationRequest;
import com.trixpert.beebbeeb.data.response.FileUploadResponse;
import com.trixpert.beebbeeb.data.response.ResponseWrapper;
import com.trixpert.beebbeeb.data.to.CarDTO;
import com.trixpert.beebbeeb.data.to.PhotoDTO;
import com.trixpert.beebbeeb.exception.NotFoundException;
import com.trixpert.beebbeeb.services.AuditService;
import com.trixpert.beebbeeb.services.CarService;
import com.trixpert.beebbeeb.services.CloudStorageService;
import com.trixpert.beebbeeb.services.ReporterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final ParentColorRepository parentColorRepository;
    private final TypeRepository typeRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final EssentialCarSpecsRepository essentialCarSpecsRepository;

    private final CarMapper carMapper;
    private final PhotoMapper photoMapper;

    private final CloudStorageService cloudStorageService;
    private final ReporterService reporterService;
    private final AuditService auditService;

    public CarServiceImpl(CarRepository carRepository,
                          ModelRepository modelRepository,
                          BrandRepository brandRepository, CategoryRepository categoryRepository,
                          ColorRepository colorRepository,
                          ParentColorRepository parentColorRepository,
                          TypeRepository typeRepository,
                          UserRepository userRepository,
                          PhotoRepository photoRepository,
                          EssentialCarSpecsRepository essentialCarSpecsRepository, CarMapper carMapper,
                          PhotoMapper photoMapper,
                          CloudStorageService cloudStorageService,
                          ReporterService reporterService,
                          AuditService auditService) {

        this.carRepository = carRepository;
        this.modelRepository = modelRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.colorRepository = colorRepository;
        this.parentColorRepository = parentColorRepository;
        this.typeRepository = typeRepository;
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.essentialCarSpecsRepository = essentialCarSpecsRepository;
        this.carMapper = carMapper;
        this.photoMapper = photoMapper;
        this.cloudStorageService = cloudStorageService;
        this.reporterService = reporterService;
        this.auditService = auditService;
    }


    @Override
    public ResponseWrapper<Boolean> registerCars(CarRegistrationRequest carRegistrationRequest, String authHeader) {
        try {

            String username = auditService.getUsernameForAudit(authHeader);

            Optional<UserEntity> optionalUser = userRepository.findByEmail(username);

            if (!optionalUser.isPresent()) {
                throw new NotFoundException("user not found !!");
            }

            Optional<ModelEntity> optionalModelEntity = modelRepository.findById(carRegistrationRequest.getModelId());

            if (!optionalModelEntity.isPresent()) {
                throw new NotFoundException("Model entity not found");
            }

            Optional<TypeEntity> typeEntityOptional = typeRepository.findById(carRegistrationRequest.getTypeId());

            if (!typeEntityOptional.isPresent()) {
                throw new NotFoundException("Type Not Found");
            }

            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .name(carRegistrationRequest.getCategoryName())
                    .build();
            categoryEntity.setType(typeEntityOptional.get());
            categoryRepository.save(categoryEntity);

            ModelEntity modelRecord = optionalModelEntity.get();


            for (CarRegistrationRequest.ColorFormData color : carRegistrationRequest.getColors()) {
                Optional<ParentColorEntity> parentColorOptional = parentColorRepository.findById(color.getParentColor().getId());

                if (!parentColorOptional.isPresent()) {
                    throw new NotFoundException("Parent Color Not Found");
                }

                ColorEntity colorEntity = ColorEntity.builder()
                        .name(color.getColorName())
                        .code(color.getColorCode())
                        .build();

                colorEntity.setParentColor(parentColorOptional.get());
                colorRepository.save(colorEntity);


                CarEntity carEntityRecord = CarEntity.builder()
                        .additionDate(LocalDateTime.now())
                        .model(modelRecord)
                        .brand(modelRecord.getBrand())
                        .creator(optionalUser.get())
                        .category(categoryEntity)
                        .color(colorEntity)
                        .originalPrice(carRegistrationRequest.getOriginalPrice())
                        .active(true)
                        .build();

                CarEntity savedCar = carRepository.save(carEntityRecord);

                String[] specsAr = new String[]{
                        "???????? ????????????",
                        "?????? ????????????????",
                        "??????/?????? ??????????????",
                        "?????? ??????????????",
                        "???????? ????????",
                        "???????? ?????????? ???????????? ??????????????",
                        "?????????? ??????????",
                        "?????????? ??????????",
                        "???????????????? ??????????",
                        "???????????????? ???? ??????????",
                        "?????? ???????? ????????????",
                        "?????? ????????????",
                        "?????? ??????????????",
                        "???????????? ????????????????????",
                        "???????? ????????",
                        "?????????????? ???? 0 ?? 100",
                        "????????????????",
                        "?????? ??????????????",
                        "?????????????? ????????????????",
                        "???????? ?????????????? ???????????? ????????????????-ABS",
                        "???????? ?????????? ?????? ?????????????? EBD",
                        "???????????? ???????? ???????????????? ESP",
                        "???????? ???????????????????? ?????????????? ???? ?????????? ??????????????",
                        "??????????",
                        "???????? ISO Fix ???????????? ?????????? ??????????????",
                        "???????? ?????????????? ?????????????? ????????????????",
                        "???????? ?????????????? ?????????????? ??????????????",
                        "???????? ??????????",
                        "???????? ????????",
                        "???????? ????????",
                        "???????? ??????",
                        " ?????? ????????????????",
                        "???????????? ????????????",
                        "???????????? ???? ????????????????",
                        "?????? ??????????",
                        "???????????? ????????????",
                        "???????? ??????",
                        "????????????????",
                        "???????????? ??????????",
                        "?????? ??????????  ??????????????",
                        "????????????",
                        "?????????? / ?????????? ???????????? ???????? ??????????",
                        "?????????? ???? ????????????????",
                        "???????? ???????? ????????????",
                        "???????? ???????????? ??????????????",
                        "???????? ?????????? ??????????",
                        "?????????????? ??????????",
                        "???????????? ?????? ???????????? ???? ????????????????",
                        "???????? ????????",
                        "GPS",
                        "???????? AUX",
                        "???????? USB",
                        "????????????",
                        "???????? ????????????",
                        "?????????????? ????????",
                        "????????????",
                        "?????????? ??????????????",
                        "?????? ????????"
                };

                for (int i = 0; i < specsAr.length; i++) {
                    EssentialSpecsEntity essentialSpecsEntity = new EssentialSpecsEntity();
                    essentialSpecsEntity.setKey(specsAr[i]);
                    String specsValue = carRegistrationRequest.getSpecs().get(i);
                    if (specsValue != null && !"".equals(specsValue)) {
                        essentialSpecsEntity.setValue(specsValue);
                    } else {
                        essentialSpecsEntity.setValue("N/A");
                    }
                    essentialSpecsEntity.setActive(true);
                    essentialSpecsEntity.setCar(savedCar);
                    essentialCarSpecsRepository.save(essentialSpecsEntity);
                }
            }


            return reporterService.reportSuccess("Cars Added to Category Successfully");
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Transactional
    @Override
    public ResponseWrapper<FileUploadResponse> uploadInterior(long modelId, MultipartFile file) {
        try {
            PhotoDTO photoDTO = uploadImage(true, modelId, file);
            if (photoDTO == null) return reporterService.reportError(new IllegalArgumentException(""));
            return reporterService.reportSuccess(new FileUploadResponse(photoDTO));
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    private PhotoDTO uploadImage(boolean interior, long carId, MultipartFile file) {
        try {
            Optional<CarEntity> optionalCar = carRepository.findById(carId);

            if (!optionalCar.isPresent()) {
                throw new NotFoundException("car not found !");
            }

            CarEntity carEntity = optionalCar.get();

            String mainImage = cloudStorageService.uploadFile(file);

            PhotoEntity photo = photoRepository
                    .save(PhotoEntity.builder()
                            .photoUrl(mainImage)
                            .active(true)
                            .caption(carEntity.getModel().getName())
                            .interior(interior)
                            .build());

            List<PhotoEntity> modelPhotos = carEntity.getPhotos();
            modelPhotos.add(photo);
            carEntity.setPhotos(modelPhotos);
            carRepository.save(carEntity);
            return photoMapper.convertToDTO(photo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @Override
    public ResponseWrapper<FileUploadResponse> uploadExterior(long modelId, MultipartFile file) {
        try {
            PhotoDTO photoDTO = uploadImage(false, modelId, file);
            if (photoDTO == null) return reporterService.reportError(new IllegalArgumentException(""));
            return reporterService.reportSuccess(new FileUploadResponse(photoDTO));
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }


    @Transactional
    @Override
    public ResponseWrapper<Boolean> deleteCar(long carId) {
        try {
            Optional<CarEntity> optionalCarEntity = carRepository.findById(carId);
            if (!optionalCarEntity.isPresent()) {
                throw new NotFoundException("Car entity not found");
            }
            CarEntity carEntityRecord = optionalCarEntity.get();
            carEntityRecord.setActive(false);
            carRepository.save(carEntityRecord);
            return reporterService.reportSuccess("Car with Id: ".concat(Long.toString(carId))
                    .concat(" has been deleted successfully"));
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<List<CarDTO>> getAllCars(boolean active) {
        try {
            List<CarDTO> carDTOList = new ArrayList<>();
            carRepository.findAllByActive(active).forEach(car ->
                    carDTOList.add(carMapper.convertToDTO(car))
            );
            return reporterService.reportSuccess(carDTOList);
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<List<CarDTO>> listCarsForYear(boolean active, String year) {
        try {
            List<CarDTO> carList = new ArrayList<>();
            modelRepository.findAllByActiveAndYear(active, year).forEach(modelEntity -> {
                carRepository.findAllByActiveAndModel(active, modelEntity).forEach(carEntity -> {
                    carList.add(carMapper.convertToDTO(carEntity));
                });
            });
            return reporterService.reportSuccess(carList);
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<List<CarDTO>> listCarsForBrandAndModel(boolean active, long brandId, long modelId) {
        try {
            List<CarDTO> carList = new ArrayList<>();
            Optional<ModelEntity> optionalModelEntityRecord = modelRepository.findById(modelId);
            if (!optionalModelEntityRecord.isPresent()) {
                throw new NotFoundException("Model entity not found");
            }
            Optional<BrandEntity> optionalBrandEntityRecord = brandRepository.findById(brandId);
            if (!optionalBrandEntityRecord.isPresent()) {
                throw new NotFoundException("brand entity not found");
            }
            carRepository.findAllByActiveAndBrandAndModel(active, optionalBrandEntityRecord.get(),
                    optionalModelEntityRecord.get()).forEach(carEntity -> {
                carList.add(carMapper.convertToDTO(carEntity));
            });
            return reporterService.reportSuccess(carList);
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<List<CarDTO>> listCarsForModel(boolean active, long modelId) {
        try {
            List<CarDTO> carList = new ArrayList<>();
            Optional<ModelEntity> optionalModelEntityRecord = modelRepository.findById(modelId);
            if (!optionalModelEntityRecord.isPresent()) {
                throw new NotFoundException("Model entity not found");
            }
            carRepository.findAllByActiveAndModel(active, optionalModelEntityRecord.get())
                    .forEach(carEntity -> {
                        carList.add(carMapper.convertToDTO(carEntity));
                    });
            return reporterService.reportSuccess(carList);
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<List<CarDTO>> listCarsForBrand(boolean active, long brandId) {
        try {
            List<CarDTO> carList = new ArrayList<>();
            Optional<BrandEntity> optionalBrandEntityRecord = brandRepository.findById(brandId);
            if (!optionalBrandEntityRecord.isPresent()) {
                throw new NotFoundException("brand entity not found");
            }
            carRepository.findAllByActiveAndBrand(active, optionalBrandEntityRecord.get())
                    .forEach(carEntity -> {
                        carList.add(carMapper.convertToDTO(carEntity));
                    });
            return reporterService.reportSuccess(carList);
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }

    @Override
    public ResponseWrapper<CarDTO> getCar(long carId) {
        try {
            Optional<CarEntity> optionalCarEntity = carRepository.findById(carId);
            if (!optionalCarEntity.isPresent()) {
                throw new NotFoundException("This car not found");
            }
            CarEntity carEntityRecord = optionalCarEntity.get();
            return reporterService.reportSuccess(carMapper.convertToDTO(carEntityRecord));

        } catch (Exception e) {
            return reporterService.reportError(e);
        }

    }

    @Override
    public ResponseWrapper<Boolean> updateCar(long carId, CarRegistrationRequest carRegistrationRequest) {
        try {
            Optional<CarEntity> carEntityOptional = carRepository.findById(carId);
            if (!carEntityOptional.isPresent()) {
                throw new NotFoundException("Car with ID : ".concat(Long.toString(carId)).concat(" Not Exist !"));
            }
            CarEntity carEntityRecord = carEntityOptional.get();

            if (carRegistrationRequest.getCategoryName() != carEntityRecord.getCategory().getName()) {
                Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(carEntityRecord.getCategory().getId());
                if (!categoryEntityOptional.isPresent()) {
                    throw new NotFoundException("Category with ID : ".concat(Long.toString(carEntityRecord.getCategory().getId())).concat(" Not Exist !"));
                }
                CategoryEntity categoryEntity = categoryEntityOptional.get();
                categoryEntity.setName(carRegistrationRequest.getCategoryName());
                categoryRepository.save(categoryEntity);
            }

            if (!carRegistrationRequest.getColorName().equals(carEntityRecord.getColor().getName())) {
                Optional<ColorEntity> colorEntityOptional = colorRepository.findById(carEntityRecord.getColor().getId());
                if (!colorEntityOptional.isPresent()) {
                    throw new NotFoundException("Color with ID : ".concat(Long.toString(carEntityRecord.getColor().getId())).concat(" Not Exist !"));
                }
                ColorEntity colorEntityRecord = colorEntityOptional.get();
                colorEntityRecord.setName(carRegistrationRequest.getColorName());
                colorRepository.save(colorEntityRecord);
            }
            if (carRegistrationRequest.getParentColorId() != carEntityRecord.getColor().getParentColor().getId()) {

                Optional<ColorEntity> colorEntityOptional = colorRepository.findById(carEntityRecord.getColor().getId());
                if (!colorEntityOptional.isPresent()) {
                    throw new NotFoundException("Parent Color with ID : ".concat(Long.toString(carRegistrationRequest.getParentColorId())).concat(" Not Exist !"));
                }
                ColorEntity colorEntityRecord = colorEntityOptional.get();
                Optional<ParentColorEntity> parentColorEntityOptional = parentColorRepository.findById(carRegistrationRequest.getParentColorId());
                if (!parentColorEntityOptional.isPresent()) {
                    throw new NotFoundException("Color with ID : ".concat(Long.toString(carEntityRecord.getColor().getId())).concat(" Not Exist !"));
                }
                colorEntityRecord.setParentColor(parentColorEntityOptional.get());
                colorRepository.save(colorEntityRecord);
            }

            if (!carRegistrationRequest.getColorCode().equals(carEntityRecord.getColor().getCode())) {
                Optional<ColorEntity> colorEntityOptional = colorRepository.findById(carEntityRecord.getColor().getId());
                if (!colorEntityOptional.isPresent()) {
                    throw new NotFoundException("Color with ID : ".concat(Long.toString(carEntityRecord.getColor().getId())).concat(" Not Exist !"));
                }
                ColorEntity colorEntityRecord = colorEntityOptional.get();
                colorEntityRecord.setCode(carRegistrationRequest.getColorCode());
                colorRepository.save(colorEntityRecord);
            }
            if (carRegistrationRequest.getModelId() != carEntityRecord.getModel().getId()) {
                Optional<ModelEntity> modelEntityOptional = modelRepository.findById(carRegistrationRequest.getModelId());
                if (!modelEntityOptional.isPresent()) {
                    throw new NotFoundException("Model with ID : ".concat(Long.toString(carRegistrationRequest.getModelId())).concat(" Not Exist !"));
                }
                ModelEntity modelEntity = modelEntityOptional.get();
                carEntityRecord.setModel(modelEntity);

            }
            if (carRegistrationRequest.getTypeId() != carEntityRecord.getCategory().getType().getId()) {
                Optional<TypeEntity> typeEntityOptional = typeRepository.findById(carRegistrationRequest.getTypeId());
                if (!typeEntityOptional.isPresent()) {
                    throw new NotFoundException("Type with ID : ".concat(Long.toString(carRegistrationRequest.getTypeId())).concat(" Not Exist !"));
                }
                TypeEntity typeEntity = typeEntityOptional.get();

                Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(carEntityRecord.getCategory().getId());
                if (!categoryEntityOptional.isPresent()) {
                    throw new NotFoundException("Category with ID : ".concat(Long.toString(carEntityRecord.getCategory().getId())).concat(" Not Exist !"));
                }
                CategoryEntity categoryEntity = categoryEntityOptional.get();
                categoryEntity.setType(typeEntity);
                categoryRepository.save(categoryEntity);
                carEntityRecord.setCategory(categoryEntity);
            }
            carRepository.save(carEntityRecord);
            return reporterService.reportSuccess("Car With ID : ".concat(Long.toString(carId)).concat(" has been updated Successfully !"));
        } catch (Exception e) {
            return reporterService.reportError(e);
        }
    }
}
