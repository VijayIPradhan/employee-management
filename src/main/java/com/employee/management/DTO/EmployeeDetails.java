package com.employee.management.DTO;

import com.employee.management.models.Employee;
import com.employee.management.models.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeDetails implements UserDetails {
    private final String id;
    private final String password;
    private final List<GrantedAuthority> authorityList;
    private final Status status;
    public EmployeeDetails(Employee employee){
        id=employee.getEmployeeID();
        password= employee.getPassword();
        status=employee.getStatus();
        this.authorityList = employee.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
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
        return status.getName().equals("active");
    }
}
