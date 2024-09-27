package com.basketballticketsproject.basketballticketsproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    private String nombre;

    private String apellidos;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(name = "partidosAsistidos")
    private int partidosAsistidos = 0;

    private boolean is_admin = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "usuario", cascade = {
            CascadeType.ALL
    })
    private Set<Ticket> tickets;

    @Override
    public String toString() {
        return "Usuario {" +
                "id = " + user_id + ", nombre = " + nombre + ", email = " + email + ", is_admin = " + is_admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles(is_admin());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    private List<GrantedAuthority> getRoles(Boolean isAdmin){
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        if(isAdmin){
            grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");
        }
        return grantedAuthorities;
    }
}
