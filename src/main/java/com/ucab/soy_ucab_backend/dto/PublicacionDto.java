package com.ucab.soy_ucab_backend.dto;

import java.time.OffsetDateTime;

public class PublicacionDto {
    private String idPublicacion;
    private String correoAutor;
    private String textoPub;
    private OffsetDateTime fechaHora;
    private int totalLikes;
    private int totalComen;

    public PublicacionDto() {
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getCorreoAutor() {
        return correoAutor;
    }

    public void setCorreoAutor(String correoAutor) {
        this.correoAutor = correoAutor;
    }

    public String getTextoPub() {
        return textoPub;
    }

    public void setTextoPub(String textoPub) {
        this.textoPub = textoPub;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalComen() {
        return totalComen;
    }

    public void setTotalComen(int totalComen) {
        this.totalComen = totalComen;
    }
}
