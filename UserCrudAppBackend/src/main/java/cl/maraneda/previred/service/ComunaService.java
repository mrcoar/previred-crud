package cl.maraneda.previred.service;

import cl.maraneda.previred.dto.ComunaDto;
import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.repository.ComunaRepository;
import cl.maraneda.previred.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComunaService {
    private transient final ComunaRepository comunaRepository;
    private transient final ModelMapper modelMapper;

    @Autowired
    public ComunaService(ComunaRepository repository, ModelMapper mapper){
        comunaRepository = repository;
        modelMapper = mapper;
    }

    public List<ComunaDto> findByRegion(String region){
        return comunaRepository.findByRegion(region)
                               .stream()
                               .map(c -> modelMapper.map(c, ComunaDto.class))
                               .collect(Collectors.toList());
    }

    public Comuna findOne(Integer id){
        return comunaRepository.findById(id).orElseThrow(Util::throwUnexistingCommunaException);
    }

    public String getRegionId(Integer id){
        return comunaRepository.findRegionId(id);
    }
}
