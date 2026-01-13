package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.model.Evento;
import com.ucab.soy_ucab_backend.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> findById(String nombreEvento) {
        return eventoRepository.findById(nombreEvento);
    }

    public Evento save(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void deleteById(String nombreEvento) {
        eventoRepository.deleteById(nombreEvento);
    }
}
