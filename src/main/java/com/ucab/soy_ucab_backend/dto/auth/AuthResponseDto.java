package com.ucab.soy_ucab_backend.dto.auth;

import lombok.Data;
import com.ucab.soy_ucab_backend.model.PeriodoEducativo;
import com.ucab.soy_ucab_backend.model.PeriodoExperiencia;
import java.util.List;

@Data
public class AuthResponseDto {
    private String email;
    private String role;
    private String memberType;
    private Object memberDetails;
    private long followersCount;
    private Long friendsCount; // Optional for non-Persona
    @com.fasterxml.jackson.annotation.JsonProperty("isFollowing")
    private boolean isFollowing;
    private String profileImageBase64;
    private String profileHeader;
    private List<PeriodoEducativo> academicPeriods;
    private List<PeriodoExperiencia> professionalPeriods;
    private String location;
    private List<String> interests;
    private List<GroupDto> groups;

    @Data
    public static class GroupDto {
        private String name;
        private String description;
        private String type;

        public GroupDto(String name, String description, String type) {
            this.name = name;
            this.description = description;
            this.type = type;
        }
    }

    public AuthResponseDto(String email, String role, String memberType, Object memberDetails, long followersCount,
            Long friendsCount, String profileImageBase64, String profileHeader, List<PeriodoEducativo> academicPeriods,
            List<PeriodoExperiencia> professionalPeriods, String location, List<String> interests) {
        this.email = email;
        this.role = role;
        this.memberType = memberType;
        this.memberDetails = memberDetails;
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
        this.profileImageBase64 = profileImageBase64;
        this.profileHeader = profileHeader;
        this.academicPeriods = academicPeriods;
        this.professionalPeriods = professionalPeriods;
        this.location = location;
        this.interests = interests;
    }
}
