package aoki.restaurantes.service;

import aoki.restaurantes.domain.UserType;
import aoki.restaurantes.exception.ConflictException;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.UserTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserTypeService {
    private final UserTypeRepository repository;

    public UserTypeService(UserTypeRepository repository){
        this.repository = repository;
    }

    @Transactional
    public UserType create(String name) {
        if(repository.existsByNameIgnoreCase(name)){
            throw new ConflictException("Tipo de usuário já existe.");
        }
        var userType = new UserType();
        userType.setName(name.trim());
        return repository.save(userType);
    }

    public UserType findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Tipo de usuário não encontrado"));
    }
    public List<UserType> findAll() {
        return repository.findAll();
    }

    @Transactional
    public UserType update(UUID id, String name) {
        var userType = findById(id);
        if(!userType.getName().equalsIgnoreCase(name) && repository.existsByNameIgnoreCase(name)) {
            throw  new ConflictException("Tipo de usuário já existe.");
        }
        userType.setName(name.trim());
        return repository.save(userType);
    }

    @Transactional
    public void delete(UUID id){
        repository.delete(findById(id));
    }


}
