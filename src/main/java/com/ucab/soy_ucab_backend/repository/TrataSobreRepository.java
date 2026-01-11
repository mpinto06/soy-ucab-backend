package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.TrataSobre;
import com.ucab.soy_ucab_backend.model.TrataSobreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrataSobreRepository extends JpaRepository<TrataSobre, TrataSobreId> {
}
