package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        countryName=countryName.toUpperCase();
        User user=new User();
       // CountryName UserOfgivenCountry=null;
        Country country=new Country();
        if(countryName.equals("IND")){
           country.setCountryName(CountryName.IND);
           country.setCode(CountryName.IND.toCode());
        }
        else if(countryName.equals("USA")){
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }
        else if(countryName.equals("AUS")){
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        }
        else if(countryName.equals("CHI")){
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        }
        else if(countryName.equals("JPN")) {
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }
        else{
            throw new Exception("Country not found");
        }
        user.setOriginalCountry(country);
        user.setPassword(password);
        user.setUsername(username);
        user.setConnected(false);
        user.setMaskedIp(null);
        user.setOriginalIp(country.getCode()+"."+userRepository3.save(user).getId());
        country.setUser(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
              return null;
    }
}
