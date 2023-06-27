package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        //Connect the user to a vpn by considering the following priority order.
        //1. If the user is already connected to any service provider, throw "Already connected" exception.
        //2. Else if the countryName corresponds to the original country of the user, do nothing. This means
        // that the user wants to connect to its original country, for which we do not require a connection.
        // Thus, return the user as it is.
        //3. Else, the user should be subscribed under a serviceProvider having option to connect to the given
        // country.
        //If the connection can not be made (As user does not have a serviceProvider or serviceProvider
        // does not have given country, throw "Unable to connect" exception.
        //Else, establish the connection where the maskedIp is "updatedCountryCode.serviceProviderId.userId"
        // and return the updated user. If multiple service providers allow you to connect to the country,
        // use the service provider having smallest id.
        User user=userRepository2.findById(userId).get();
        if(user.getConnected()){
            throw new Exception("Already connected");
        }

        Country country=new Country();
        String newCountryName=countryName.toUpperCase();
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
        if(user.getOriginalCountry().getCountryName().equals(country.getCountryName())){
            return user;
        }

        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        ServiceProvider serviceProviderWithLowestId=null;
        Integer lowestId=0;
        for(ServiceProvider serviceProvider:serviceProviderList){
            List<Country>countryList=serviceProvider.getCountryList();
            for(Country country1:countryList){
                if(country1.getCode().equals(country.getCode())){
                    if(serviceProviderWithLowestId==null||lowestId>serviceProvider.getId()){
                        serviceProviderWithLowestId=serviceProvider;
                        lowestId=serviceProviderWithLowestId.getId();
                    }
                }
            }
        }

        if(serviceProviderWithLowestId==null){
            throw new Exception("Unable to connect");
        }
        //Else, establish the connection where the maskedIp is "updatedCountryCode.serviceProviderId.userId"
        // and return the updated user. If multiple service providers allow you to connect to the country,
        // use the service provider having smallest id.
        user.setConnected(true);
        user.setMaskedIp(new String(country.getCode()+"."+serviceProviderWithLowestId.getId()+"."+user.getId()));
        Connection connection=new Connection();
        connection.setUser(user);
        connection.setServiceProvider(serviceProviderWithLowestId);
        connectionRepository2.save(connection);
        user.getConnectionList().add(connection);
        userRepository2.save(user);
        serviceProviderWithLowestId.getConnectionList().add(connection);
        serviceProviderRepository2.save(serviceProviderWithLowestId);

        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user=userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        user.setMaskedIp(null);
        user.setConnected(false);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User receiver=userRepository2.findById(receiverId).get();
        CountryName currentReceiverCountry=null;
        if(receiver.getConnected()){
            String maskedCode=receiver.getMaskedIp().substring(0,3);
            if(maskedCode.equals("001")){
                currentReceiverCountry=CountryName.IND;
            }
            else if(maskedCode.equals("005")){
                currentReceiverCountry=CountryName.JPN;
            }
            else if(maskedCode.equals("004")){
                currentReceiverCountry=CountryName.CHI;
            }
            else if(maskedCode.equals("003")){
                currentReceiverCountry=CountryName.AUS;
            }
            else if(maskedCode.equals("002")){
                currentReceiverCountry=CountryName.USA;
            }
        }
        else{
            currentReceiverCountry=receiver.getOriginalCountry().getCountryName();
        }
        User user=null;
        try{
            user=connect(senderId,currentReceiverCountry.toString());
        }catch (Exception e){
            throw new Exception("Cannot establish communication");
        }
        return user;
    }
}
