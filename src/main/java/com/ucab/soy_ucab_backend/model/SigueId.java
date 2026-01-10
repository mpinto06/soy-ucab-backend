package com.ucab.soy_ucab_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigueId implements Serializable {
    private String followerEmail;
    private String followedEmail;
}
