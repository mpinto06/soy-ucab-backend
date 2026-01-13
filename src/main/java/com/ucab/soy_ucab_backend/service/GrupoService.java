package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.model.Grupo;
import com.ucab.soy_ucab_backend.model.Pertenece;
import com.ucab.soy_ucab_backend.repository.GrupoRepository;
import com.ucab.soy_ucab_backend.repository.PerteneceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {
    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PerteneceRepository perteneceRepository;

    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    public Optional<Grupo> findById(String id) {
        return grupoRepository.findById(id);
    }

    @Transactional
    public Grupo save(Grupo grupo) {
        boolean isNew = grupoRepository.findById(grupo.getNombreGrupo()).isEmpty();
        Grupo saved = grupoRepository.save(grupo);

        if (isNew) {
            Pertenece member = new Pertenece();
            member.setNombreGrupo(saved.getNombreGrupo());
            member.setCorreoMiembro(saved.getCreador().getEmail());
            member.setRol("administrador");
            member.setFechaIngreso(java.time.LocalDate.now());
            perteneceRepository.save(member);
        }

        return saved;
    }

    public long getMemberCount(String nombreGrupo) {
        return perteneceRepository.countByNombreGrupo(nombreGrupo);
    }

    @Transactional
    public void deleteById(String id) {
        perteneceRepository.deleteByNombreGrupo(id);
        grupoRepository.deleteById(id);
    }
}
