package bg.softuni.serviceScheduler.vignette.service.impl;

import bg.softuni.serviceScheduler.vignette.dao.VignetteRepository;
import bg.softuni.serviceScheduler.vignette.service.VignetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VignetteServiceImpl implements VignetteService {

    private final VignetteRepository vignetteRepository;

    @Autowired
    public VignetteServiceImpl(VignetteRepository vignetteRepository) {
        this.vignetteRepository = vignetteRepository;
    }

}
