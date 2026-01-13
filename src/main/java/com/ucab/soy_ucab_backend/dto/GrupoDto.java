package com.ucab.soy_ucab_backend.dto;

import com.ucab.soy_ucab_backend.model.TipoGrupo;
import java.time.LocalDate;

public class GrupoDto {
    @com.fasterxml.jackson.annotation.JsonProperty("nombre_grupo")
    private String nombreGrupo;
    @com.fasterxml.jackson.annotation.JsonProperty("descripcion")
    private String description;
    @com.fasterxml.jackson.annotation.JsonProperty("tipo_grupo")
    private TipoGrupo tipoGrupo;
    @com.fasterxml.jackson.annotation.JsonProperty("fecha_creacion")
    private LocalDate fechaCreacion;
    @com.fasterxml.jackson.annotation.JsonProperty("correo_creador")
    private String correoCreador;
    @com.fasterxml.jackson.annotation.JsonProperty("isCreator")
    private boolean isCreator; // Helper for frontend
    @com.fasterxml.jackson.annotation.JsonProperty("member_count")
    private int memberCount;
    @com.fasterxml.jackson.annotation.JsonProperty("posts")
    private java.util.List<PublicacionDto> posts;

    public GrupoDto() {
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getCorreoCreador() {
        return correoCreador;
    }

    public void setCorreoCreador(String correoCreador) {
        this.correoCreador = correoCreador;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public java.util.List<PublicacionDto> getPosts() {
        return posts;
    }

    public void setPosts(java.util.List<PublicacionDto> posts) {
        this.posts = posts;
    }
}
