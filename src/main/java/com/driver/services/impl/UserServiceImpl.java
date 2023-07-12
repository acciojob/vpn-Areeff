package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        //create a user of given country. The originalIp of the user should be "countryCode.userId" and return the user.
        // Note that right now user is not connected and thus connected would be false and maskedIp would be null
        //Note that the userId is created automatically by the repository layer
        String newCountryName=countryName.toUpperCase();
        User user=new User();
        Country country=new Country();
        if(newCountryName.equals("IND")){
            country.setCountryName(CountryName.IND);
            country.setCode(CountryName.IND.toCode());
        }
        else if(newCountryName.equals("USA")){
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }
        else if(newCountryName.equals("AUS")){
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        }
        else if(newCountryName.equals("CHI")){
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        }
        else if(newCountryName.equals("JPN")){
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }
        else{
            throw new Exception("Country not found");
        }
        country.setUser(user);
        user.setOriginalCountry(country);
        user.setPassword(password);
        user.setUsername(username);
        user.setConnected(false);
        //user.setConnectionList(new ArrayList<>());
        //user.setServiceProviderList(new ArrayList<>());
        user.setMaskedIp(null);
        User user1=userRepository3.save(user);
        String Ip=country.getCode()+"."+user1.getId();
        user.setOriginalIp(Ip);
        user1=userRepository3.save(user);
        return user1;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user=userRepository3.findById(userId).get();
        ServiceProvider serviceProvider=serviceProviderRepository3.findById(serviceProviderId).get();
        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);
        serviceProviderRepository3.save(serviceProvider);
        return user;


    }
}
